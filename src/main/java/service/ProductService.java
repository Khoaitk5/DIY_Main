package service;

import dao.ProductDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Category;
import model.Product;
import model.Tag;

public class ProductService implements IProductService {

    private final ProductDao productDao = new ProductDao();

    @Override
    public void createProduct(Product product) throws SQLException {
        productDao.insertProduct(product);
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        return productDao.selectProduct(id);
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        List<Product> all = productDao.selectAllProducts();
        List<Product> activeOnly = new ArrayList<>();
        for (Product p : all) {
            if (p.isStatus()) {
                activeOnly.add(p);
            }
        }
        return activeOnly;
    }

    @Override
    public boolean updateProduct(Product product) throws SQLException {
        return productDao.updateProduct(product);
    }

    @Override
    public boolean disableProduct(int id) throws SQLException {
        return productDao.deleteProduct(id);
    }

    @Override
    public List<Product> searchByTag(List<String> tagNames) throws SQLException {
        List<Product> all = productDao.selectAllProducts();
        List<Product> matched = new ArrayList<>();
        for (Product p : all) {
            if (!p.isStatus()) {
                continue;
            }
            // So sánh tag của Product thay vì tag của Category!
            List<String> productTags = new ArrayList<>();
            if (p.getTags() != null) {
                for (Tag t : p.getTags()) {
                    productTags.add(t.getName().toLowerCase());
                }
            }
            for (String tag : tagNames) {
                if (productTags.contains(tag.toLowerCase())) {
                    matched.add(p);
                    break;
                }
            }
        }
        return matched;
    }

    public Category selectCategoryByName(String name) {
        return productDao.selectCategoryByName(name);
    }

    public List<Product> filterProducts(String search, List<String> categoryNames, List<String> tagNames, String sortBy) {
        try {
            return productDao.filterProducts(search, categoryNames, tagNames, sortBy);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Category> getAllCategories() {
        return productDao.selectAllCategories();
    }

    public List<Tag> getAllTags() {
        return productDao.selectAllTags();
    }

    public boolean decreaseStock(int productId, int quantity) {
        try {
            return productDao.decreaseStock(productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean increaseStock(int productId, int quantity) {
        try {
            return productDao.increaseStock(productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
