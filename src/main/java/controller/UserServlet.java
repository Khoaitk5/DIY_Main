package controller;

import dao.UserDao;
import service.AddressService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.Address;
import model.Cart;
import model.CartItem;
import model.User;
import model.UserCoupon;
import service.CartService;
import service.CouponService;
import service.UserCouponService;
import service.UserService;

@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {

    CartService cartService = new CartService();
    private UserCouponService userCouponService = new UserCouponService();
    private CouponService couponService = new CouponService();
    private UserService userService = new UserService();
    // Dùng instance, không gọi static!
    private AddressService addressService = new AddressService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String action = request.getParameter("action");
        User user = null;
        try {
            user = userService.getUserById(sessionUser.getId());
        } catch (SQLException ex) {
            Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user != null) {
            int userId = user.getId();
            Cart cart = cartService.getCartByUserId(userId);
            List<CartItem> recentItems = new ArrayList<>();
            if (cart != null && cart.getItems() != null) {
                List<CartItem> allItems = cart.getItems();
                Collections.reverse(allItems);
                recentItems = allItems.stream().limit(5).collect(Collectors.toList());
            }
            request.setAttribute("recentCartItems", recentItems);
            request.setAttribute("cartCount", cart != null && cart.getItems() != null
                    ? cart.getItems().stream().mapToInt(CartItem::getQuantity).sum()
                    : 0);
            List<UserCoupon> userCoupons = userCouponService.getUserCoupons(userId);
            request.setAttribute("userCoupons", userCoupons);
            // Trong Servlet xử lý user
            request.setAttribute("couponService", couponService);
        }

        if ("edit".equals(action)) {
            request.getRequestDispatcher("/user/editProfile.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/user/user.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int id = sessionUser.getId();
        String username = sessionUser.getUsername();
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String role = sessionUser.getRole();
        boolean status = sessionUser.isStatus();
        String currentPassword = sessionUser.getPassword();
        String password = request.getParameter("password");
        String repassword = request.getParameter("repassword");
        String finalPassword = currentPassword;

        // Validate password nếu có đổi
        if (password != null && !password.isEmpty()) {
            if (!password.equals(repassword)) {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                request.setAttribute("user", sessionUser);
                request.getRequestDispatcher("/user/editProfile.jsp").forward(request, response);
                return;
            } else {
                finalPassword = password;
            }
        }
        
        // 1. Xử lý xóa địa chỉ (nếu có)
        String[] deleteAddressIds = request.getParameterValues("deleteAddressId");
        if (deleteAddressIds != null) {
    for (String delId : deleteAddressIds) {
        try {
            userService.deleteUserAddressLink(id, Integer.parseInt(delId));// Chỉ xóa liên kết
            // KHÔNG xóa bảng Address
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

        // 2. Xử lý danh sách địa chỉ còn lại (update & insert)
        List<Address> addressList = new ArrayList<>();
        int i = 0;
        while (true) {
            String street = request.getParameter("street" + i);
            if (street == null) break;
            String district = request.getParameter("district" + i);
            String city = request.getParameter("city" + i);
            String addrIdStr = request.getParameter("addressId" + i);

            Address addr = new Address();
            addr.setStreet(street);
            addr.setDistrict(district != null ? district : "");
            addr.setCity(city != null ? city : "");

            if (addrIdStr != null && !addrIdStr.isEmpty()) {
                // Địa chỉ cũ: update
                addr.setId(Integer.parseInt(addrIdStr));
                addressService.updateAddress(addr);
            } else {
                // Địa chỉ mới: insert
                int newId = addressService.addAddress(addr);
                if (newId > 0) {
                    addr.setId(newId);
                    try {
                        UserDao.linkUserToAddress(id, newId);
                    } catch (SQLException ex) {
                        Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            addressList.add(addr);
            i++;
        }

        User updatedUser = new User(id, username, name, email, role, status, addressList, finalPassword);

        boolean success = false;
        try {
            success = userService.updateUser(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            request.getSession().setAttribute("user", updatedUser);
            request.setAttribute("success", "Update profile successfully!");
        } else {
            request.setAttribute("error", "Update failed. Please try again.");
        }
        request.setAttribute("user", updatedUser);
        request.getRequestDispatcher("/user/editProfile.jsp").forward(request, response);
    }
}
