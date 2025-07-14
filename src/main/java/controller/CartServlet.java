/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cart;
import model.CartItem;
import model.Product;
import model.User;
import model.UserCoupon;
import service.CartService;
import service.ProductService;
import service.UserCouponService;

/**
 *
 * @author HP
 */
@WebServlet(name = "cartServlet", urlPatterns = {"/carts"})
public class CartServlet extends HttpServlet {

    CartService cartService = new CartService();
    UserCouponService userCouponService = new UserCouponService();
    ProductService productService = new ProductService();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet cartServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet cartServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "view": {
                viewCart(request, response);
            }
            break;
            case "edit": {
            }
            break;
            case "details": {

            }
            break;

        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "remove": {
                removeCartItems(request, response);
            }
            break;
            case "update": {
                updateQuantity(request, response);
            }
            break;
            case "insert": {
            try {
                addToCart(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            break;

        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void viewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy user từ session
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/"); // hoặc trang thông báo chưa đăng nhập
            return;
        }

        // 2. Lấy cart theo userId
        int userId = user.getId();

        List<UserCoupon> userCoupons = userCouponService.getUserCoupons(userId);
        if (userCoupons.isEmpty()) {
            userCouponService.assignCoupon(userId, "WELCOME20");
            userCoupons = userCouponService.getUserCoupons(userId); // load lại
        }
        Cart cart = cartService.getCartByUserId(userId);

        // 3. Lấy danh sách item trong cart
        List<CartItem> cartItems = cart.getItems(); // List<CartItem> gồm {Product, quantity}

        // 4. Tính tổng tiền
        double total = cartService.calculateCartTotal(userId);

        // 5. Truyền dữ liệu sang cart.jsp
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("total", total);
        request.setAttribute("userCoupons", userCoupons);

        // 6. Chuyển tiếp sang JSP
        request.getRequestDispatcher("Cart/cart.jsp").forward(request, response);
    }

    private void removeCartItems(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy user từ session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login/login.jsp"); // Nếu chưa đăng nhập thì quay về login
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // Lấy productId từ request
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null && !productIdParam.isEmpty()) {
            try {
                int productId = Integer.parseInt(productIdParam);

                // Gọi service để xoá item
                CartService cartService = new CartService();
                cartService.removeItem(userId, productId);

                // Sau khi xoá, chuyển hướng về lại trang giỏ hàng
                response.sendRedirect("carts?action=view");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
        }
    }

    private void updateQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy user từ session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login/login.jsp"); // Nếu chưa đăng nhập thì về login
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // Lấy productId và newQuantity từ request
        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        if (productIdParam != null && quantityParam != null) {
            try {
                int productId = Integer.parseInt(productIdParam);
                int newQuantity = Integer.parseInt(quantityParam);

                if (newQuantity <= 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quantity must be greater than 0.");
                    return;
                }

                // Gọi service để cập nhật
                CartService cartService = new CartService();
                cartService.updateCartItem(userId, productId, newQuantity);

                // Nếu là gọi từ JavaScript (AJAX), trả JSON hoặc status
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Quantity updated");

            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid productId or quantity.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing productId or quantity.");
        }
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
        // Lấy user từ session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login/login.jsp"); // Chuyển hướng nếu chưa đăng nhập
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // Lấy productId và quantity từ request
        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        if (productIdParam == null || quantityParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing productId or quantity.");
            return;
        }


        try {
            int productId = Integer.parseInt(productIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                quantity = 1; // Nếu số lượng không hợp lệ thì gán mặc định
            }
            Product product = productService.getProductById(productId);
            if(product.getStockQuantity()<= 0){
                System.out.println("Sold out");
                return;
            }
            // Gọi service thêm vào giỏ hàng
            CartService cartService = new CartService();
            cartService.addToCart(userId, productId, quantity);

            // Chuyển hướng về lại trang sản phẩm, hoặc cart
            response.sendRedirect("carts?action=view");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format for productId or quantity.");
        }
    }

}
