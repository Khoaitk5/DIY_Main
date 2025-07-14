<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Add New Product | 3tlCraft</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
        <style>
            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }

            html, body {
                height: 100%;
                font-family: 'Poppins', sans-serif;
                background: linear-gradient(to right, #f3f4f6, #e9ebee);
            }

            body {
                display: flex;
                flex-direction: column;
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
                color: #83B735;
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

            .main-content {
                flex: 1;
                padding: 50px 20px;
            }

            h2 {
                text-align: center;
                color: #333;
                margin-bottom: 30px;
                font-size: 28px;
            }

            form {
                background: #fff;
                padding: 30px;
                border-radius: 15px;
                width: 100%;
                max-width: 500px;
                margin: auto;
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
            }

            label {
                display: block;
                margin-top: 15px;
                font-weight: 600;
                color: #444;
            }

            input[type="text"], input[type="number"], textarea {
                width: 100%;
                padding: 12px;
                margin-top: 5px;
                border: 1px solid #ccc;
                border-radius: 8px;
                font-size: 14px;
            }

            textarea {
                resize: vertical;
            }

            #drop-area {
                border: 2px dashed #ccc;
                border-radius: 10px;
                padding: 20px;
                text-align: center;
                margin-top: 10px;
                background-color: #fafafa;
                transition: border-color 0.3s;
            }

            #drop-area.highlight {
                border-color: #83B735;
            }

            #fileElem {
                display: none;
            }

            #fileLabel {
                display: inline-block;
                margin-top: 12px;
                padding: 10px 25px;
                background-color: #83B735;
                color: white;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
            }

            #preview img {
                max-width: 100%;
                margin-top: 15px;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(0,0,0,0.15);
            }

            button[type="submit"] {
                background-color: #83B735;
                color: white;
                padding: 12px 25px;
                border: none;
                border-radius: 8px;
                font-weight: 600;
                font-size: 16px;
                cursor: pointer;
                width: 100%;
                margin-top: 25px;
                transition: background-color 0.3s;
            }

            button[type="submit"]:hover {
                background-color: #76a62e;
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
                color: #83B735;
                font-weight: bold;
            }
        </style>
    </head>
    <body>

        <header>
            <a href="home" class="logo">3tlCraft <span>X.</span></a>
        </header>

        <main class="main-content">
            <h2>Add a New Product</h2>

            <form action="products" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="create">

                <label for="name">Product Name</label>
                <input type="text" id="name" name="name" required>

                <label for="description">Description</label>
                <textarea id="description" name="description" rows="3" required></textarea>

                <label for="price">Price (USD)</label>
                <input type="number" id="price" name="price" step="0.01" required>

                <label for="stockQuantity">Stock Quantity</label>
                <input type="number" id="stockQuantity" name="stockQuantity" required>

                <label>Product Image</label>
                <div id="drop-area">
                    <p>Drag & drop your image here or click to browse</p>
                    <input type="file" name="image" id="fileElem" accept="image/*" onchange="handleFiles(this.files)">
                    <label for="fileElem" id="fileLabel">Choose Image</label>
                    <div id="preview"></div>
                </div>

                <button type="submit">Add Product</button>
            </form>
        </main>

        <footer>
            <p>&copy; <span>3tlCraft X</span>. All rights reserved.</p>
        </footer>

        <script>
            let dropArea = document.getElementById('drop-area');

            ['dragenter', 'dragover'].forEach(eventName => {
                dropArea.addEventListener(eventName, e => {
                    e.preventDefault();
                    dropArea.classList.add('highlight');
                }, false);
            });

            ['dragleave', 'drop'].forEach(eventName => {
                dropArea.addEventListener(eventName, e => {
                    e.preventDefault();
                    dropArea.classList.remove('highlight');
                }, false);
            });

            dropArea.addEventListener('drop', e => {
                let dt = e.dataTransfer;
                let files = dt.files;
                document.getElementById("fileElem").files = files;
                handleFiles(files);
            });

            function handleFiles(files) {
                const preview = document.getElementById('preview');
                preview.innerHTML = "";
                for (let i = 0; i < files.length; i++) {
                    const img = document.createElement("img");
                    img.src = URL.createObjectURL(files[i]);
                    preview.appendChild(img);
                }
            }
        </script>

    </body>
</html>
