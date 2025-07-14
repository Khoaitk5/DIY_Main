package dao;

import java.sql.*;
import java.util.*;
import model.Category;
import model.Product;
import model.Tag;

public class ProductDao implements IProductDAO {

    // SQL statements
    private static final String INSERT_PRODUCT
            = "INSERT INTO Product (name, description, price, status, stockQuantity, category_id, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM Product WHERE id = ?";
    private static final String UPDATE_PRODUCT
            = "UPDATE Product SET name = ?, description = ?, price = ?, status = ?, stockQuantity = ?, image_url = ?, category_id = ? WHERE id = ?";
    private static final String DELETE_PRODUCT = "UPDATE Product SET status = ? WHERE id = ?";
    private static final String SELECT_ALL_PRODUCT = "SELECT * FROM Product";

    private static final String INSERT_CATEGORY = "INSERT INTO Category (name) VALUES (?)";
    private static final String SELECT_CATEGORY_BY_ID = "SELECT * FROM Category WHERE id = ?";
    private static final String SELECT_ID_BY_CATEGORY_NAME = "SELECT id FROM Category WHERE name = ?";
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM Category";

    private static final String INSERT_TAG = "INSERT INTO Tag (name) VALUES (?)";
    private static final String SELECT_ID_BY_TAG_NAME = "SELECT id FROM Tag WHERE name = ?";
    private static final String SELECT_ALL_TAGS = "SELECT * FROM Tag";

    // Tag - Product_Tag
    private static final String INSERT_PRODUCT_TAG = "INSERT INTO Product_Tag (product_id, tag_id) VALUES (?, ?)";
    private static final String DELETE_PRODUCT_TAGS = "DELETE FROM Product_Tag WHERE product_id = ?";
    private static final String SELECT_TAGS_BY_PRODUCT_ID
            = "SELECT t.id, t.name FROM Tag t JOIN Product_Tag pt ON t.id = pt.tag_id WHERE pt.product_id = ?";

    // ---------------- INSERT PRODUCT ----------------
    @Override
    public void insertProduct(Product pro) throws SQLException {
        int productId = -1;
        int categoryId = insertCategory(pro.getCategory());

        if (categoryId == -1) {
            System.err.println("Không thể chèn hoặc lấy ID cho Category");
            return;
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pro.getName());
            pstmt.setString(2, pro.getDescription());
            pstmt.setDouble(3, pro.getPrice());
            pstmt.setBoolean(4, pro.isStatus());
            pstmt.setInt(5, pro.getStockQuantity());
            pstmt.setInt(6, categoryId);
            pstmt.setString(7, pro.getImageUrl());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        productId = rs.getInt(1);
                        System.out.println("Insert Product thành công. ID: " + productId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gán tag cho product
        if (productId != -1 && pro.getTags() != null && !pro.getTags().isEmpty()) {
            insertProductTags(productId, pro.getTags());
        }
    }

    // ---------------- TAG & CATEGORY HELPERS ----------------
    public static int insertCategory(Category category) {
        int categoryId = -1;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, category.getName());
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categoryId = rs.getInt(1);
                    }
                }
                System.out.println("Insert Category thành công");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            // Nếu category đã tồn tại → lấy ID
            try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ID_BY_CATEGORY_NAME)) {
                stmt.setString(1, category.getName());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        categoryId = rs.getInt("id");
                        System.out.println("Category đã tồn tại, lấy lại ID: " + categoryId);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryId;
    }

    public static int insertTag(String tagName) throws SQLException {
        int tagId = -1;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_TAG, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tagName);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tagId = rs.getInt(1);
                }
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            // Tag đã tồn tại → lấy lại ID
            try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ID_BY_TAG_NAME)) {
                stmt.setString(1, tagName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        tagId = rs.getInt("id");
                    }
                }
            }
        }
        return tagId;
    }

    public void insertProductTags(int productId, List<Tag> tags) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_PRODUCT_TAG)) {
            for (Tag tag : tags) {
                int tagId = insertTag(tag.getName());
                pstmt.setInt(1, productId);
                pstmt.setInt(2, tagId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public void updateProductTags(int productId, List<Tag> tags) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(DELETE_PRODUCT_TAGS)) {
            deleteStmt.setInt(1, productId);
            deleteStmt.executeUpdate();
        }
        insertProductTags(productId, tags);
    }

    public List<Tag> selectTagsByProductId(int productId) {
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_TAGS_BY_PRODUCT_ID)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tag tag = new Tag();
                    tag.setId(rs.getInt("id"));
                    tag.setName(rs.getString("name"));
                    tags.add(tag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    // ---------------- SELECT PRODUCT BY ID ----------------
    @Override
    public Product selectProduct(int id) {
        Product product = null;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_PRODUCT_BY_ID)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStatus(rs.getBoolean("status"));
                    product.setStockQuantity(rs.getInt("stockQuantity"));
                    product.setImageUrl(rs.getString("image_url"));

                    int categoryId = rs.getInt("category_id");
                    Category category = selectCategoryById(categoryId);
                    product.setCategory(category);

                    // Lấy list tag
                    product.setTags(selectTagsByProductId(product.getId()));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return product;
    }

    private Category selectCategoryById(int id) {
        Category category = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_CATEGORY_BY_ID)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    category = new Category(id, name); // Không cần List<Tag> tags nữa
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    // ---------------- SELECT ALL PRODUCTS ----------------
    @Override
    public List<Product> selectAllProducts() {
        List<Product> productList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_PRODUCT)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                boolean status = rs.getBoolean("status");
                int stockQuantity = rs.getInt("stockQuantity");
                int categoryId = rs.getInt("category_id");
                String imageUrl = rs.getString("image_url");

                Category category = selectCategoryById(categoryId);
                Product product = new Product(id, name, description, price, status, stockQuantity, imageUrl, category);

                // Lấy tag cho sản phẩm
                product.setTags(selectTagsByProductId(id));

                productList.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList;
    }

    // ---------------- DELETE PRODUCT ----------------
    @Override
    public boolean deleteProduct(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(DELETE_PRODUCT)) {
            pstmt.setBoolean(1, false);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------- UPDATE PRODUCT ----------------
    @Override
    public boolean updateProduct(Product pro) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(UPDATE_PRODUCT)) {

            int categoryId = insertCategory(pro.getCategory());

            pstmt.setString(1, pro.getName());
            pstmt.setString(2, pro.getDescription());
            pstmt.setDouble(3, pro.getPrice());
            pstmt.setBoolean(4, pro.isStatus());
            pstmt.setInt(5, pro.getStockQuantity());
            pstmt.setString(6, pro.getImageUrl());
            pstmt.setInt(7, categoryId);
            pstmt.setInt(8, pro.getId());

            int rows = pstmt.executeUpdate();

            // Update product_tags
            updateProductTags(pro.getId(), pro.getTags());

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- CATEGORIES/TAGS HELPERS ----------------
    public Category selectCategoryByName(String name) {
        Category category = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_ID_BY_CATEGORY_NAME)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int categoryId = rs.getInt("id");
                    category = new Category(categoryId, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    public List<Category> selectAllCategories() {
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_CATEGORIES); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                list.add(new Category(id, name));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tag> selectAllTags() {
        List<Tag> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TAGS); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tag tag = new Tag();
                tag.setId(rs.getInt("id"));
                tag.setName(rs.getString("name"));
                list.add(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---------------- FILTER PRODUCTS ----------------
    public List<Product> filterProducts(String search, List<String> categories, List<String> tags, String sortBy) throws SQLException {
        List<Product> filtered = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Product p WHERE p.status = 1");
        List<Object> params = new ArrayList<>();

        // Lọc theo tên sản phẩm
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND p.name LIKE ?");
            params.add("%" + search.trim() + "%");
        }

        // Lọc theo danh mục
        if (categories != null && !categories.isEmpty()) {
            sql.append(" AND p.category_id IN (")
                    .append("SELECT id FROM Category WHERE name IN (")
                    .append(String.join(",", Collections.nCopies(categories.size(), "?")))
                    .append("))");
            params.addAll(categories);
        }

        // Lọc theo tag (OR logic) qua Product_Tag
        if (tags != null && !tags.isEmpty()) {
            sql.append(" AND p.id IN (")
                    .append("SELECT pt.product_id FROM Product_Tag pt JOIN Tag t ON pt.tag_id = t.id WHERE t.name IN (")
                    .append(String.join(",", Collections.nCopies(tags.size(), "?")))
                    .append("))");
            params.addAll(tags);
        }

        // Sắp xếp
        if (sortBy != null) {
            switch (sortBy) {
                case "name-asc":
                    sql.append(" ORDER BY p.name ASC");
                    break;
                case "name-desc":
                    sql.append(" ORDER BY p.name DESC");
                    break;
                case "price-asc":
                    sql.append(" ORDER BY p.price ASC");
                    break;
                case "price-desc":
                    sql.append(" ORDER BY p.price DESC");
                    break;
                case "newest":
                    sql.append(" ORDER BY p.id DESC");
                    break;
            }
        }

        // Thực thi truy vấn
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getDouble("price"));
                    p.setStatus(rs.getBoolean("status"));
                    p.setStockQuantity(rs.getInt("stockQuantity"));
                    p.setImageUrl(rs.getString("image_url"));

                    int categoryId = rs.getInt("category_id");
                    Category category = selectCategoryById(categoryId);
                    p.setCategory(category);

                    // Lấy tag cho sản phẩm
                    p.setTags(selectTagsByProductId(p.getId()));

                    filtered.add(p);
                }
            }
        }

        return filtered;
    }

    public boolean increaseStock(int productId, int quantity) {
        String sql = "UPDATE Product SET stockQuantity = stockQuantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decreaseStock(int productId, int quantity) {
        String sql = "UPDATE Product SET stockQuantity = stockQuantity - ? WHERE id = ? AND stockQuantity >= ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);  // đảm bảo không giảm về âm
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
