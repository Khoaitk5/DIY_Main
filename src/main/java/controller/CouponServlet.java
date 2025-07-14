/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonObject;
import dao.CouponDao;
import dao.UserCouponDao;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Coupon;
import model.User;
import model.UserCoupon;
import service.CouponService;
import service.UserCouponService;

/**
 *
 * @author HP
 */
@WebServlet(name = "CouponServlet", urlPatterns = {"/coupon"})
public class CouponServlet extends HttpServlet {

    private CouponService couponService;
    private UserCouponService userCouponService;

    public void init() {
        couponService = new CouponService();
        userCouponService = new UserCouponService();
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
            out.println("<title>Servlet CouponServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CouponServlet at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter.");
            return;
        }

        switch (action) {
            case "check":
                handleCheckCoupon(request, response);
                break;
            case "list":
                handleListUserCoupons(request, response);
                break;

            // case "apply": handleApplyCoupon(request, response); break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported action: " + action);
                break;
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

    private void handleCheckCoupon(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String code = request.getParameter("code");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject json = new JsonObject();

        // === Debug log start ===
        System.out.println("👉 Checking coupon:");
        System.out.println("Coupon code: " + code);
        System.out.println("User: " + (user != null ? user.getId() : "null"));

        if (code == null || user == null) {
            System.out.println("❌ Mã code hoặc user null");
            json.addProperty("valid", false);
            out.print(json);
            return;
        }

        Coupon coupon = couponService.findByCode(code);
        System.out.println("Found coupon: " + (coupon != null));
        boolean isCouponValid = coupon != null && couponService.isValid(coupon);
        boolean canUserUse = userCouponService.canUserUse(user.getId(), code);

        System.out.println("✔ couponService.isValid = " + isCouponValid);
        System.out.println("✔ userCouponService.canUserUse = " + canUserUse);

        boolean valid = isCouponValid && canUserUse;

        if (valid) {
            json.addProperty("valid", true);
            if (coupon.isIsPercent()) {
                json.addProperty("discountPercent", coupon.getDiscountValue());
                System.out.println("🎯 Percent discount: " + coupon.getDiscountValue());
            } else {
                json.addProperty("discountAmount", coupon.getDiscountValue());
                System.out.println("🎯 Fixed discount: " + coupon.getDiscountValue());
            }
        } else {
            json.addProperty("valid", false);
            System.out.println("❌ Coupon is invalid or cannot be used");
        }

        // === Debug log end ===
        out.print(json);
        out.flush();
    }

    private void handleListUserCoupons(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (user == null) {
            out.print("[]");
            return;
        }

        List<UserCoupon> userCoupons = userCouponService.getUserCoupons(user.getId());

        // Gửi JSON
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(userCoupons);
        out.print(json);
        out.flush();
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
