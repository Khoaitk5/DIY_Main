package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Coupon;

public class CouponDao implements ICouponDAO {

    @Override
    public Coupon findByCode(String code) {
        String sql = "SELECT * FROM Coupon WHERE code = ?";
        System.out.println("🔍 Looking for coupon code: [" + code + "]");
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCoupon(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isValid(String code) {
        String sql = "SELECT quantity, expired_at FROM Coupon WHERE code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                Timestamp expiredAt = rs.getTimestamp("expired_at");
                Timestamp now = new Timestamp(System.currentTimeMillis());

                System.out.println(">>> Coupon check:");
                System.out.println("Quantity: " + quantity);
                System.out.println("ExpiredAt: " + expiredAt);
                System.out.println("Now: " + now);
                System.out.println("Valid? " + (quantity > 0 && (expiredAt == null || expiredAt.after(now))));
                return quantity > 0 && (expiredAt == null || expiredAt.after(now));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void decreaseQuantity(String code) {
        String sql = "UPDATE Coupon SET quantity = quantity - 1 WHERE code = ? AND quantity > 0";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Coupon> findAllAvailable() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT * FROM Coupon WHERE quantity > 0 AND (expired_at IS NULL OR expired_at > GETDATE())";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCoupon(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void insert(Coupon coupon) {
        String sql = "INSERT INTO Coupon (code, description, discount_value, is_percent, min_order_value, quantity, expired_at, max_usage_per_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, coupon.getCode());
            ps.setString(2, coupon.getDescription());
            ps.setDouble(3, coupon.getDiscountValue());
            ps.setBoolean(4, coupon.isIsPercent());
            ps.setDouble(5, coupon.getMinOrderValue());
            ps.setInt(6, coupon.getQuantity());
            if (coupon.getExpiredAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(coupon.getExpiredAt()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }
            ps.setInt(8, coupon.getMaxUsagePerUser());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Coupon coupon) {
        String sql = "UPDATE Coupon SET description=?, discount_value=?, is_percent=?, min_order_value=?, quantity=?, expired_at=?, max_usage_per_user=? WHERE code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, coupon.getDescription());
            ps.setDouble(2, coupon.getDiscountValue());
            ps.setBoolean(3, coupon.isIsPercent());
            ps.setDouble(4, coupon.getMinOrderValue());
            ps.setInt(5, coupon.getQuantity());
            if (coupon.getExpiredAt() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(coupon.getExpiredAt()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            ps.setInt(7, coupon.getMaxUsagePerUser());
            ps.setString(8, coupon.getCode());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String code) {
        String sql = "DELETE FROM Coupon WHERE code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setCode(rs.getString("code"));
        coupon.setDescription(rs.getString("description"));
        coupon.setDiscountValue(rs.getDouble("discount_value"));
        coupon.setIsPercent(rs.getBoolean("is_percent"));
        coupon.setMinOrderValue(rs.getDouble("min_order_value"));
        coupon.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("expired_at");
        if (ts != null) {
            coupon.setExpiredAt(ts.toLocalDateTime());
        }
        coupon.setMaxUsagePerUser(rs.getInt("max_usage_per_user"));
        return coupon;
    }
}
