package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Address;

import model.Order;
import model.OrderItem;
import model.OrderStatus;
import model.Product;
import model.User;
import service.AddressService;
import service.OrderService;
import service.ProductService;
import service.UserService;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private OrderService orderService;
    private ProductService productService;
    private UserService userService;

    @Override
    public void init() {
        orderService = new OrderService();
        productService = new ProductService();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "view":
                    viewOrder(request, response);
                    break;
                case "cancel":
                    cancelOrder(request, response);
                    break;
                default:
                    listOrders(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        try {
            switch (action) {
                case "create":
                    createOrder(request, response);
                    break;
                case "updateAddress":
                    updateAddress(request, response);
                    break;
                default:
                    response.sendRedirect("home.jsp");
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void createOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("Login/login.jsp"); // hoặc trang thông báo chưa đăng nhập
            return;
        }

        String[] productIds = request.getParameterValues("productIds");
        String[] quantities = request.getParameterValues("quantities");
        String promoCode = request.getParameter("promoCode");
        double discountPercent = 0;

        try {
            discountPercent = Double.parseDouble(request.getParameter("discountPercent"));
        } catch (Exception ignored) {
        }

        if (productIds == null || quantities == null || productIds.length != quantities.length) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Dữ liệu không hợp lệ\"}");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double subtotal = 0;

        for (int i = 0; i < productIds.length; i++) {
            int productId = Integer.parseInt(productIds[i]);
            int quantity = Integer.parseInt(quantities[i]);

            Product product = productService.getProductById(productId);
            if (product == null || quantity <= 0) {
                continue;
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());

            subtotal += quantity * product.getPrice();
            orderItems.add(item);
        }

        double discountAmount = subtotal * discountPercent / 100.0;
        double totalAmount = subtotal - discountAmount;

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.Processing);
        order.setItems(orderItems);
        order.setCouponCode(promoCode);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);

        List<Address> addresses = userService.getAddressesByUserId(user.getId());
        System.out.println("User ID: " + user.getId());
        System.out.println("Addresses size: " + addresses.size());

        session.setAttribute("userAddresses", addresses); // DÙNG session thay vì request


        // Gán vào session để chuyển sang payment.jsp
        session.setAttribute("orderPreview", order);

        // Trả về JSON để React redirect
        response.setContentType("application/json");
        response.getWriter().write("{\"redirect\": \"Cart/payment.jsp\"}");
    }

    private void viewOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        Order order = orderService.getOrderById(id);

        request.setAttribute("order", order);
        request.getRequestDispatcher("/order-detail.jsp").forward(request, response);
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId")); // hoặc lấy từ session
        List<Order> orders = orderService.getOrdersByUserId(userId);

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/order-list.jsp").forward(request, response);
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        orderService.cancelOrder(id);

        response.sendRedirect("orders?action=list");
    }

    private void updateAddress(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));
        int newAddressId = Integer.parseInt(request.getParameter("newAddressId"));

        orderService.updateShippingAddress(orderId, newAddressId);
        response.sendRedirect("orders?action=view&id=" + orderId);
    }
}
