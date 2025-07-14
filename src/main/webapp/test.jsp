<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.CartItem" %>

<%
    List<CartItem> cartItems = (List<CartItem>) request.getAttribute("cartItems");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Xoá sản phẩm (POST)</title>
</head>
<body>
    <h2>Danh sách Product ID</h2>

    <%
        if (cartItems != null) {
            for (int i = 0; i < cartItems.size(); i++) {
                int id = cartItems.get(i).getProduct().getId();
    %>
                <div style="margin-bottom: 10px;">
                    <p>Sản phẩm <%= (i + 1) %> - ID: <%= id %></p>
                    
                    <!-- Form POST -->
                    <form method="post" action="carts?action=remove" onsubmit="return confirm('Bạn có chắc muốn xoá sản phẩm này?')">
                        <input type="hidden" name="productId" value="<%= id %>" />
                        <button type="submit">Xoá</button>
                    </form>
                </div>
    <%
            }
        } else {
    %>
        <p style="color:red;">Không có cartItems nào được truyền vào!</p>
    <%
        }
    %>
</body>
</html>
