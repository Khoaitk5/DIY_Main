package com.vnpay.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Address;
import model.Cart;
import model.CartItem;
import model.Order;
import model.OrderItem;
import model.OrderStatus;
import model.User;
import service.AddressService;
import service.CartService;
import service.OrderService;
import service.ProductService;
import service.UserService;

@WebServlet(name = "ajaxServlet", urlPatterns = {"/payment"})
public class ajaxServlet extends HttpServlet {

    private OrderService orderService = new OrderService();
    private UserService userService = new UserService();
    private CartService cartService = new CartService();
    private AddressService addressService = new AddressService();
    private ProductService productService = new ProductService();
    final double EXCHANGE_RATE = 25000;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        JsonObject job = new JsonObject();
        // Lấy tham số yêu cầu từ phía frontend
        try {
            String totalBillParam = req.getParameter("totalBill");
            String userIdParam = req.getParameter("userId");
            String addressIdParam = req.getParameter("addressId"); // bắt buộc luôn có

            if (totalBillParam == null || userIdParam == null || addressIdParam == null) {
                job.addProperty("code", "01");
                job.addProperty("message", "Thiếu thông tin đầu vào");
                resp.getWriter().write(gson.toJson(job));
                return;
            }

            double totalBill;
            int userId;
            try {
                totalBill = Double.parseDouble(totalBillParam);
                userId = Integer.parseInt(userIdParam);
            } catch (NumberFormatException e) {
                job.addProperty("code", "01");
                job.addProperty("message", "Dữ liệu đầu vào không hợp lệ");
                resp.getWriter().write(gson.toJson(job));
                resp.getWriter().flush();
                return;
            }

            Address shippingAddress = null;

            if ("new".equals(addressIdParam)) {
                String newStreet = req.getParameter("newStreet");
                String newDistrict = req.getParameter("newDistrict");
                String newCity = req.getParameter("newCity");

                // Kiểm tra các trường địa chỉ mới
                if (newStreet == null || newDistrict == null || newCity == null
                        || newStreet.isEmpty() || newDistrict.isEmpty() || newCity.isEmpty()) {

                    job.addProperty("code", "01");
                    job.addProperty("message", "Thiếu thông tin địa chỉ mới");
                    resp.getWriter().write(gson.toJson(job));
                    return;
                }

                Address newAddr = new Address();
                newAddr.setStreet(newStreet);
                newAddr.setDistrict(newDistrict);
                newAddr.setCity(newCity);

                int newAddressId = addressService.addAddress(newAddr);

                userService.linkUserToAddress(userId, newAddressId);

                shippingAddress = addressService.getAddressById(newAddressId);
            } else {
                try {
                    int selectedAddressId = Integer.parseInt(addressIdParam);
                    shippingAddress = addressService.getAddressById(selectedAddressId);
                } catch (NumberFormatException e) {
                    job.addProperty("code", "01");
                    job.addProperty("message", "addressId không hợp lệ");
                    resp.getWriter().write(gson.toJson(job));
                    return;
                }
            }

            // Tạo đơn hàng mới
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderDate(LocalDateTime.now());
            order.setShippingAddress(shippingAddress);

            // Kiểm tra giỏ hàng
            Cart cart = cartService.getCartByUserId(userId);
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                job.addProperty("code", "03");
                job.addProperty("message", "Giỏ hàng rỗng hoặc không tồn tại");
                resp.getWriter().write(gson.toJson(job));
                resp.getWriter().flush();
                return;
            }

            // Tạo danh sách OrderItem
            List<OrderItem> orderItems = new ArrayList<>();
            double computedTotal = 0;
            for (CartItem cartItem : cart.getItems()) {
                OrderItem item = new OrderItem();
                item.setProduct(cartItem.getProduct());
                item.setQuantity(cartItem.getQuantity());
                item.setUnitPrice(cartItem.getProduct().getPrice());
                orderItems.add(item);
                computedTotal += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            }
            order.setItems(orderItems);
            order.setTotalAmount(computedTotal);
            order.setStatus(OrderStatus.Processing);

            String promoCode = req.getParameter("promoCode");
            String discountAmountParam = req.getParameter("discountAmount");

            if (promoCode != null && !promoCode.isEmpty()) {
                order.setCouponCode(promoCode);
            }

            double discountAmount = 0;
            if (discountAmountParam != null && !discountAmountParam.isEmpty()) {
                try {
                    discountAmount = Double.parseDouble(discountAmountParam);
                } catch (NumberFormatException e) {
                    discountAmount = 0;
                }
            }
            order.setDiscountAmount(discountAmount);

            int orderId = orderService.createOrder(order, orderItems);
            if (orderId < 1) {
                job.addProperty("code", "02");
                job.addProperty("message", "Tạo đơn hàng thất bại");
                resp.getWriter().write(gson.toJson(job));
                resp.getWriter().flush();
                return;
            }

            // Giảm stock từng sản phẩm
            for (OrderItem item : orderItems) {
                productService.decreaseStock(item.getProduct().getId(), item.getQuantity());
            }

            cartService.clearCart(userId);

            // Tạo URL thanh toán VNPAY
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "other";
            long amount = (long) (computedTotal * EXCHANGE_RATE * 100);
            String bankCode = req.getParameter("bankCode");

            String vnp_TxnRef = String.valueOf(orderId);
            String vnp_IpAddr = Config.getIpAddress(req);
            String vnp_TmnCode = Config.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", orderType);

            String locale = req.getParameter("language");
            vnp_Params.put("vnp_Locale", (locale != null && !locale.isEmpty()) ? locale : "vn");
            vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

            job.addProperty("code", "00");
            job.addProperty("message", "success");
            job.addProperty("data", paymentUrl);
            resp.getWriter().write(gson.toJson(job));
            resp.getWriter().flush();

        } catch (Exception ex) {
            Logger.getLogger(ajaxServlet.class.getName()).log(Level.SEVERE, null, ex);
            JsonObject jobError = new JsonObject();
            jobError.addProperty("code", "99");
            jobError.addProperty("message", "Lỗi server: " + ex.getMessage());
            resp.getWriter().write(gson.toJson(jobError));
            resp.getWriter().flush();
        }
    }
}
