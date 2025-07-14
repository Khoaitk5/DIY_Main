/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;
import model.Cart;

/**
 *
 * @author HP
 */
public interface ICartDAO {

    public void addToCart(int userId, int productId, int quantity) throws SQLException;

    public Cart getCartByUserId(int userId);

    public void updateCartItem(int userId, int productId, int newQuantity) throws SQLException;

    public void removeItem(int userId, int productId) throws SQLException;

    public void clearCart(int userId) throws SQLException;

    public double calculateCartTotal(int userId);

}
