/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Coupon;
import java.util.List;

/**
 *
 * @author HP
 */
public interface ICouponDAO {

    // 1. Tìm mã giảm giá theo code
    Coupon findByCode(String code);

    // 2. Kiểm tra mã còn hiệu lực toàn hệ thống (còn hạn và còn lượt)
    boolean isValid(String code);

    // 3. Giảm quantity của mã đi 1 khi người dùng sử dụng
    void decreaseQuantity(String code);

    // 4. Lấy danh sách tất cả mã còn dùng được (chưa hết hạn, còn lượt)
    List<Coupon> findAllAvailable();

    // 5. Thêm mới mã giảm giá (nếu cần quản lý mã động)
    void insert(Coupon coupon);

    // 6. Cập nhật thông tin mã giảm giá (nếu bạn có giao diện admin)
    void update(Coupon coupon);

    // 7. Xóa mã giảm giá (nếu quản trị muốn xoá)
    void delete(String code);
}
