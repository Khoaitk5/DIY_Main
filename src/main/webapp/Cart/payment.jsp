<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Order, model.OrderItem, model.Product, model.Address, java.util.List" %>
<%
    Order order = (Order) session.getAttribute("orderPreview");
    List<Address> userAddresses = (List<Address>) session.getAttribute("userAddresses");

    if (order == null) {
%>
<h2 style="text-align:center; color:red;">No order preview available. Please return to cart.</h2>
<%
        return;
    }

    List<OrderItem> items = order.getItems();
    double subtotal = 0;
    for (OrderItem item : items) {
        subtotal += item.getUnitPrice() * item.getQuantity();
    }

    double discountAmount = order.getDiscountAmount();
    double totalAmount = order.getTotalAmount();
    String promoCode = order.getCouponCode();
    String userId = String.valueOf(order.getUserId());

%>

<!DOCTYPE html>
<html>
    <head>
        <title>Payment</title>
        <style>
            body {
                background: #eaf6fb;
                font-family: 'Segoe UI', Arial, sans-serif;
                margin: 0;
            }
            .container {
                max-width: 700px;
                margin: 40px auto;
                background: #fff;
                border-radius: 15px;
                box-shadow: 0 8px 24px #b0d6f3;
                padding: 32px;
            }
            h1 {
                color: #3498db;
                text-align: center;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 30px;
            }
            th, td {
                padding: 12px;
                border-bottom: 1px solid #e1eaf4;
            }
            th {
                background: #3498db;
                color: #fff;
            }
            .summary {
                margin-top: 30px;
                padding: 18px;
                background: #f7fafc;
                border-radius: 10px;
            }
            .summary label {
                font-weight: bold;
            }
            .promo {
                color: #16a085;
                font-weight: bold;
            }
            .pay-btn {
                width: 100%;
                margin-top: 28px;
                padding: 16px;
                background: #27ae60;
                color: #fff;
                border: none;
                border-radius: 10px;
                font-size: 1.2em;
                cursor: pointer;
                font-weight: bold;
                transition: background 0.2s;
            }
            .pay-btn:hover {
                background: #219150;
            }
            .address-section {
                margin-top: 30px;
            }
            .address-select, .new-address-form input {
                width: 100%;
                margin-top: 8px;
                padding: 8px;
                border-radius: 5px;
                border: 1px solid #ccc;
            }
            .new-address-form {
                display: none;
                margin-top: 16px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Payment Information</h1>

            <div class="address-section">
                <h3>Select Shipping Address</h3>
                <select id="addressSelect" class="address-select">
                    <% if (userAddresses != null) {
                            for (Address addr : userAddresses) {%>
                    <option value="<%= addr.getId()%>">
                        <%= addr.getStreet()%>, <%= addr.getDistrict()%>, <%= addr.getCity()%>
                    </option>
                    <% }
                        } %>
                    <option value="new">+ Add New Address</option>
                </select>

                <div id="newAddressForm" class="new-address-form">
                    <input type="text" id="newStreet" placeholder="Street" />
                    <input type="text" id="newDistrict" placeholder="District" />
                    <input type="text" id="newCity" placeholder="City" />
                </div>
            </div>

            <h3>Order Preview</h3>
            <table>
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Description</th>
                        <th>Unit Price</th>
                        <th>Quantity</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (OrderItem item : items) {%>
                    <tr>
                        <td><%= item.getProduct().getName()%></td>
                        <td><%= item.getProduct().getDescription()%></td>
                        <td><%= String.format("$%.2f", item.getUnitPrice())%></td>
                        <td><%= item.getQuantity()%></td>
                        <td><%= String.format("$%.2f", item.getUnitPrice() * item.getQuantity())%></td>
                    </tr>
                    <% }%>
                </tbody>
            </table>

            <div class="summary">
                <p>Subtotal: <b><%= String.format("$%.2f", subtotal)%></b></p>
                <p>Promo Code: <span class="promo"><%= promoCode != null && !promoCode.isEmpty() ? promoCode : "(none)"%></span></p>
                <p>Discount: <b><%= String.format("-$%.2f", discountAmount)%></b></p>
                <p style="font-size:1.2em;">Total Amount: <span style="color:#e67e22;font-weight:bold;"><%= String.format("$%.2f", totalAmount)%></span></p>
            </div>

            <button id="vnpay-btn" class="pay-btn">Pay with VNPAY</button>

            <script>
                document.getElementById("addressSelect").addEventListener("change", function () {
                    const newForm = document.getElementById("newAddressForm");
                    if (this.value === "new") {
                        newForm.style.display = "block";
                    } else {
                        newForm.style.display = "none";
                    }
                });

                document.getElementById('vnpay-btn').onclick = function () {
                    const selectedAddressId = document.getElementById("addressSelect").value;
                    const totalBill = "<%= totalAmount%>";
                    const userId = "<%= userId%>";

                    let body = 'totalBill=' + encodeURIComponent(totalBill) + '&userId=' + encodeURIComponent(userId);
                    body += '&promoCode=' + encodeURIComponent("<%= promoCode%>");
                    body += '&discountAmount=' + encodeURIComponent("<%= String.format("%.2f", discountAmount)%>");

                    if (selectedAddressId === 'new') {
                        const street = document.getElementById("newStreet").value;
                        const district = document.getElementById("newDistrict").value;
                        const city = document.getElementById("newCity").value;
                        body += '&newStreet=' + encodeURIComponent(street) +
                                '&newDistrict=' + encodeURIComponent(district) +
                                '&newCity=' + encodeURIComponent(city);
                    } else {
                        body += '&addressId=' + encodeURIComponent(selectedAddressId);
                    }

                    fetch('<%= request.getContextPath()%>/payment', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: body
                    })
                            .then(res => res.json())
                            .then(data => {
                                if (data.code === "00") {
                                    window.location.href = data.data;
                                } else {
                                    alert("Failed to generate VNPAY link.");
                                }
                            })
                            .catch(() => alert("Error while contacting payment gateway."));
                };
            </script>
        </div>
    </body>
</html>
