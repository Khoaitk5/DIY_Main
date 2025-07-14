package controller;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cart;
import model.CartItem;
import model.Category;
import model.Product;
import model.Tag;
import model.User;
import service.CartService;
import service.ProductService;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 15 // 15MB
)
@WebServlet(name = "ProductServlet", urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private CartService cartService;

    public void init() {
        productService = new ProductService();
        cartService = new CartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                showNewForm(request, response);
                break;
            case "edit": {
                try {
                    showEditForm(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case "details": {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Product product = productService.getProductById(id);
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("Product/productDetails.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
                }
            }
            break;
            default:
                showProductList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create": {
                try {
                    insertPro(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case "edit": {
                try {
                    updatePro(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case "delete": {
                try {
                    deletePro(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
        }
    }

    private void showProductList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String search = request.getParameter("search");
            String sort = request.getParameter("sort");
            String[] categoryArray = request.getParameterValues("category");
            String[] tagArray = request.getParameterValues("tag");

            List<String> categories = (categoryArray != null) ? List.of(categoryArray) : new ArrayList<>();
            List<String> tags = (tagArray != null) ? List.of(tagArray) : new ArrayList<>();

            int page = 1;
            int pageSize = 9;

            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                // mặc định giữ page = 1
            }

            int offset = (page - 1) * pageSize;

            List<Product> allFilteredProducts;
            boolean hasFilter = (search != null && !search.trim().isEmpty())
                    || (sort != null && !sort.trim().isEmpty())
                    || (!categories.isEmpty())
                    || (!tags.isEmpty());

            if (hasFilter) {
                allFilteredProducts = productService.filterProducts(search, categories, tags, sort);
            } else {
                allFilteredProducts = productService.getAllProducts();
            }

            int totalProducts = allFilteredProducts.size();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            List<Product> pagedProducts = allFilteredProducts
                    .stream()
                    .skip(offset)
                    .limit(pageSize)
                    .toList();

            List<Category> allCategories = productService.getAllCategories();
            List<Tag> allTags = productService.getAllTags();

            request.setAttribute("products", pagedProducts);
            request.setAttribute("allCategories", allCategories);
            request.setAttribute("allTags", allTags);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

//            <div class="filter-section">
//                    <div class="filter-title">Tags</div>
//                    <c:forEach var="tag" items="${allTags}">
//                        <div class="subcategory-item">
//                            <input type="checkbox" name="tag" value="${tag.name}"
//                                   ${fn:contains(paramValues.tag, tag.name) ? 'checked' : ''}>
//                            <label for="tag-${tag.name}">${tag.name.toUpperCase()}</label>
//                        </div>
//                    </c:forEach>
//                </div>

            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                Cart cart = cartService.getCartByUserId(user.getId());

                List<CartItem> recentItems = cart != null ? cart.getItems() : new ArrayList<>();
                request.setAttribute("recentCartItems", recentItems);
                request.setAttribute("cartCount", recentItems.stream()
                        .mapToInt(CartItem::getQuantity).sum());
            }

            request.getRequestDispatcher("Product/products.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi hiển thị danh sách sản phẩm.");
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("product/createPro.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        Product existingProduct = productService.getProductById(id);
        request.setAttribute("product", existingProduct);
        request.getRequestDispatcher("product/editPro.jsp").forward(request, response);
    }

    public static String generateRandomCategoryName(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private void insertPro(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String name = request.getParameter("name");
        Double price = Double.valueOf(request.getParameter("price"));
        String description = request.getParameter("description");
        int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));

        // Lấy tag từ form nếu có (ví dụ: <input name="tags" ... /> truyền lên, tách chuỗi hoặc lấy nhiều input)
        String[] tagNames = request.getParameterValues("tags");
        List<Tag> tagList = new ArrayList<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                tagList.add(new Tag(tagName.trim()));
            }
        }

        // Lấy category (có thể chọn từ form, demo tạm random)
        String randomCategoryName = generateRandomCategoryName(4);
        Category category = new Category(randomCategoryName);

        // Xử lý ảnh upload
        Part imagePart = request.getPart("image");
        String fileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
        String uploadDir = "C:\\Users\\HP\\Documents\\NetBeansProjects\\DIY_Main\\src\\main\\webapp\\images";
        File imagesFolder = new File(uploadDir);
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }
        File imageFile = new File(imagesFolder, fileName);
        imagePart.write(imageFile.getAbsolutePath());
        String imageUrl = "images/" + fileName;

        // Tạo và lưu sản phẩm
        Product newProduct = new Product(name, description, price, stockQuantity, imageUrl, category);
        newProduct.setTags(tagList); // Gán tags cho sản phẩm

        productService.createProduct(newProduct);

        response.sendRedirect("home");
    }

    private void updatePro(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        Double price = Double.valueOf(request.getParameter("price"));
        String description = request.getParameter("description");
        int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));

        // Lấy tag từ form
        String[] tagNames = request.getParameterValues("tags");
        List<Tag> tagList = new ArrayList<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                tagList.add(new Tag(tagName.trim()));
            }
        }

        // Lấy category (tùy chỉnh lấy từ form hay logic khác)
        String randomCategoryName = generateRandomCategoryName(4);
        Category category = new Category(randomCategoryName);

        // Lấy ảnh mới nếu có
        Part imagePart = request.getPart("image");
        String fileName = (imagePart != null && imagePart.getSize() > 0)
                ? Paths.get(imagePart.getSubmittedFileName()).getFileName().toString()
                : null;

        String imageUrl = null;
        if (fileName != null && !fileName.isEmpty()) {
            String uploadDir = "C:\\Users\\HP\\Documents\\NetBeansProjects\\DIY_Main\\src\\main\\webapp\\images";
            File imagesFolder = new File(uploadDir);
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            File imageFile = new File(imagesFolder, fileName);
            imagePart.write(imageFile.getAbsolutePath());
            imageUrl = "images/" + fileName;
        }

        Product updatedProduct = new Product(id, name, description, price, true, stockQuantity, imageUrl, category);
        updatedProduct.setTags(tagList);

        productService.updateProduct(updatedProduct);

        response.sendRedirect("products"); // hoặc load lại list
    }

    private void deletePro(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        productService.disableProduct(id);
        response.sendRedirect("products");
    }

    @Override
    public String getServletInfo() {
        return "ProductServlet for product CRUD and filter";
    }
}
