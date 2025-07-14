package service;

import model.UserCoupon;
import java.util.List;

public interface IUserCouponService {

    // Lấy 1 bản ghi UserCoupon theo userId và couponCode
    UserCoupon getByUserAndCoupon(int userId, String couponCode);

    // Kiểm tra user còn được dùng mã đó hay không
    boolean canUserUse(int userId, String couponCode);

    // Trả về danh sách tất cả coupon mà user đang sở hữu
    List<UserCoupon> getUserCoupons(int userId);

    // Gán coupon cho user nếu chưa có
    void assignCoupon(int userId, String couponCode);

    // Tăng usage_count + cập nhật used_at = NOW()
    void increaseUsage(int userId, String couponCode);
}

