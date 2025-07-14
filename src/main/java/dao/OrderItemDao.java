/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import model.OrderItem;
import model.Product;

/**
 *
 * @author HP
 */
public class OrderItemDao implements IOrderItemDAO {

    private static final String INSERT_ORDER_ITEM = "INSERT INTO OrderItem (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ITEMS_BY_ORDER_ID = "SELECT order_id, product_id, quantity, unit_price FROM OrderItem WHERE order_id = ?";
    private static final String CALCULATE_ORDER_TOTAL = "SELECT SUM(quantity * unit_price) AS total FROM OrderItem WHERE order_id = ?";

    private final ProductDao productDao = new ProductDao();

    @Override
    public boolean insertOrderItem(OrderItem item) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_ORDER_ITEM)) {
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getProduct().getId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public void insertOrderItems(List<OrderItem> items) throws SQLException {
        for (OrderItem item : items) {
            insertOrderItem(item);
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ITEMS_BY_ORDER_ID)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = productDao.selectProduct(rs.getInt("product_id"));
                    OrderItem item = new OrderItem(
                            rs.getInt("order_id"),
                            product,
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price")
                    );
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public double calculateOrderTotal(int orderId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(CALCULATE_ORDER_TOTAL)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

}
