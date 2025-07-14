<%@page import="model.Coupon"%>
<%@page import="service.CouponService"%>
<%@page import="model.UserCoupon"%>
<%@page import="java.util.List"%>
<%@page import="model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    List<UserCoupon> userCoupons = (List<UserCoupon>) request.getAttribute("userCoupons");
    CouponService couponService = (CouponService) request.getAttribute("couponService");
    User user = (User) session.getAttribute("user");
    String name = user.getName();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>My Profile | DIY Store</title>
        <!-- Boxicons CDN -->
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&family=Turret+Road:wght@300;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css" />
        <style>
            /* Profile card chỉnh đẹp */
            .profile-section {
                max-width: 940px;
                margin: 200px auto 150px;
                background: #fff;
                border-radius: 28px;
                box-shadow: 0 8px 42px rgba(40,32,90,0.13);
                padding: 44px 40px 32px 44px;
                display: flex;
                gap: 48px;
                align-items: flex-start;
                font-family: 'Poppins', sans-serif;
            }
            .profile-sidebar {
                min-width: 230px;
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 16px;
            }
            .profile-avatar {
                width: 118px;
                height: 118px;
                border-radius: 50%;
                object-fit: cover;
                box-shadow: 0 2px 10px #c9c7f7;
                margin-bottom: 7px;
            }
            .profile-name {
                font-size: 1.35rem;
                font-weight: 700;
                margin-bottom: 2px;
            }
            .profile-email {
                color: #7e8698;
                font-size: 1.07rem;
                margin-bottom: 8px;
            }
            .profile-btn {
                background: #181c2b;
                color: #fff;
                border: none;
                border-radius: 16px;
                padding: 10px 24px;
                margin-top: 18px;
                cursor: pointer;
                font-weight: 600;
                font-size: 1rem;
                transition: background 0.2s;
                box-shadow: 0 2px 6px rgba(60,40,90,0.05);
                display: flex;
                align-items: center;
                gap: 7px;
            }
            .profile-btn:hover {
                background: #3e4a7b;
            }
            .profile-info {
                flex: 1;
            }
            .profile-title {
                font-size: 1.22rem;
                font-weight: 700;
                color: #181c2b;
                margin-bottom: 22px;
                border-left: 5px solid #F38E22;
                padding-left: 14px;
                letter-spacing: 0.5px;
            }
            .profile-details {
                margin-bottom: 34px;
                font-size: 1.07rem;
            }
            .profile-details div {
                margin-bottom: 6px;
            }
            .profile-details span {
                min-width: 105px;
                display: inline-block;
                font-weight: 600;
                color: #434656;
            }
            .profile-details ul {
                margin: 0;
                padding-left: 16px;
            }
            .profile-details li {
                font-size: 1.06rem;
                color: #4a4e5a;
            }
            .coupon-list {
                display: grid;
                gap: 16px;
            }
            .coupon-card {
                background: #fff6ea;
                border-radius: 18px;
                box-shadow: 0 1px 8px rgba(255, 155, 46, 0.10);
                padding: 20px 28px;
                display: flex;
                align-items: center;
                gap: 28px;
            }
            .coupon-icon {
                font-size: 2.2rem;
                color: #F38E22;
            }
            .coupon-info h4 {
                font-size: 1.1rem;
                font-weight: 700;
                margin: 0;
            }
            .coupon-info p {
                font-size: 1rem;
                color: #666;
                margin: 6px 0 0 0;
            }
            .coupon-status {
                font-weight: 600;
                margin-left: auto;
            }
            .coupon-status.active {
                color: #1b8836;
            }
            .coupon-status.used {
                color: #e83a30;
            }
            .footerUser {
                width: 100%;
                text-align: center;
                padding: 2rem 0 1.2rem 0;
                background: #fff;
                margin-top: 4rem;
                color: #222;
                font-size: 1rem;
                font-weight: 500;
                box-shadow: 0 -2px 14px rgba(0,0,0,0.04);
            }
            .footerUser span {
                color: var(--main-color);
                font-weight: bold;
            }
            @media (max-width: 1050px) {
                .profile-section {
                    flex-direction: column;
                    padding: 24px 9vw;
                    gap: 28px;
                }
                .profile-sidebar {
                    flex-direction: row;
                    gap: 25px;
                }
            }
            @media (max-width: 700px) {
                .profile-section {
                    padding: 10vw 2vw;
                }
                .profile-title {
                    font-size: 1rem;
                    padding-left: 9px;
                }
            }
        </style>
    </head>
    <body>
        <!-- Header đẹp -->
        <header>
            <a href="home" class="logo">3tlCraft <span>X.</span></a>
            <i class='bx bx-menu' id="menu-icon"></i>
            <ul class="navbar">
                <li><a href="${pageContext.request.contextPath}/home">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/home#shop">Shop</a></li>
                <li><a href="${pageContext.request.contextPath}/home#new">New Arrivals</a></li>
                <li><a href="${pageContext.request.contextPath}/home#about">About</a></li>
                <li><a href="${pageContext.request.contextPath}/home#brands">Our Partners</a></li>
                <li><a href="${pageContext.request.contextPath}/home#contact">Contact</a></li>
                <li>
                    <a href="${pageContext.request.contextPath}/user" class="profile-icon-link" title="<%= name%>">
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


        <section class="profile-section">
            <div class="profile-sidebar">
                <img class="profile-avatar" src="${pageContext.request.contextPath}/images/profile.jpg" alt="avatar">
                <div class="profile-name">${user.name}</div>
                <div class="profile-email">${user.email}</div>
                <<form action="${pageContext.request.contextPath}/user" method="get">
                    <input type="hidden" name="action" value="edit"/>
                    <button class="profile-btn" type="submit"><i class='bx bx-edit'></i> Edit Profile</button>
                </form>
                <form action="${pageContext.request.contextPath}/logout" method="post" style="margin-top:10px;">
                    <button class="profile-btn" type="submit"><i class='bx bx-log-out'></i> Logout</button>
                </form>
            </div>
            <div class="profile-info">
                <div class="profile-title">Personal Info</div>
                <div class="profile-details">
                    <div><span>Username:</span> ${user.username}</div>
                    <div><span>Email:</span> ${user.email}</div>
                    <div>
                        <span>Address:</span>
                        <c:choose>
                            <c:when test="${not empty user.address}">
                                <ul>
                                    <c:forEach var="addr" items="${user.address}">
                                        <li>
                                            ${addr.street}
                                            <c:if test="${not empty addr.district}">, ${addr.district}</c:if>
                                            <c:if test="${not empty addr.city}">, ${addr.city}</c:if>
                                            </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                Chưa có địa chỉ
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div><span>Role:</span> ${user.role}</div>
                    <div><span>Status:</span> <c:out value="${user.status ? 'Active' : 'Inactive'}"/></div>
                </div>
                <div class="profile-title" style="margin-top:12px;">Your Coupons</div>
                <div class="coupon-list">
                    <% if (userCoupons != null && !userCoupons.isEmpty()) { %>
                    <% for (UserCoupon uc : userCoupons) {
                            Coupon coupon = couponService.findByCode(uc.getCouponCode());
                            boolean used = uc.getUsageCount() > 0 || uc.getUsedAt() != null;
                    %>
                    <div class="coupon-card">
                        <i class='bx bxs-purchase-tag coupon-icon'></i>
                        <div class="coupon-info">
                            <h4>
                                <%= coupon.getCode()%> - 
                                <%= coupon.isIsPercent() ? coupon.getDiscountValue() + "%" : "$" + coupon.getDiscountValue()%> OFF
                            </h4>
                            <p>
                                <% if (coupon.getExpiredAt() != null) {%>
                                Expires: 
                                <%= java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(coupon.getExpiredAt())%>
                                <% }%>
                            </p>
                        </div>
                        <div class="coupon-status <%= used ? "used" : "active"%>">
                            <%= used ? "Used" : "Active"%>
                        </div>
                    </div>
                    <% } %>
                    <% } else { %>
                    <div class="coupon-card" style="background: #fff6ea;">
                        <i class='bx bx-gift coupon-icon'></i>
                        <div class="coupon-info">
                            <h4>No coupons found!</h4>
                            <p>Check back later for new rewards.</p>
                        </div>
                    </div>
                    <% }%>
                </div>
            </div>
        </section>

        <footer class="footerUser">
            <p>&copy; <span>3tlCraft X</span>. All rights reserved.</p>
        </footer>


        <script>
            document.addEventListener("DOMContentLoaded", function () {
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
