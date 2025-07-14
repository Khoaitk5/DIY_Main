package dao;

import java.sql.*;
import java.util.*;
import model.*;

public class CartDao implements ICartDAO {

    private static final String ADD_TO_CART = "INSERT INTO Cart (user_id) VALUES (?)";
    private static final String SELECT_CART_ID_FROM_USER_ID = "SELECT id FROM Cart WHERE user_id = ?";
    private static final String SELECT_CART_PRODUCTS
            = "SELECT p.id AS id, p.name, p.description, p.price, p.status, "
            + "p.stockQuantity, p.image_url, cp.quantity "
            + "FROM Cart_Product cp "
            + "JOIN Product p ON cp.product_id = p.id "
            + "WHERE cp.cart_id = ?";
    private static final String UPDATE_CART_PRODUCT = "UPDATE Cart_Product SET quantity = ? WHERE cart_id = ? AND product_id = ?";
    private static final String DELETE_CART_PRODUCT = "DELETE FROM Cart_Product WHERE cart_id = ? AND product_id = ?";
    private static final String DELETE_ALL_CART_ITEMS = "DELETE FROM Cart_Product WHERE cart_id = ?";
    private static final String CALCULATE_TOTAL = "SELECT SUM(p.price * cp.quantity) AS total FROM Cart_Product cp JOIN Cart c ON cp.cart_id = c.id JOIN Product p ON cp.product_id = p.id WHERE c.user_id = ?";
    private static final String CHECK_PRODUCT_IN_CART = "SELECT quantity FROM Cart_Product WHERE cart_id = ? AND product_id = ?";
    private static final String INSERT_CART_PRODUCT = "INSERT INTO Cart_Product (cart_id, product_id, quantity) VALUES (?, ?, ?)";

    @Override
    public void addToCart(int userId, int productId, int quantity) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int cartId = getOrCreateCartId(conn, userId);

            boolean exists = false;
            int currentQuantity = 0;
            try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_PRODUCT_IN_CART)) {
                checkStmt.setInt(1, cartId);
                checkStmt.setInt(2, productId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    exists = true;
                    currentQuantity = rs.getInt("quantity");
                }
            }

            if (exists) {
                try (PreparedStatement updateStmt = conn.prepareStatement(UPDATE_CART_PRODUCT)) {
                    updateStmt.setInt(1, currentQuantity + quantity);
                    updateStmt.setInt(2, cartId);
                    updateStmt.setInt(3, productId);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_CART_PRODUCT)) {
                    insertStmt.setInt(1, cartId);
                    insertStmt.setInt(2, productId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private int getOrCreateCartId(Connection conn, int userId) throws SQLException {
        int cartId = -1;
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_CART_ID_FROM_USER_ID)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        try (PreparedStatement insertStmt = conn.prepareStatement(ADD_TO_CART, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, userId);
            insertStmt.executeUpdate();
            ResultSet rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                cartId = rs.getInt(1);
            }
        }
        return cartId;
    }

    @Override
    public Cart getCartByUserId(int userId) {
        Cart cart = null;
        try (Connection conn = DBConnection.getConnection()) {
            int cartId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_CART_ID_FROM_USER_ID)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cartId = rs.getInt("id");
                } else {
                    return null;
                }
            }

            User user = new User();
            user.setId(userId);
            List<CartItem> cartItems = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(SELECT_CART_PRODUCTS)) {
                stmt.setInt(1, cartId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getBoolean("status"),
                            rs.getInt("stockQuantity"),
                            rs.getString("image_url"),
                            null);
                    int quantity = rs.getInt("quantity");
                    cartItems.add(new CartItem(product, quantity));
                }
            }
            cart = new Cart(cartId, user, cartItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    @Override
    public void updateCartItem(int userId, int productId, int newQuantity) throws SQLException {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
        }
        try (Connection conn = DBConnection.getConnection()) {
            int cartId = getOrCreateCartId(conn, userId);
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_CART_PRODUCT)) {
                stmt.setInt(1, newQuantity);
                stmt.setInt(2, cartId);
                stmt.setInt(3, productId);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void removeItem(int userId, int productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            int cartId = getOrCreateCartId(conn, userId);
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_CART_PRODUCT)) {
                stmt.setInt(1, cartId);
                stmt.setInt(2, productId);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void clearCart(int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            int cartId = getOrCreateCartId(conn, userId);
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_CART_ITEMS)) {
                stmt.setInt(1, cartId);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public double calculateCartTotal(int userId) {
        double total = 0.0;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(CALCULATE_TOTAL)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
