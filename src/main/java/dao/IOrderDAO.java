/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Order;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author HP
 */
public interface IOrderDAO {
    // === CRUD cơ bản ===
    
    // Thêm đơn hàng mới, trả về orderId vừa tạo
    int insertOrder(Order order) throws SQLException;
    
    // Tìm đơn hàng theo ID
    Order getOrderById(int orderId) throws SQLException;

    // Cập nhật trạng thái đơn (VD: từ PENDING → PAID)
    boolean updateOrderStatus(int orderId, String newStatus) throws SQLException;

    boolean cancelOrder(int orderId) throws SQLException;


    
    // === Truy vấn theo người dùng ===
    
    // Lấy danh sách đơn hàng của 1 user
    List<Order> getOrdersByUserId(int userId) throws SQLException;
    
    // Lấy tổng chi tiêu của 1 user (optional)
    double getTotalSpentByUser(int userId) throws SQLException;
    
    boolean updateAddress(int orderId, int newAddressId) throws SQLException;

}
