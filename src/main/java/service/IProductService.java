/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import java.sql.SQLException;
import java.util.List;
import model.Product;
/**
 *
 * @author HP
 */
public interface IProductService {
    // Thêm sản phẩm mới
    void createProduct(Product product) throws SQLException;

    // Lấy sản phẩm theo ID
    Product getProductById(int id) throws SQLException;

    // Lấy tất cả sản phẩm (active)
    List<Product> getAllProducts() throws SQLException;

    // Cập nhật sản phẩm
    boolean updateProduct(Product product) throws SQLException;

    // Ẩn/xoá sản phẩm (soft delete)
    boolean disableProduct(int id) throws SQLException;

    // (Tuỳ chọn) Tìm sản phẩm theo tên tag
    List<Product> searchByTag(List<String> tagNames) throws SQLException;
}
