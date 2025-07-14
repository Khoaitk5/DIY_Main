/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.CartDao;
import model.Cart;

/**
 *
 * @author HP
 */
public class CartService implements ICartService{
    private final CartDao cartDao = new CartDao();

    @Override
    public void addToCart(int userId, int productId, int quantity) {
        try {
            cartDao.addToCart(userId, productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            // Log lỗi hoặc throw RuntimeException tuỳ vào kiến trúc
        }
    }

    @Override
    public Cart getCartByUserId(int userId) {
        return cartDao.getCartByUserId(userId);
    }

    @Override
    public void updateCartItem(int userId, int productId, int newQuantity) {
        try {
            cartDao.updateCartItem(userId, productId, newQuantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeItem(int userId, int productId) {
        try {
            cartDao.removeItem(userId, productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearCart(int userId) {
        try {
            cartDao.clearCart(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public double calculateCartTotal(int userId) {
        return cartDao.calculateCartTotal(userId);
    }
}
