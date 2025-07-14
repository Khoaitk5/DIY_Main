/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.Cart;
import model.CartItem;
import model.Product;
import model.User;
import service.CartService;
import service.ProductService;

/**
 *
 * @author HP
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private ProductService productService;
    private CartService cartService;

    public void init() {
        productService = new ProductService();
        cartService = new CartService();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HomeServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomeServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            loadInitialData(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void loadInitialData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<Product> allProducts = productService.getAllProducts();
        int totalProducts = Math.min(16, allProducts.size()); 
        int pageSize = 8;
        int totalPages = (int) Math.ceil(totalProducts / (double) pageSize);

        // Xác định trang hiện tại
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) {
                    currentPage = 1;
                }
                if (currentPage > totalPages) {
                    currentPage = totalPages;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, totalProducts);

        // Lấy đúng 6 sản phẩm cho trang hiện tại
        List<Product> shopnow = allProducts.subList(0, totalProducts).subList(start, end);

        // Bestseller ví dụ random 3 sản phẩm khác
        Collections.shuffle(allProducts);
        List<Product> bestseller = allProducts.stream().limit(3).collect(Collectors.toList());

        // Xử lý cart và các info khác (giữ nguyên như cũ)
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            int userId = user.getId();
            Cart cart = cartService.getCartByUserId(userId);
            List<CartItem> recentItems = new ArrayList<>();
            if (cart != null && cart.getItems() != null) {
                List<CartItem> allItems = cart.getItems();
                Collections.reverse(allItems);
                recentItems = allItems.stream().limit(5).collect(Collectors.toList());
            }
            request.setAttribute("recentCartItems", recentItems);
            request.setAttribute("cartCount", cart != null && cart.getItems() != null
                    ? cart.getItems().stream().mapToInt(CartItem::getQuantity).sum()
                    : 0);
        }

        request.setAttribute("shopnow", shopnow);
        request.setAttribute("bestseller", bestseller);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        RequestDispatcher dispatcher = request.getRequestDispatcher("Home.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
