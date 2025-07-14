package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;

@WebServlet(name = "SignupServlet", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        userService = new UserService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy thông tin từ form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Lấy username tự động từ phần trước @ của email
        String username = (email != null) ? email.split("@")[0] : "";

        try {
            // Kiểm tra username/email trùng
            if (userService.isUsernameTaken(username)) {
                request.setAttribute("message", "Tên đăng nhập '" + username + "' đã tồn tại! Vui lòng dùng email khác.");
            } else if (userService.isEmailTaken(email)) {
                request.setAttribute("message", "Email đã tồn tại!");
            } else {
                // Đăng ký user mới, không truyền address
                boolean registered = userService.register(username, password, email, null);
                if (registered) {
                    request.setAttribute("message", "Đăng ký thành công! Mời bạn đăng nhập.");
                } else {
                    request.setAttribute("message", "Đăng ký thất bại!");
                }
            }
            // Chuyển về trang login và hiện message
            request.getRequestDispatcher("Login/login.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Lỗi hệ thống! Vui lòng thử lại.");
            request.getRequestDispatcher("Login/login.jsp").forward(request, response);
        }
    }
}
