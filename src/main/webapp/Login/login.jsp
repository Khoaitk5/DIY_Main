<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en" >
    <head>
        <meta charset="UTF-8">
        <title>Login or Sign Up | DIY Store</title>
        <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.0/css/bootstrap.min.css'>
        <link rel='stylesheet' href='https://unicons.iconscout.com/release/v2.1.9/css/unicons.css'>
        <link rel="stylesheet" href="css/login.css">
    </head>
    <body>


        <div class="section">
            <div class="container">
                <div class="row full-height justify-content-center">
                    <div class="col-12 text-center align-self-center py-5">
                        <div class="section pb-5 pt-5 pt-sm-2 text-center">
                            <h6 class="mb-0 pb-3"><span>Log In </span><span>Sign Up</span></h6>
                            <input class="checkbox" type="checkbox" id="reg-log" name="reg-log"/>
                            <label for="reg-log"></label>
                            <div class="card-3d-wrap mx-auto">
                                <div class="card-3d-wrapper">
                                    <div class="card-front">
                                        <div class="center-wrap">
                                            <div class="section text-center">
                                                <h4 class="login">Log In</h4>
                                                <c:if test="${not empty message}">
                                                    <div class="alert alert-danger" style="color: red; margin-bottom: 10px;">
                                                        ${message}
                                                    </div>
                                                </c:if>
                                                <form action="login" method="post">
                                                    <input type="hidden" name="action" value="login" />
                                                    <div class="form-group">
                                                        <input type="text" name="username" class="form-style" placeholder="Your Username" id="logusername" autocomplete="off" required>
                                                        <i class="input-icon uil uil-at"></i>
                                                    </div>  
                                                    <div class="form-group mt-2">
                                                        <input type="password" name="password" class="form-style" placeholder="Your Password" id="logpass-login" autocomplete="off" required>
                                                        <i class="input-icon uil uil-lock-alt"></i>
                                                    </div>
                                                    <button type="submit" class="btn mt-4">submit</button>
                                                </form>


                                                <!-- Bắt đầu thêm nút đăng nhập Google -->
                                                <div style="margin-top:10px;">
                                                    <p>Or login with</p>
                                                    <a aria-label="Log in with Google"
                                                       class="btn btn-outline-primary"
                                                       href="https://accounts.google.com/o/oauth2/auth?client_id=688136618761-ap2mjqjblu5nnn047natijpnvk2ek882.apps.googleusercontent.com&redirect_uri=http://localhost:8080/DIY_Main/oauth2callback&response_type=code&scope=email%20profile&prompt=select_account"
                                                       style="display:inline-flex; align-items:center; justify-content:center;">
                                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" style="height:20px; width:20px; margin-right:8px;">
                                                        <path d="M16.318 13.714v5.484h9.078c-0.37 2.354-2.745 6.901-9.078 6.901-5.458 0-9.917-4.521-9.917-10.099s4.458-10.099 9.917-10.099c3.109 0 5.193 1.318 6.38 2.464l4.339-4.182c-2.786-2.599-6.396-4.182-10.719-4.182-8.844 0-16 7.151-16 16s7.156 16 16 16c9.234 0 15.365-6.49 15.365-15.635 0-1.052-0.115-1.854-0.255-2.651z"></path>
                                                        </svg>
                                                        Login with Google
                                                    </a>
                                                </div>
                                                <!-- Kết thúc thêm nút đăng nhập Google -->

                                                <p class="mb-0 mt-4 text-center"><a href="#0" class="link">Forgot your password?</a></p>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Phần đăng ký (card-back) -->
                                    <div class="card-back">
                                        <div class="center-wrap">
                                            <div class="section text-center">
                                                <h4 class="mb-4 pb-3">Sign Up</h4>
                                                <form action="signup" method="post"> 
                                                    <div class="form-group mt-2">
                                                        <input type="text" name="fullname" class="form-style" placeholder="Your Full Name (English)" id="fullname_en" autocomplete="off" required>
                                                        <i class="input-icon uil uil-user"></i>
                                                    </div>
                                                    <div class="form-group mt-2">
                                                        <input type="email" name="email" class="form-style" placeholder="Your Email" id="logemail" autocomplete="off" required>
                                                        <i class="input-icon uil uil-at"></i>
                                                    </div>  
                                                    <div class="form-group mt-2">
                                                        <input type="password" name="password" class="form-style" placeholder="Your Password" id="logpass-signup" autocomplete="off" required>
                                                        <i class="input-icon uil uil-lock-alt"></i>
                                                    </div>
                                                    <button type="submit" class="btn mt-4">submit</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div> <!-- section -->
                    </div> <!-- col -->
                </div> <!-- row -->
            </div> <!-- container -->
        </div> <!-- section -->

        <!-- partial -->
        <script>

            document.addEventListener('DOMContentLoaded', function () {
                // 1. Chuyển đổi tab login/signup
                const regLogCheckbox = document.getElementById('reg-log');
                const tabs = document.querySelectorAll('h6 span');
                if (tabs.length === 2) {
                    tabs[0].addEventListener('click', function () {
                        regLogCheckbox.checked = false;
                    });
                    tabs[1].addEventListener('click', function () {
                        regLogCheckbox.checked = true;
                    });
                }


                // 3. Kiểm tra password signup (ví dụ)
                const signupForm = document.querySelector('form[action="signupServlet"]');
                if (signupForm) {
                    signupForm.addEventListener('submit', function (e) {
                        const password = signupForm.querySelector('input[name="password"]').value;
                        if (password.length < 6) {
                            alert('Password phải từ 6 ký tự trở lên!');
                            e.preventDefault();
                        }
                    });
                }

                // 4. Autofocus input khi chuyển tab
                regLogCheckbox.addEventListener('change', function () {
                    if (this.checked) {
                        document.getElementById('fullname_en').focus();
                    } else {
                        document.getElementById('logusername').focus();
                    }
                });
            });

        </script>

    </body>
</html>
