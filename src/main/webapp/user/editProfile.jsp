<%@page import="model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    User user = (User) session.getAttribute("user");
    String name = user.getName();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Profile</title>
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css" />
        <style>
            .edit-section {
                max-width: 540px;
                margin: 200px auto;
                background: #fff;
                border-radius: 24px;
                box-shadow: 0 2px 12px #ccc;
                padding: 36px 28px;
            }
            .edit-title {
                font-size: 1.3rem;
                font-weight: 700;
                margin-bottom: 22px;
                color: #181c2b;
            }
            .edit-form label {
                display:block;
                margin-bottom: 4px;
                color: #444;
                font-weight:600;
            }
            .edit-form input {
                width: 100%;
                padding: 10px 12px;
                border-radius: 10px;
                border: 1px solid #ddd;
                margin-bottom: 18px;
            }
            .edit-btn {
                background: #F38E22;
                color: #fff;
                border: none;
                padding: 10px 28px;
                border-radius: 14px;
                font-weight: 700;
                cursor:pointer;
                transition: background .18s;
            }
            .edit-btn:hover {
                background: #181c2b;
            }
            .msg-success {
                color: #22b573;
                font-weight: 600;
                margin-bottom: 18px;
            }
            .msg-error {
                color: #e83a30;
                font-weight: 600;
                margin-bottom: 18px;
            }
            .address-block {
                background: #f8f8fa;
                padding: 14px 18px;
                margin-bottom: 14px;
                border-radius: 13px;
            }
            .address-block label {
                margin-bottom: 0;
                display:inline-block;
                width:auto;
                margin-right:12px;
            }
            .address-block input {
                width: 140px;
                margin-bottom:0;
                margin-right:7px;
            }
            .address-title {
                font-weight:700;
                margin-bottom:7px;
                color:#444;
            }
            .add-address-btn {
                background:#27c271;
                color:#fff;
                border:none;
                border-radius:8px;
                padding:7px 14px;
                font-weight:600;
                cursor:pointer;
            }
            .add-address-btn:hover {
                background:#1a9251;
            }
            .remove-address-btn {
                background:#e83a30;
                color:#fff;
                border:none;
                border-radius:8px;
                padding:6px 10px;
                font-size:.9rem;
                font-weight:600;
                cursor:pointer;
            }
            .remove-address-btn:hover {
                background:#bd2210;
            }
        </style>
    </head>
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
    <body>
        <div class="edit-section">
            <div class="edit-title"><i class='bx bx-user'></i> Edit Profile</div>
            <c:if test="${not empty success}">
                <div class="msg-success">${success}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="msg-error">${error}</div>
            </c:if>
            <form class="edit-form" action="${pageContext.request.contextPath}/user" method="post" id="editProfileForm">
                <label for="name">Full Name</label>
                <input id="name" name="name" type="text" value="${user.name}" required>
                <label for="email">Email</label>
                <input id="email" name="email" type="email" value="${user.email}" required>
                <label for="role">Role (không sửa được)</label>
                <input id="role" name="role" type="text" value="${user.role}" readonly>
                <label for="password">New Password (để trống nếu không đổi):</label>
                <input id="password" name="password" type="password" autocomplete="new-password">
                <label for="repassword">Confirm Password:</label>
                <input id="repassword" name="repassword" type="password" autocomplete="new-password">

                <div class="address-title" style="margin-top:20px;">Addresses</div>
                <div id="address-list">
                    <c:forEach var="addr" items="${user.address}" varStatus="loop">
                        <div class="address-block" data-index="${loop.index}">
                            <!-- Quan trọng: input ẩn lưu id -->
                            <input type="hidden" name="addressId${loop.index}" value="${addr.id}"/>
                            <label>Street:
                                <input type="text" name="street${loop.index}" value="${addr.street}" required>
                            </label>
                            <label>District:
                                <input type="text" name="district${loop.index}" value="${addr.district}">
                            </label>
                            <label>City:
                                <input type="text" name="city${loop.index}" value="${addr.city}">
                            </label>
                            <button type="button" class="remove-address-btn" onclick="removeAddress(this)">Remove</button>
                        </div>
                    </c:forEach>
                </div>
                <button type="button" class="add-address-btn" onclick="addAddressBlock()">+ Add address</button>

                <button class="edit-btn" type="submit" style="margin-top:20px;"><i class='bx bx-save'></i> Save</button>
            </form>
        </div>
        <script>
            let addressCount = ${fn:length(user.address)};
            function addAddressBlock() {
                const container = document.getElementById("address-list");
                const html = `
            <div class="address-block" data-index="\${addressCount}">
                <label>Street:
                    <input type="text" name="street\${addressCount}" required>
                </label>
                <label>District:
                    <input type="text" name="district\${addressCount}">
                </label>
                <label>City:
                    <input type="text" name="city\${addressCount}">
                </label>
                <button type="button" class="remove-address-btn" onclick="removeAddress(this)">Xóa</button>
            </div>`;
                container.insertAdjacentHTML('beforeend', html);
                addressCount++;
            }
            function removeAddress(btn) {
                // Lấy addressId nếu có
                const block = btn.closest('.address-block');
                const hiddenId = block.querySelector('input[name^="addressId"]');
                if (hiddenId) {
                    // Thêm input ẩn vào form để báo với backend là cần xóa id này
                    const form = document.getElementById('editProfileForm');
                    const deleted = document.createElement('input');
                    deleted.type = "hidden";
                    deleted.name = "deleteAddressId";
                    deleted.value = hiddenId.value;
                    form.appendChild(deleted);
                }
                block.remove();
            }
            document.getElementById('editProfileForm').onsubmit = function () {
                const pw = document.getElementById('password').value;
                const repw = document.getElementById('repassword').value;
                if (pw !== '' && pw !== repw) {
                    alert('Password confirmation does not match!');
                    return false;
                }
                return true;
            };
        </script>
    </body>
</html>
