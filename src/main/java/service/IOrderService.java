/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;


import java.sql.SQLException;
import java.util.List;
import model.Order;
import model.OrderItem;
import model.OrderStatus;

public interface IOrderService {

    // === Đơn hàng ===

    // Tạo đơn hàng mới từ danh sách sản phẩm
    int createOrder(Order order, List<OrderItem> items) throws SQLException;

    // Lấy đơn hàng theo ID
    Order getOrderById(int orderId) throws SQLException;

    // Lấy danh sách đơn hàng của người dùng
    List<Order> getOrdersByUserId(int userId) throws SQLException;

    // Cập nhật trạng thái đơn hàng
    boolean updateOrderStatus(int orderId, OrderStatus status) throws SQLException;

    // Huỷ đơn hàng
    boolean cancelOrder(int orderId) throws SQLException;

    // Cập nhật địa chỉ giao hàng của đơn
    boolean updateShippingAddress(int orderId, int newAddressId) throws SQLException;

    // Lấy tổng chi tiêu của người dùng
    double getTotalSpentByUser(int userId) throws SQLException;


    // === Dòng sản phẩm trong đơn hàng (OrderItem) ===

    // Lấy danh sách sản phẩm trong đơn hàng
    List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException;

    // Tính tổng tiền của đơn hàng từ các OrderItem
    double calculateOrderTotal(int orderId) throws SQLException;
}

