/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;
import java.util.List;
import model.OrderItem;
/**
 *
 * @author HP
 */
public interface IOrderItemDAO {

    // Thêm một dòng sản phẩm vào đơn hàng
    boolean insertOrderItem(OrderItem item) throws SQLException;

    // Thêm nhiều dòng sản phẩm cho đơn hàng (thường dùng khi tạo order mới)
    void insertOrderItems(List<OrderItem> items) throws SQLException;

    // Lấy tất cả item theo order_id
    List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException;


    // Tính tổng tiền của đơn hàng (nếu không có cột total trong Orders)
    double calculateOrderTotal(int orderId) throws SQLException;

}

