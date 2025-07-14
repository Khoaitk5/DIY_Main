package com.vnpay.common;

import com.vnpay.common.Config;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Order;
import model.OrderItem;
import model.OrderStatus;
import service.OrderService;
import service.ProductService;

@WebServlet(name = "VnPayReturn", urlPatterns = {"/vnpay_return"})
public class VnpayReturn extends HttpServlet {

    private OrderService orderService = new OrderService();
    private ProductService productService = new ProductService();

    // Xử lý phản hồi từ VNPAY cho cả phương thức GET và POST
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try {
            // Lấy tất cả tham số trả về từ VNPAY
            Map<String, String> fields = new HashMap<>();
            Enumeration<String> params = request.getParameterNames();

            while (params.hasMoreElements()) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    if (!fieldName.equals("vnp_SecureHash") && !fieldName.equals("vnp_SecureHashType")) {
                        // Mã hóa giá trị tham số trước khi tạo chuỗi hash
                        fields.put(fieldName, URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    }
                }
            }

            // Lấy mã hash trả về và tự tính toán mã hash để đối chiếu
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            String signValue = Config.hashAllFields(fields);

            if (signValue.equals(vnp_SecureHash)) {
                // Chữ ký hợp lệ, tiến hành xử lý kết quả giao dịch
                String paymentCode = request.getParameter("vnp_TransactionNo"); // Mã giao dịch VNPAY
                String orderIdStr = request.getParameter("vnp_TxnRef");         // Mã đơn hàng
                String transactionStatus = request.getParameter("vnp_TransactionStatus");

                // Xác định trạng thái thanh toán từ mã phản hồi
                OrderStatus status = null;
                boolean transSuccess;

                if ("00".equals(transactionStatus)) {
                    status = OrderStatus.Completed;
                    transSuccess = true;
                } else {
                    status = OrderStatus.Failed;
                    transSuccess = false;
                }

                // Cập nhật trạng thái đơn hàng trong hệ thống
                int orderId = Integer.parseInt(orderIdStr);
                try {
                    orderService.updateOrderStatus(orderId, status);

                    // Nếu thất bại, rollback stock
                    if (!transSuccess) {
                        try {
                            List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);
                            for (OrderItem item : items) {
                                productService.increaseStock(item.getProduct().getId(), item.getQuantity());
                            }
                        } catch (Exception e) {
                            Logger.getLogger(VnpayReturn.class.getName()).log(Level.WARNING,
                                    "Rollback stock thất bại cho đơn hàng " + orderId, e);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(VnpayReturn.class.getName()).log(Level.SEVERE,
                            "Lỗi cập nhật trạng thái đơn hàng", ex);
                    // (Tiếp tục xử lý để vẫn hiển thị trang kết quả cho người dùng)
                }

                // Lấy thông tin đơn hàng (nếu cần hiển thị cho người dùng)
                Order order = null;
                try {
                    order = orderService.getOrderById(orderId);
                } catch (Exception ex) {
                    Logger.getLogger(VnpayReturn.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (order != null) {
                    request.setAttribute("order", order);
                }

                request.setAttribute("transResult", transSuccess);

                // Chuyển hướng sang trang kết quả thanh toán
                request.getRequestDispatcher("Cart/paymentResult.jsp").forward(request, response);

                // Ghi log thông tin giao dịch để kiểm tra đối soát
                Logger.getLogger(VnpayReturn.class.getName()).log(Level.INFO,
                        "VNPAY return - OrderID {0}, VNPAY TransactionNo {1}, Status {2}",
                        new Object[]{orderId, paymentCode, status});
            } else {
                // Chữ ký không hợp lệ, coi như giao dịch thất bại
                Logger.getLogger(VnpayReturn.class.getName()).log(Level.WARNING,
                        "Invalid signature for VNPAY return data (TxRef = {0})",
                        request.getParameter("vnp_TxnRef"));

                request.setAttribute("transResult", false);
                request.setAttribute("message", "Giao dịch không hợp lệ");
                request.getRequestDispatcher("Cart/paymentResult.jsp").forward(request, response);
            }

        } catch (Exception ex) {
            // Xử lý lỗi chung
            Logger.getLogger(VnpayReturn.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("transResult", false);
            request.setAttribute("message", "Đã xảy ra lỗi trong quá trình xử lý kết quả thanh toán");
            request.getRequestDispatcher("Cart/paymentResult.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
