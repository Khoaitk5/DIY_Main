/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import model.Coupon;
import model.User;
import service.CouponService;
import service.SpinTicketService;

/**
 *
 * @author HP
 */
@WebServlet(name = "SpinServlet", urlPatterns = {"/spin"})
public class SpinServlet extends HttpServlet {

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
            out.println("<title>Servlet SpinServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SpinServlet at " + request.getContextPath() + "</h1>");
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
    private final SpinTicketService spinTicketService = new SpinTicketService();
    private final CouponService couponService = new CouponService(); // bạn cần tạo

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Coupon> allAvailable = couponService.findAllAvailable();

        List<Coupon> spinCoupons;
        if (allAvailable.size() <= 6) {
            spinCoupons = allAvailable;
        } else {
            Collections.shuffle(allAvailable);
            spinCoupons = allAvailable.subList(0, 6);
        }

        request.setAttribute("coupons", spinCoupons);
        request.getRequestDispatcher("/Wheel/wheel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (user == null) {
            out.println("{");
            out.println("\"status\": \"error\",");
            out.println("\"message\": \"You must be logged in.\"");
            out.println("}");
            return;
        }

        int userId = user.getId();

        if (!spinTicketService.canSpin(userId)) {
            out.println("{");
            out.println("\"status\": \"error\",");
            out.println("\"message\": \"You can only spin once every 7 days.\"");
            out.println("}");
            return;
        }

        String[] codes = request.getParameterValues("codes");
        if (codes == null || codes.length == 0) {
            out.println("{");
            out.println("\"status\": \"error\",");
            out.println("\"message\": \"No coupons provided.\"");
            out.println("}");
            return;
        }

        String wonCode = codes[new Random().nextInt(codes.length)];

        spinTicketService.updatePlayDate(userId);

        out.println("{");
        out.println("\"status\": \"success\",");
        out.println("\"message\": \"You won coupon: " + wonCode + "\",");
        out.println("\"couponCode\": \"" + wonCode + "\"");
        out.println("}");
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
