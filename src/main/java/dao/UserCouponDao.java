package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.UserCoupon;

public class UserCouponDao implements IUserCouponDAO {

    @Override
    public UserCoupon findByUserAndCoupon(int userId, String couponCode) {
        String sql = "SELECT * FROM User_Coupon WHERE user_id = ? AND coupon_code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, couponCode);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUserCoupon(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean canUseCoupon(int userId, String couponCode) {
        String sql = "SELECT uc.usage_count, c.max_usage_per_user, c.quantity, c.expired_at FROM User_Coupon uc JOIN Coupon c ON uc.coupon_code = c.code WHERE uc.user_id = ? AND uc.coupon_code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, couponCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int usage = rs.getInt("usage_count");
                int maxUsage = rs.getInt("max_usage_per_user");
                int quantity = rs.getInt("quantity");
                Timestamp expiredAt = rs.getTimestamp("expired_at");

                System.out.println("usage = " + usage);
                System.out.println("maxUsage = " + maxUsage);
                System.out.println("quantity = " + quantity);
                System.out.println("expired_at = " + expiredAt);
                System.out.println("expired_at is after now = " + (expiredAt == null ? true : expiredAt.toLocalDateTime().isAfter(LocalDateTime.now())));
                Timestamp now = new Timestamp(System.currentTimeMillis());

                return usage < maxUsage
                        && quantity > 0
                        && (expiredAt == null || expiredAt.after(now));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void increaseUsage(int userId, String couponCode) {
        String sql = "UPDATE User_Coupon SET usage_count = usage_count + 1, used_at = GETDATE() WHERE user_id = ? AND coupon_code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, couponCode);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void assignCouponToUser(int userId, String couponCode) {
        // kiểm tra xem đã có chưa
        if (findByUserAndCoupon(userId, couponCode) != null) {
            return;
        }

        String sql = "INSERT INTO User_Coupon (user_id, coupon_code, usage_count, used_at) VALUES (?, ?, 0, NULL)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, couponCode);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserCoupon> getCouponsOfUser(int userId) {
        List<UserCoupon> list = new ArrayList<>();
        String sql = "SELECT * FROM User_Coupon WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUserCoupon(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void removeCouponFromUser(int userId, String couponCode) {
        String sql = "DELETE FROM User_Coupon WHERE user_id = ? AND coupon_code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, couponCode);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getUsageCount(int userId, String couponCode) {
        String sql = "SELECT usage_count FROM User_Coupon WHERE user_id = ? AND coupon_code = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, couponCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("usage_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private UserCoupon mapResultSetToUserCoupon(ResultSet rs) throws SQLException {
        UserCoupon uc = new UserCoupon();
        uc.setUserId(rs.getInt("user_id"));
        uc.setCouponCode(rs.getString("coupon_code"));
        uc.setUsageCount(rs.getInt("usage_count"));
        Timestamp ts = rs.getTimestamp("used_at");
        if (ts != null) {
            uc.setUsedAt(ts.toLocalDateTime());
        }
        return uc;
    }
}
