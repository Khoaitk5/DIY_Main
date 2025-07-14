package dao;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;

import model.Order;
import model.Address;
import model.OrderStatus;

public class OrderDao implements IOrderDAO {

    private static final String INSERT_ORDER_SQL = "INSERT INTO Orders (user_id, address_id, order_date, total_amount, status, coupon_code, discount_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ORDER_BY_ID_SQL = "SELECT * FROM Orders WHERE id = ?";
    private static final String UPDATE_ORDER_STATUS_SQL = "UPDATE Orders SET status = ? WHERE id = ?";
    private static final String CANCEL_ORDER_SQL = "UPDATE Orders SET status = 'Cancelled' WHERE id = ? AND status = 'Processing'";
    private static final String SELECT_ORDERS_BY_USER_SQL = "SELECT * FROM Orders WHERE user_id = ? ORDER BY order_date DESC";
    private static final String TOTAL_SPENT_BY_USER_SQL = "SELECT SUM(total_amount) FROM Orders WHERE user_id = ? AND status = 'Completed'";
    private static final String UPDATE_ADDRESS_SQL = "UPDATE Orders SET address_id = ? WHERE id = ? AND status = 'Processing'";

    private final AddressDao addressDao = new AddressDao();

    @Override
    public int insertOrder(Order order) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ORDER_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getShippingAddress().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setString(5, order.getStatus().name()); // enum → "Processing"
            stmt.setString(6, order.getCouponCode());
            stmt.setDouble(7, order.getDiscountAmount());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    @Override
    public Order getOrderById(int orderId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ORDER_BY_ID_SQL)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status"))); // DB → enum
                    order.setCouponCode(rs.getString("coupon_code"));
                    order.setDiscountAmount(rs.getDouble("discount_amount"));

                    int addressId = rs.getInt("address_id");
                    Address address = addressDao.selectAddress(addressId);
                    order.setShippingAddress(address);

                    return order;
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ORDER_STATUS_SQL)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cancelOrder(int orderId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CANCEL_ORDER_SQL)) {

            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ORDERS_BY_USER_SQL)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(userId);
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                    order.setCouponCode(rs.getString("coupon_code"));
                    order.setDiscountAmount(rs.getDouble("discount_amount"));

                    int addressId = rs.getInt("address_id");
                    Address address = addressDao.selectAddress(addressId);
                    order.setShippingAddress(address);

                    orders.add(order);
                }
            }
        }
        return orders;
    }

    @Override
    public double getTotalSpentByUser(int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TOTAL_SPENT_BY_USER_SQL)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    @Override
    public boolean updateAddress(int orderId, int newAddressId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ADDRESS_SQL)) {

            stmt.setInt(1, newAddressId);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
}
