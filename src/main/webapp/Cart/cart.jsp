<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
    List<model.CartItem> cartItems = (List<model.CartItem>) request.getAttribute("cartItems");
    StringBuilder json = new StringBuilder("[");
    if (cartItems != null) {
        for (int i = 0; i < cartItems.size(); i++) {
            model.CartItem item = cartItems.get(i);
            json.append("{");
            json.append("\"id\":").append(item.getProduct().getId()).append(",");
            json.append("\"image\":\"").append(item.getProduct().getImageUrl()).append("\",");
            json.append("\"name\":\"").append(item.getProduct().getName()).append("\",");
            json.append("\"description\":\"").append(item.getProduct().getDescription()).append("\",");
            json.append("\"price\":").append(item.getProduct().getPrice()).append(",");
            json.append("\"quantity\":").append(item.getQuantity());
            json.append("}");
            if (i < cartItems.size() - 1) json.append(",");
        }
    }
    json.append("]");
    String cartItemsJson = json.toString();
    double total = (Double) request.getAttribute("total");
    
    List<model.UserCoupon> userCoupons = (List<model.UserCoupon>) request.getAttribute("userCoupons");

    com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
        .registerTypeAdapter(java.time.LocalDateTime.class,
            new com.google.gson.JsonSerializer<java.time.LocalDateTime>() {
                public com.google.gson.JsonElement serialize(java.time.LocalDateTime src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                    return new com.google.gson.JsonPrimitive(src.toString()); // ISO 8601
                }
            }).create();

    String userCouponsJson = gson.toJson(userCoupons);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Cart | DIY Store</title>
    <script src="https://unpkg.com/react@17/umd/react.development.js" crossorigin></script>
    <script src="https://unpkg.com/react-dom@17/umd/react-dom.development.js" crossorigin></script>
    <script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
    <link rel="stylesheet" href="css/cart.css" />
</head>
<body>
<div id="root"
     data-cart-items='<%= cartItemsJson %>'
     data-total='<%= total %>'
     data-user-coupons='<%= userCouponsJson %>'></div>

<script type="text/babel">
    function formatCurrency(value) {
        return Number(value).toLocaleString("en-US", {
            style: "currency",
            currency: "USD"
        });
    }

    function Header({ itemCount }) {
        return (
            <header className="container">
                <h1>Shopping Cart</h1>
                <ul className="breadcrumb">
                    <li><a href="home">Home</a></li>
                    <li>Shopping Cart</li>
                </ul>
                <span className="count">{itemCount} items in the bag</span>
            </header>
        );
    }

    function ProductList({ products, onChangeProductQuantity }) {
        return (
            <section className="container">
                <ul className="products">
                    {products.map((product, index) => (
                        <li className="row" key={index}>
                            <div className="col left">
                                <div className="thumbnail">
                                    <img src={product.image} alt={product.name} />
                                </div>
                                <div className="detail">
                                    <div className="name"><a href="#">{product.name}</a></div>
                                    <div className="description">{product.description}</div>
                                    <div className="price">{formatCurrency(product.price)}</div>
                                </div>
                            </div>
                            <div className="col right">
                                <div className="quantity">
                                    <input
                                        type="number"
                                        className="quantity"
                                        value={product.quantity}
                                        min={1}
                                        onChange={(event) => onChangeProductQuantity(index, event)}
                                    />
                                </div>


                                <div className="remove">
                                    <form
                                        method="post"
                                        action="carts?action=remove"
                                        onSubmit={() => window.confirm("Bạn có chắc muốn xoá sản phẩm này?")}
                                    >
                                        <input type="hidden" name="productId" value={product.id} />
                                        <button type="submit" style={{ all: "unset", cursor: "pointer" }}>
                                            <svg className="close" viewBox="0 0 60 60">
                                                <polygon points="38.936,23.561 36.814,21.439 30.562,27.691 24.311,21.439 22.189,23.561 28.441,29.812 22.189,36.064 24.311,38.186 30.562,31.934 36.814,38.186 38.936,36.064 32.684,29.812" />
                                            </svg>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            </section>
        );
    }

    function Summary({
        subTotal, discount, tax,
        checkPromoCode, onCheckout,
        userCoupons, selectedCode, setSelectedCode,
        showSelect, setShowSelect
    }) {
        const total = subTotal - discount + tax;
        return (
            <section className="container">
                <div className="promotion">
                    <label>Promo Code</label>
                    {!showSelect ? (
                      <button className="promo-apply-btn" onClick={() => setShowSelect(true)}>+ Add Coupon</button>
                    ) : (
                      <div className="promo-code-container">
                        <select className="promo-select" onChange={(e) => setSelectedCode(e.target.value)} value={selectedCode}>
                          <option value="">-- Select a coupon --</option>
                          {userCoupons.map(c => (
                            <option key={c.couponCode} value={c.couponCode}>
                              {c.couponCode}
                            </option>
                          ))}
                        </select>
                        <button className="promo-apply-btn" onClick={checkPromoCode}>Apply</button>
                      </div>
                    )}
                  </div>


                <div className="summary">
                    <ul>
                        <li>Subtotal <span>{formatCurrency(subTotal)}</span></li>
                        {discount > 0 && <li>Discount <span>{formatCurrency(discount)}</span></li>}
                        <li>Tax <span>{formatCurrency(tax)}</span></li>
                        <li className="total">Total <span>{formatCurrency(total)}</span></li>
                    </ul>
                </div>
                <div className="checkout">
                    <button type="button" onClick={onCheckout}>Check Out</button>
                </div>
            </section>
        );
    }


    const TAX = 0;

    function Page() {
    const root = document.getElementById("root");
    const products = JSON.parse(root.getAttribute("data-cart-items"));
    const userCoupons = JSON.parse(root.getAttribute("data-user-coupons"));

    const [cartProducts, setCartProducts] = React.useState(products);
    const [discountPercent, setDiscountPercent] = React.useState(0);
    const [selectedCode, setSelectedCode] = React.useState("");
    const [showSelect, setShowSelect] = React.useState(false);

    const itemCount = cartProducts.reduce((sum, item) => sum + item.quantity, 0);
    const subTotal = cartProducts.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const discount = (subTotal * discountPercent) / 100;

    const onChangeProductQuantity = (index, event) => {
        let value = event.target.value;
        if (value === "" || parseInt(value) <= 0 || isNaN(parseInt(value))) {
            value = 1;
        } else {
            value = parseInt(value);
        }
        const updatedProducts = [...cartProducts];
        updatedProducts[index].quantity = value;
        setCartProducts(updatedProducts);
    };

    const checkPromoCode = () => {
        
        console.log("Selected coupon code:", selectedCode); // ← THÊM DÒNG NÀY
        if (!selectedCode) {
            alert("Please select a coupon to apply");
            return;
        }
        fetch("<%=request.getContextPath()%>/coupon?action=check&code=" + encodeURIComponent(selectedCode), {
            method: "GET",
            headers: { "Accept": "application/json" }
        })

        .then(res => res.json())
        .then(data => {
            
            console.log("Server response:", data);  // ← THÊM DÒNG NÀY
            if (data.valid) {
                setDiscountPercent(data.discountPercent || 0);
            } else {
                alert("Coupon is invalid or cannot be used");
            }
        })
        .catch(err => {
            console.error("Error checking coupon:", err);
            alert("Failed to verify coupon.");
        });
    };

    const handleCheckout = () => {
        const formData = new URLSearchParams();
        formData.append("action", "create");

        cartProducts.forEach(product => {
            const quantity = parseInt(product.quantity);
            if (!isNaN(quantity) && quantity > 0) {
                formData.append("productIds", product.id);       // mảng productIds[]
                formData.append("quantities", quantity);         // mảng quantities[]
            }
        });

        if (selectedCode) {
            formData.append("promoCode", selectedCode);
            formData.append("discountPercent", discountPercent.toString());
        }

        fetch("<%=request.getContextPath()%>/orders", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData.toString()
        })
        .then(res => res.json())
        .then(data => {
            if (data.redirect) {
                window.location.href = "<%=request.getContextPath()%>/" + data.redirect;
            } else {
                alert("Checkout failed!");
            }
        })
        .catch(error => {
            console.error("Checkout error:", error);
            alert("Checkout failed!");
        });
    };


    return (
        <div>
            <Header itemCount={itemCount} />
            <ProductList products={cartProducts} onChangeProductQuantity={onChangeProductQuantity} />
            <Summary
                subTotal={subTotal}
                discount={discount}
                tax={TAX}
                checkPromoCode={checkPromoCode}
                onCheckout={handleCheckout}
                userCoupons={userCoupons}
                selectedCode={selectedCode}
                setSelectedCode={setSelectedCode}
                showSelect={showSelect}
                setShowSelect={setShowSelect}
            />
        </div>
    );
}


    ReactDOM.render(<Page />, document.getElementById("root"));
</script>
</body>
</html>
