/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dao.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import service.UserService;

/**
 *
 * @author Vo Hoang
 */
@WebServlet(name = "GoogleCallbackServlet", urlPatterns = {"/oauth2callback"})
public class GoogleCallbackServlet extends HttpServlet {

    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");

    private static final String REDIRECT_URI = "http://localhost:8080/DIY_Main/oauth2callback";
    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";
    private UserService userService = new UserService();

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
            out.println("<title>Servlet GoogleCallbackServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GoogleCallbackServlet at " + request.getContextPath() + "</h1>");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        if (code == null || code.isEmpty()) {
            response.sendRedirect("Login/login.jsp");
            return;
        }

        // 1. Gửi POST request đến Google để lấy access_token
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                transport, jsonFactory,
                CLIENT_ID, CLIENT_SECRET,
                code, REDIRECT_URI
        ).execute();

        String accessToken = tokenResponse.getAccessToken();

        // 2. Tạo credential và gọi API userinfo
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
        GenericUrl url = new GenericUrl(USER_INFO_URI);

        HttpRequest getRequest = requestFactory.buildGetRequest(url);
        getRequest.getHeaders().setContentType("application/json");
        String jsonIdentity = getRequest.execute().parseAsString();

        // 3. Parse JSON user info
        JsonObject userInfo = JsonParser.parseString(jsonIdentity).getAsJsonObject();
        String email = userInfo.get("email").getAsString();
        String name = userInfo.get("name").getAsString();
        String picture = userInfo.get("picture").getAsString();

        // Lưu hoặc cập nhật user vào database
        UserDao userDao = new UserDao();
        Integer userId = userDao.saveOrUpdateOAuthUser(email, name);

        if (userId == null) {
            // Nếu không lưu hoặc update được user
            response.sendRedirect("error.jsp");
            return;
        }
        User userGoogle = null;
        try {
            userGoogle = userService.getUserById(userId);
        } catch (SQLException ex) {
            Logger.getLogger(GoogleCallbackServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Lưu thông tin user vào session
        request.getSession().setAttribute("userEmail", email);
        request.getSession().setAttribute("userName", name);
        request.getSession().setAttribute("userPicture", picture);
        request.getSession().setAttribute("user", userGoogle);

        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
