<%@page import="model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    User user = (User) session.getAttribute("user");
    String name = user.getName();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Products - DIY Store</title>
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&family=Turret+Road:wght@300;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="css/products.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css" />

    </head>
    <body>
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





        <div class="main-container">
            <aside class="sidebar">
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Tìm kiếm sản phẩm..." value="${param.search}">
                </div>

                <div class="filter-section">
                    <div class="filter-title">Sort by:</div>
                    <select class="sort-select" id="sortSelect">
                        <option value="name-asc" ${param.sort == 'name-asc' ? 'selected' : ''}>A-Z</option>
                        <option value="name-desc" ${param.sort == 'name-desc' ? 'selected' : ''}>Z-A</option>
                        <option value="price-asc" ${param.sort == 'price-asc' ? 'selected' : ''}>Price: Low to High</option>
                        <option value="price-desc" ${param.sort == 'price-desc' ? 'selected' : ''}>Price: High to Low</option>
                        <option value="newest" ${param.sort == 'newest' ? 'selected' : ''}>Newest</option>
                    </select>
                </div>

                <!-- Danh mục (Category) -->
                <div class="filter-section">
                    <div class="filter-title">Categories</div>
                    <c:forEach var="cate" items="${allCategories}">
                        <div class="subcategory-item">
                            <input type="checkbox" id="cate-${cate.name}" name="category" value="${cate.name}" 
                                   ${fn:contains(paramValues.category, cate.name) ? 'checked' : ''}>
                            <label for="cate-${cate.name}">${cate.name.toUpperCase()}</label>
                        </div>
                    </c:forEach>
                </div>

            </aside>

            <main class="products-section">
                <div class="products-header">
                    <div class="products-count">Displaying ${products.size()} products</div>
                    <div class="view-toggle">
                        <button class="view-btn active" onclick="toggleView('grid')">
                            <i class='bx bx-grid-alt'></i>
                        </button>
                        <button class="view-btn" onclick="toggleView('list')">
                            <i class='bx bx-list-ul'></i>
                        </button>
                    </div>
                </div>

                <div class="products-grid" id="productsGrid">
                    <c:choose>
                        <c:when test="${empty products}">
                            <div style="grid-column: 1 / -1; text-align: center; padding: 50px;">
                                <i class='bx bx-search' style="font-size: 3rem; color: #ccc; margin-bottom: 20px;"></i>
                                <h3>No products found</h3>
                                <p>Try changing the filters or search keywords</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="product" items="${products}">
                                <div class="product-card">
                                    <div class="product-image">
                                        <a href="${pageContext.request.contextPath}/products?action=details&id=${product.id}">
                                            <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}" />
                                        </a>
                                    </div>
                                    <div class="product-info">
                                        <h3>${product.name}</h3>
                                        <div class="product-price">$${product.price}</div>
                                        <c:if test="${not empty product.tags}">
                                            <div class="product-tags">
                                                <c:forEach var="tag" items="${product.tags}">
                                                    <span class="tag">${tag.name}</span>
                                                </c:forEach>
                                            </div>
                                        </c:if>

                                    </div>
                                    <c:choose>
                                        <c:when test="${product.stockQuantity > 0}">
                                            <form action="${pageContext.request.contextPath}/carts" method="post">
                                                <input type="hidden" name="action" value="insert" />
                                                <input type="hidden" name="productId" value="${product.id}" />
                                                <input type="hidden" name="quantity" value="1" />
                                                <button type="submit" class="add-to-cart">
                                                    <i class='bx bx-cart'></i>
                                                    <span class="add-text" style="display: none;">Add to Cart</span>
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="add-to-cart" onclick="alert('This product is out of stock.')" style="cursor: not-allowed;" disabled>
                                                <i class='bx bx-cart'></i>
                                                <span class="add-text" style="display: none;">Out of Stock</span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="product-pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="products">
                            <c:param name="page" value="${currentPage - 1}" />
                            <!-- giữ filter -->
                            <c:if test="${param.search != null}">
                                <c:param name="search" value="${param.search}" />
                            </c:if>
                            <c:if test="${param.sort != null}">
                                <c:param name="sort" value="${param.sort}" />
                            </c:if>
                            <c:forEach var="cate" items="${paramValues.category}">
                                <c:param name="category" value="${cate}" />
                            </c:forEach>
                            <c:forEach var="tag" items="${paramValues.tag}">
                                <c:param name="tag" value="${tag}" />
                            </c:forEach>
                        </c:url>
                        <a href="${prevUrl}" class="page-link">Previous</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:url var="pageUrl" value="products">
                            <c:param name="page" value="${i}" />
                            <c:if test="${param.search != null}">
                                <c:param name="search" value="${param.search}" />
                            </c:if>
                            <c:if test="${param.sort != null}">
                                <c:param name="sort" value="${param.sort}" />
                            </c:if>
                            <c:forEach var="cate" items="${paramValues.category}">
                                <c:param name="category" value="${cate}" />
                            </c:forEach>
                            <c:forEach var="tag" items="${paramValues.tag}">
                                <c:param name="tag" value="${tag}" />
                            </c:forEach>
                        </c:url>
                        <a href="${pageUrl}" class="page-link ${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <c:url var="nextUrl" value="products">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:if test="${param.search != null}">
                                    <c:param name="search" value="${param.search}" />
                                </c:if>
                                <c:if test="${param.sort != null}">
                                    <c:param name="sort" value="${param.sort}" />
                                </c:if>
                                <c:forEach var="cate" items="${paramValues.category}">
                                    <c:param name="category" value="${cate}" />
                                </c:forEach>
                                <c:forEach var="tag" items="${paramValues.tag}">
                                    <c:param name="tag" value="${tag}" />
                                </c:forEach>
                            </c:url>
                            <a href="${nextUrl}" class="page-link">Next</a>
                        </c:when>
                        <c:otherwise>
                            <span class="page-link disabled">Next</span>
                        </c:otherwise>
                    </c:choose>
                </div>



            </main>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const searchInput = document.getElementById('searchInput');
                const sortSelect = document.getElementById('sortSelect');
                const tagCheckboxes = document.querySelectorAll('input[name="tag"]');
                const cateCheckboxes = document.querySelectorAll('input[name="category"]');
                const viewBtns = document.querySelectorAll('.view-btn');

                // Debounce filter (đúng context)
                const debounceApplyFilters = debounce(applyFilters, 500);

                if (searchInput)
                    searchInput.addEventListener('input', debounceApplyFilters);
                if (sortSelect)
                    sortSelect.addEventListener('change', applyFilters);
                tagCheckboxes.forEach(cb => cb.addEventListener('change', applyFilters));
                cateCheckboxes.forEach(cb => cb.addEventListener('change', applyFilters));

                // View toggle buttons
                viewBtns.forEach(btn => {
                    btn.addEventListener('click', function (event) {
                        viewBtns.forEach(b => b.classList.remove('active'));
                        this.classList.add('active');
                        const grid = document.getElementById('productsGrid');
                        grid.classList.toggle('list-view', this.dataset.view === 'list');
                    });
                });
            });

            function applyFilters() {
                const search = document.getElementById('searchInput')?.value || '';
                const sort = document.getElementById('sortSelect')?.value || '';
                const categories = Array.from(document.querySelectorAll('input[name="category"]:checked')).map(cb => cb.value);
                const tags = Array.from(document.querySelectorAll('input[name="tag"]:checked')).map(cb => cb.value);

                const params = new URLSearchParams();
                if (search)
                    params.append('search', search);
                if (sort)
                    params.append('sort', sort);
                categories.forEach(cate => params.append('category', cate));
                tags.forEach(tag => params.append('tag', tag));

                window.location.href = '${pageContext.request.contextPath}/products?' + params.toString();
            }

            function debounce(func, wait) {
                let timeout;
                return function (...args) {
                    clearTimeout(timeout);
                    timeout = setTimeout(() => func.apply(this, args), wait);
                };
            }

        </script>
    </body>
</html>
