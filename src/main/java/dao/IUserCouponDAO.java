/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.UserCoupon;
import java.util.List;

/**
 *
 * @author HP
 */
public interface IUserCouponDAO {
    // 1. Tìm mã mà user đã được cấp
    UserCoupon findByUserAndCoupon(int userId, String couponCode);

    // 2. Kiểm tra user còn được phép dùng mã (chưa quá usage_count)
    boolean canUseCoupon(int userId, String couponCode);

    // 3. Tăng usage_count của user lên 1 và cập nhật used_at = NOW()
    void increaseUsage(int userId, String couponCode);

    // 4. Gán coupon cho user nếu chưa có (tạo dòng mới)
    void assignCouponToUser(int userId, String couponCode);

    // 5. Lấy tất cả coupon mà user đang sở hữu
    List<UserCoupon> getCouponsOfUser(int userId);

    // 6. Xoá coupon khỏi user (nếu bạn cho phép người dùng từ chối mã)
    void removeCouponFromUser(int userId, String couponCode);

    // 7. Đếm số lượt user đã dùng mã
    int getUsageCount(int userId, String couponCode);
}
