<%@page import="model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/");
        return;
    }
    String name = user.getName();
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Vòng Quay May Mắn</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            @import url("https://fonts.googleapis.com/css?family=Open+Sans:600,300");

            :root {
                --text-color: #111111;
                --main-color: #83B735;
            }

            * {
                box-sizing: border-box;
            }

            html, body {
                height: 100%;
                margin: 0;
                font-family: 'Open Sans', sans-serif;
            }

            .wrapper {
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }

            body {
                background-color: #f3f3f3;
            }

            header {
                position: fixed;
                width: 100%;
                top: 0;
                z-index: 1000;
                background: #f3f3f3;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
            }

            .header-inner {
                max-width: 1200px;
                margin: 0 auto;
                padding: 10px 20px;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .logo {
                font-size: 1.3rem;
                font-weight: 700;
                color: var(--text-color);
                text-decoration: none;
            }
            .logo span {
                color: var(--main-color);
            }

            .navbar {
                display: flex;
                align-items: center;
                list-style: none;
                margin: 0;
                padding: 0;
                gap: 10px;
            }
            .navbar li {
                display: flex;
                align-items: center;
            }
            .navbar a {
                font-size: 1rem;
                padding: 6px 12px;
                font-weight: 600;
                color: var(--text-color);
                text-decoration: none;
                transition: all .3s ease;
            }
            .navbar a:hover {
                color: var(--main-color);
            }

            .profile-icon-link {
                display: inline-block;
            }
            .profile-icon-img {
                width: 30px;
                height: 30px;
                border-radius: 50%;
                border: 2px solid #f1f1f1;
                object-fit: cover;
                background: #fff;
                transition: border .2s;
            }
            .profile-icon-img:hover {
                border: 2px solid #F38E22;
            }

            .cart-wrapper {
                display: flex;
                align-items: center;
                justify-content: center;
                position: relative;
            }
            .cart-icon {
                font-size: 1.8rem;
                color: #181818;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                transition: color .2s, transform .18s;
            }
            .cart-icon:hover {
                color: var(--main-color);
                transform: scale(1.08) rotate(-6deg);
            }
            .cart-count {
                position: absolute;
                top: -10px;
                right: -10px;
                min-width: 20px;
                height: 20px;
                background: #ff2323;
                color: #fff;
                font-size: 0.8rem;
                font-weight: bold;
                border-radius: 50%;
                border: 2px solid #fff;
                box-shadow: 0 2px 8px #e44c;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .main-container {
                flex: 1;
                display: flex;
                justify-content: center;
                gap: 2rem;
                padding: 120px 2rem 2rem;
                flex-wrap: wrap;
            }

            .rule__content {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
            }
            .rule__color {
                width: 50px;
                height: 50px;
                margin-right: 10px;
            }
            .color-1 {
                background-color: #16a085;
            }
            .color-2 {
                background-color: #2980b9;
            }
            .color-3 {
                background-color: #34495e;
            }
            .color-4 {
                background-color: #f39c12;
            }
            .color-5 {
                background-color: #d35400;
            }
            .color-6 {
                background-color: #c0392b;
            }

            .wheel {
                width: 312px;
                height: 312px;
                border-radius: 50%;
                border: 6px solid #fff;
                box-shadow: 0 4px 9px rgba(0, 0, 0, 0.1);
                position: relative;
            }
            .wheel__inner {
                width: 300px;
                height: 300px;
                border-radius: 50%;
                overflow: hidden;
                position: relative;
                transition: cubic-bezier(0.19, 1, 0.22, 1) 5s;
            }
            .wheel__sec {
                position: absolute;
                top: 0;
                left: 62px;
                width: 0;
                height: 0;
                border: solid;
                border-width: 150px 88px 0;
                border-color: transparent;
                transform-origin: 50% 100%;
            }
            .wheel__sec:nth-child(1) {
                border-top-color: #16a085;
            }
            .wheel__sec:nth-child(2) {
                border-top-color: #2980b9;
                transform: rotate(60deg);
            }
            .wheel__sec:nth-child(3) {
                border-top-color: #34495e;
                transform: rotate(120deg);
            }
            .wheel__sec:nth-child(4) {
                border-top-color: #f39c12;
                transform: rotate(180deg);
            }
            .wheel__sec:nth-child(5) {
                border-top-color: #d35400;
                transform: rotate(240deg);
            }
            .wheel__sec:nth-child(6) {
                border-top-color: #c0392b;
                transform: rotate(300deg);
            }

            .wheel__arrow {
                width: 70px;
                height: 70px;
                background: #fff;
                position: absolute;
                top: 121px;
                left: 115px;
                border-radius: 50%;
                display: flex;
                justify-content: center;
                align-items: center;
            }
            .wheel__arrow::after {
                content: '';
                position: absolute;
                top: -15px;
                left: 25px;
                border: solid;
                border-width: 0 10px 20px;
                border-color: transparent;
                border-bottom-color: #fff;
            }
            .wheel__button {
                width: 60px;
                height: 60px;
                background: lightgray;
                border: none;
                border-radius: 50%;
                font-weight: 600;
                font-size: 18px;
                cursor: pointer;
                transition: 0.3s;
            }
            .wheel__button:hover {
                color: #27AE60;
            }

            footer {
                width: 100%;
                text-align: center;
                padding: 1.5rem 0;
                background: #fff;
                color: #222;
                font-size: 1rem;
                font-weight: 500;
                box-shadow: 0 -2px 14px rgba(0,0,0,0.04);
            }
            footer span {
                color: var(--main-color);
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="wrapper">
            <header>
                <div class="header-inner" style="max-width: 1280px; margin: 0 auto; padding: 14px 20px; display: flex; align-items: center; justify-content: space-between;">
                    <a href="${pageContext.request.contextPath}/home" class="logo" style="font-size: 1.3rem; font-weight: 700; color: #111; text-decoration: none;">
                        3tlCraft <span style="color: #83B735;">X.</span>
                    </a>
                    <ul class="navbar" style="display: flex; align-items: center; list-style: none; margin: 0; padding: 0; gap: 1rem;">
                        <li><a href="${pageContext.request.contextPath}/home">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/home#shop">Shop</a></li>
                        <li><a href="${pageContext.request.contextPath}/home#new">New Arrivals</a></li>
                        <li><a href="${pageContext.request.contextPath}/home#about">About</a></li>
                        <li><a href="${pageContext.request.contextPath}/home#brands">Our Partners</a></li>
                        <li><a href="${pageContext.request.contextPath}/home#contact">Contact</a></li>
                        <li>
                            <a href="${pageContext.request.contextPath}/user" class="profile-icon-link" title="<%= name%>" style="display: inline-flex; align-items: center;">
                                <i class="fas fa-user-circle" style="font-size: 22px; color: #111;"></i>
                            </a>
                        </li>
                        <li class="cart-wrapper" style="display: flex; align-items: center; position: relative;">
                            <a href="${pageContext.request.contextPath}/carts?action=view" class="cart-icon" tabindex="0" style="position: relative; font-size: 1.8rem; color: #111; display: inline-flex; align-items: center; justify-content: center;">
                                <i class="fas fa-shopping-cart"></i>
                                <span class="cart-count" style="position: absolute; top: -10px; right: -10px; min-width: 20px; height: 20px; background: #ff2323; color: #fff; font-size: 0.8rem; font-weight: bold; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 2px 8px #e44c; display: flex; align-items: center; justify-content: center;">
                                    ${cartCount}
                                </span>
                            </a>
                        </li>
                    </ul>
                </div>
            </header>


            <main class="main-container">
                <div class="rule">
                    <div class="rule__content"><div class="rule__color color-1"></div><div class="rule__text">1 CĂN NHÀ LẦU 4 TẦNG</div></div>
                    <div class="rule__content"><div class="rule__color color-2"></div><div class="rule__text">1 CHUYẾN DU LỊCH MIỀN TÂY</div></div>
                    <div class="rule__content"><div class="rule__color color-3"></div><div class="rule__text">1 THẺ CÀO 100K</div></div>
                    <div class="rule__content"><div class="rule__color color-4"></div><div class="rule__text">1 THẺ CÀO 200K</div></div>
                    <div class="rule__content"><div class="rule__color color-5"></div><div class="rule__text">CHÚC BẠN MAY MẮN LẦN SAU</div></div>
                    <div class="rule__content"><div class="rule__color color-6"></div><div class="rule__text">1 CHUYẾN DU LỊCH VŨNG TÀU</div></div>
                </div>
                <div class="wheel">
                    <div class="wheel__inner">
                        <div class="wheel__sec"></div><div class="wheel__sec"></div><div class="wheel__sec"></div>
                        <div class="wheel__sec"></div><div class="wheel__sec"></div><div class="wheel__sec"></div>
                    </div>
                    <div class="wheel__arrow">
                        <button class="wheel__button">QUAY</button>
                    </div>
                </div>
            </main>

            <footer>
                <p>&copy; <span>3tlCraft X</span>. All rights reserved.</p>
            </footer>
        </div>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            $(function () {
                let value = 0, countClicked = 0, clicked = false;
                function getPosition(position) {
                    let result = '';
                    if (position <= 30)
                        result = '1 NHÀ LẦU 4 TẦNG';
                    else if (position <= 90)
                        result = '1 CHUYẾN DU LỊCH VŨNG TÀU';
                    else if (position <= 150)
                        result = 'CHÚC BẠN MAY MẮN LẦN SAU';
                    else if (position <= 210)
                        result = '1 THẺ CÀO 200K';
                    else if (position <= 270)
                        result = '1 THẺ CÀO 100K';
                    else if (position <= 330)
                        result = '1 CHUYẾN DU LỊCH MIỀN TÂY';
                    else
                        result = '1 NHÀ LẦU 4 TẦNG';
                    alert('CHÚC MỪNG! BẠN TRÚNG ' + result);
                    clicked = false;
                    countClicked = 0;
                }
                $('.wheel__button').click(function () {
                    if (clicked) {
                        alert(countClicked < 3 ? 'Ngừng phá đi men!' : 'Lì quá ngen!');
                        countClicked++;
                    } else {
                        let random = Math.floor((Math.random() * 360) + 720);
                        value += random;
                        $(".wheel__inner").css("transform", `rotate(${value}deg)`);
                        setTimeout(() => getPosition(value % 360), 5000);
                        clicked = true;
                    }
                });
            });
        </script>
    </body>
</html>
