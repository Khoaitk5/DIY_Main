<%@page import="model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    User user = (User) session.getAttribute("user");
    if(user == null){
        response.sendRedirect(request.getContextPath() + "/");
        return;
    }
    String name = user.getName();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>DIY Store | Build, Craft & Create Your Way</title>
        <!-- Boxicons CDN -->
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&family=Turret+Road:wght@300;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="css/home.css" />
    </head>
    <body>
        <header>
            <a href="home" class="logo">3tlCraft <span>X.</span></a>
            <i class='bx bx-menu' id="menu-icon"></i>
            <ul class="navbar">
                <li><a href="#home">Home</a></li>
                <li><a href="#shop">Shop</a></li>
                <li><a href="#new">New Arrivals</a></li>
                <li><a href="#about">About</a></li>
                <li><a href="#brands">Our Partners</a></li>
                <li><a href="#contact">Contact</a></li>
                <li>
                    <a href="${pageContext.request.contextPath}/user" class="profile-icon-link" title="<%= name %>">
                        <img src="${pageContext.request.contextPath}/images/profile.jpg" alt="Profile" class="profile-icon-img"/>
                    </a>
                </li>

                <li class="cart-wrapper">
                    <a href="${pageContext.request.contextPath}/carts?action=view" class="cart-icon" tabindex="0">
                        <i class='bx bx-cart'></i>
                        <span class="cart-count">${cartCount}</span>
                    </a>
                    <div class="cart-dropdown">
                        <h4 class="cart-title">New Arrivals</h4>
                        <c:forEach var="item" items="${recentCartItems}" varStatus="loop" begin="0" end="4">
                            <div class="cart-item">
                                <img src="${pageContext.request.contextPath}/${item.product.imageUrl}" alt="${item.product.name}">
                                <div class="cart-item-info">
                                    <p>${item.product.name}</p>
                                    <span>${item.product.price}$</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </li>

            </ul>
        </header>

        <section class="home" id="home">
            <div class="home-text">
                <h1>Your <span>Ideas</span> <br> Become Our <span>Inspiration</span></h1>
                <p>We empower makers of all levels to build, create, and express themselves through DIY</p>
                <a href="#shop" class="btn">Shop Now</a>
            </div>
        </section>

        <section class="shop" id="shop">
            <div class="heading">
                <span>New Arrival</span>
                <h2><a href="${pageContext.request.contextPath}/products">Shop Now</a></h2>
            </div>
            <div class="shop-container">
                <c:forEach var="product" items="${shopnow}">
                    <div class="box">
                        <div class="box-img">
                            <a href="${pageContext.request.contextPath}/products?action=details&id=${product.id}">
                                <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                            </a>
                        </div>
                        <div class="title-price">
                            <h3>${product.name}</h3>
                            <div class="stars">
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star-half'></i>
                            </div>
                        </div>
                        <span>$${product.price}</span>
                        <!-- Nút thêm giỏ hàng bằng JS -->
                        <c:choose>
                            <c:when test="${product.stockQuantity > 0}">
                                <form action="${pageContext.request.contextPath}/carts" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="insert">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <input type="hidden" name="quantity" value="1">
                                    <button type="submit" style="all: unset; cursor: pointer;">
                                        <i class='bx bx-cart'></i>
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button disabled style="all: unset; cursor: not-allowed;" title="Out of stock">
                                    <i class='bx bx-cart'></i>
                                </button>
                                <span style="color: red;">Out of stock</span>
                            </c:otherwise>
                        </c:choose>



                    </div>
                </c:forEach>

            </div>


            <!-- PHÂN TRANG -->
            <div style="text-align:center; margin: 20px 0;">
                <c:if test="${totalPages > 1}">
                    <nav class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="home?page=${currentPage - 1}#shop">Prev</a>
                        </c:if>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="home?page=${i}#shop"
                               style="${i == currentPage ? 'font-weight:bold; text-decoration:underline;' : ''}">
                                ${i}
                            </a>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <a href="home?page=${currentPage + 1}#shop">Next</a>
                        </c:if>
                    </nav>
                </c:if>
            </div>
        </section>


        <section class="new" id="new">
            <div class="heading">
                <span>New Collection</span>
                <h2>Best Selling</h2>
            </div>
            <div class="new-container">
                <c:forEach var="product" items="${bestseller}">
                    <div class="box">
                        <div class="box-img">
                            <a href="${pageContext.request.contextPath}/products?action=details&id=${product.id}">
                                <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                            </a>
                        </div>
                        <div class="title-price">
                            <h3>${product.name}</h3>
                            <div class="stars">
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star'></i>
                                <i class='bx bxs-star-half'></i>
                            </div>
                        </div>
                        <span>$${product.price}</span>
                        <c:choose>
                            <c:when test="${product.stockQuantity > 0}">
                                <form action="${pageContext.request.contextPath}/carts" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="insert">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <input type="hidden" name="quantity" value="1">
                                    <button type="submit" style="all: unset; cursor: pointer;">
                                        <i class='bx bx-cart'></i>
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button disabled style="all: unset; cursor: not-allowed;" title="Out of stock">
                                    <i class='bx bx-cart'></i>
                                </button>
                                <span style="color: red;">Out of stock</span>
                            </c:otherwise>
                        </c:choose>


                    </div>
                </c:forEach>
            </div>
        </section>



        <section class="about" id="about">
            <div class="about-img">
                <img src="https://i.postimg.cc/jjtCJp6P/about.jpg" alt="image">
            </div>
            <div class="about-text">
                <span>About us</span>
                <h2>Make It Yours with DIY</h2>
                <p>Love building things by hand? So do we. Whether you're refreshing your space, crafting something special, or just exploring a new hobby — we’re here to help you make it happen.</p>
                <p>From furniture and home decor to tools, materials, and project kits, you’ll find everything you need to start creating. No experience? No problem. Our step-by-step guides and ready-to-go kits make DIY simple and fun.</p>
                <a href="#shop" class="btn">Learn More</a>
            </div>
        </section>

        <section class="brands" id="brands">
            <div class="heading">
                <span>Brands</span>
                <h2>Our Brand Partners</h2>
            </div>
            <div class="brands-container">
                <img src="https://i.postimg.cc/JzPtZTpF/Google.png" alt="brand image">
                <img src="https://i.postimg.cc/sxYgXKBw/amazon.png" alt="brand image">
                <img src="https://i.postimg.cc/GhRpN0NN/netflix.png" alt="brand image">
                <img src="https://i.postimg.cc/jSgdLD50/tesla.png" alt="brand image">
                <img src="https://i.postimg.cc/hGX4xKzJ/starbucks.png" alt="brand image">
                <img src="https://i.postimg.cc/G3FLv72j/zoom.png" alt="brand image">
            </div>
        </section>

        <section class="newsletter" id="contact">
            <h2>Subscribe to Newsletter</h2>
            <div class="news-box">
                <input type="text" placeholder="Enter your email">
                <a href="#" class="btn">Subscribe</a>
            </div>
        </section>

        <section class="footer" id="footer">
            <div class="footer-box">
                <h2>Brand <span>X</span></h2>
                <p>Invest in your comfort and happiness.</p>
                <div class="social">
                    <a href="#"><i class='bx bxl-facebook'></i></a>
                    <a href="#"><i class='bx bxl-twitter'></i></a>
                    <a href="#"><i class='bx bxl-instagram'></i></a>
                </div>
            </div>
            <div class="footer-box">
                <h3>Services</h3>
                <li><a href="#">Product</a></li>
                <li><a href="#">Help & Support</a></li>
                <li><a href="#">Pricing</a></li>
                <li><a href="#">FAQ</a></li>
            </div>
            <div class="footer-box">
                <h3>Product</h3>
                <li><a href="#">Sofa's</a></li>
                <li><a href="#">Chair's</a></li>
                <li><a href="#">Living Room</a></li>
                <li><a href="#">Office</a></li>
            </div>
            <div class="footer-box contact-info">
                <h3>Contact</h3>
                <span>New York City, USA 10004</span>
                <span>+1 100 1004 0001</span>
                <span>brian@brandx.com</span>
            </div>
        </section>

        <div class="copyright">
            <p>&#169; <span>ULTRA CODE</span>. All rights reserved</p>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                // Xử lý menu
                const menu = document.querySelector('#menu-icon');
                const navbar = document.querySelector('.navbar');

                if (menu && navbar) {
                    menu.onclick = () => {
                        navbar.classList.toggle('active');
                    };

                    window.onscroll = () => {
                        navbar.classList.remove('active');
                    };
                }

            });
        </script>


    </body>
</html>

