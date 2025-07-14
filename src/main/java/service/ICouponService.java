package service;

import model.Coupon;
import java.util.List;

public interface ICouponService {

    // Tìm mã giảm giá theo code
    Coupon findByCode(String code);

    // Kiểm tra mã còn hạn và còn lượt sử dụng
    boolean isValid(Coupon coupon);

    // Giảm quantity hệ thống của coupon (khi user sử dụng)
    void decreaseQuantity(String code);

    // Trả về tất cả mã còn hiệu lực
    List<Coupon> findAllAvailable();

    // Thêm mới coupon (admin)
    void insert(Coupon coupon);

    // Cập nhật thông tin coupon (admin)
    void update(Coupon coupon);

    // Xóa coupon theo code (admin)
    void delete(String code);
}


