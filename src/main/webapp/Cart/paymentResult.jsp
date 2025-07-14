<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction Result</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" 
          integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA==" 
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --text-color: #212121;
            --main-color: #83B735;
            --success-color: #28a745;
            --fail-color: #dc3545;
            --warning-color: #ffc107;
            --background-color: #F7F6F4;
        }
        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--background-color);
            margin: 0;
            padding: 0;
            color: var(--text-color);
        }
        header {
            width: 100%;
            background: #fff;
            padding: 1.2rem 0;
            display: flex;
            justify-content: center;
            align-items: center;
            box-shadow: 0 2px 14px rgba(0,0,0,0.04);
            position: sticky;
            top: 0;
            z-index: 1000;
        }
        .logo {
            font-size: 1.7rem;
            font-weight: 700;
            color: var(--main-color);
            text-decoration: none;
            letter-spacing: 1px;
            transition: color 0.2s;
        }
        .logo span {
            color: #222;
        }
        .logo:hover {
            color: #668820;
        }
        .container {
            max-width: 600px;
            margin: 60px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 4px 32px rgba(0, 0, 0, 0.06);
        }
        .container img {
            width: 120px;
            height: 120px;
            margin-bottom: 20px;
        }
        .container h3 {
            font-weight: bold;
            margin-bottom: 10px;
        }
        .success h3 { color: var(--success-color); }
        .fail h3 { color: var(--fail-color); }
        .processing h3 { color: var(--warning-color); }
        .container p {
            font-size: 18px;
            margin: 10px 0;
        }
        .contact-number {
            color: red;
            font-size: 24px;
            font-weight: bold;
        }
        footer {
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
        footer span {
            color: var(--main-color);
            font-weight: bold;
        }
    </style>
</head>

<body>
<header>
    <a href="home" class="logo">3tlCraft <span>X.</span></a>
</header>
<div class="container">
    <div>
        <img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:150:0/q:90/plain/https://cellphones.com.vn/media/wysiwyg/Review-empty.png" 
             alt="Transaction Status">
    </div>

    <!-- Successful transaction -->
    <c:if test="${transResult}">
        <div class="success">
            <h3>Your transaction was successful! <i class="fas fa-check-circle"></i></h3>
            <p>Our support staff will contact you shortly via:</p>
            <div class="contact-number">0383459560</div>
        </div>
    </c:if>

    <!-- Failed transaction -->
    <c:if test="${transResult == false}">
        <div class="fail">
            <h3>Transaction failed!</h3>
            <p>Thank you for using our service.</p>
            <p>Please contact support for assistance:</p>
            <div class="contact-number">0383456xxx</div>
        </div>
    </c:if>

    <!-- Unknown/Processing -->
    <c:if test="${transResult == null}">
        <div class="processing">
            <h3>Your order has been received. Please wait while we process your transaction.</h3>
            <p>Our support staff will contact you shortly via:</p>
            <div class="contact-number">0383456xxx</div>
        </div>
    </c:if>
</div>
<footer>
    <p>&copy; <span>3tlCraft X</span>. All rights reserved.</p>
</footer>
</body>
</html>
