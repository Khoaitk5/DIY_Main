/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import java.sql.SQLException;
import model.Cart;
/**
 *
 * @author HP
 */
public interface ICartService {
    // Thêm sản phẩm vào giỏ hàng
    void addToCart(int userId, int productId, int quantity);

    // Lấy giỏ hàng của người dùng
    Cart getCartByUserId(int userId);

    // Cập nhật số lượng của sản phẩm trong giỏ
    void updateCartItem(int userId, int productId, int newQuantity);

    // Xoá 1 sản phẩm khỏi giỏ
    void removeItem(int userId, int productId);

    // Xoá toàn bộ giỏ hàng
    void clearCart(int userId);

    // Tính tổng giá trị đơn hàng
    double calculateCartTotal(int userId);
}
