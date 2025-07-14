package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Xoá session đăng nhập
        HttpSession session = request.getSession(false); // Lấy session nếu tồn tại
        if (session != null) {
            session.invalidate(); // Xoá toàn bộ session
        }
        // Chuyển về trang login (hoặc home tuỳ ý)
        response.sendRedirect(request.getContextPath() + "/");
    }

    // Nếu muốn hỗ trợ POST cũng logout luôn
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}