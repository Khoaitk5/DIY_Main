<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Product" %>
<%@ page import="model.Category" %>
<%
    Product product = (Product) request.getAttribute("product");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Product Details</title>
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&family=Turret+Road:wght@300;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="css/productDetails.css" />
    </head>
    <body>
        <header>
            <a href="home" class="logo">3tlCraft <span>X.</span></a>
        </header>

        <div class="container">
            <div class="product-img">
                <img src="<%= product.getImageUrl()%>" alt="<%= product.getName()%>">
            </div>
            <div class="product-details">
                <h2><%= product.getName()%></h2>
                
                <%
                    if (product.getTags() != null && !product.getTags().isEmpty()) {
                %>
                <div style="margin-bottom: 1rem;">
                    <% for (model.Tag tag : product.getTags()) {%>
                    <span style="display:inline-block;background:#f4f4f4;color:#83B735;border-radius:6px;padding:4px 12px;font-size:0.92rem;margin-right:6px;margin-bottom:3px;font-weight:500;">
                        <%= tag.getName()%>
                    </span>
                    <% } %>
                </div>
                <%
                    }
                %>

                <div class="product-rating">
                    <i class='bx bxs-star'></i>
                    <i class='bx bxs-star'></i>
                    <i class='bx bxs-star'></i>
                    <i class='bx bxs-star'></i>
                    <i class='bx bx-star'></i>
                    <span>4/5</span>
                </div>
                <p class="price">$<%= product.getPrice()%></p>
                <p><strong>Category:</strong> <%= product.getCategory().getName()%></p>
                <p><strong>Stock:</strong> <%= product.getStockQuantity()%> available</p>
                <p>
                    <i class='bx bx-info-circle'></i>
                    <strong>Description:</strong>
                    <span><%= product.getDescription()%></span>
                </p>
                <form action="carts?action=insert" method="post">
                    <input type="hidden" name="productId" value="<%= product.getId()%>"/>
                    <label for="quantity">Quantity:</label>
                    <input type="number" id="quantity" name="quantity" value="1" min="1" max="<%= product.getStockQuantity()%>"/>
                    <button type="submit" class="btn"><i class='bx bx-cart'></i> Add to Cart</button>
                </form>
                <a href="products" class="btn">Back to Shop</a>
            </div>
        </div>

        <footer>
            <p>&copy; <span>3tlCraft X</span>. All rights reserved.</p>
        </footer>
    </body>
</html>
