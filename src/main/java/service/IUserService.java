/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import java.sql.SQLException;
import model.Address;
import model.User;
/**
 *
 * @author HP
 */
public interface IUserService {
    // Đăng ký tài khoản mới
    boolean register(String username, String password, String email, Address address) throws SQLException;

    // Đăng nhập
    User login(String username, String password) throws SQLException;

    // Lấy thông tin user theo ID
    User getUserById(int id) throws SQLException;

    // Cập nhật thông tin user
    boolean updateUser(User user) throws SQLException;

    // Kiểm tra username có tồn tại chưa
    boolean isUsernameTaken(String username) throws SQLException;
}
