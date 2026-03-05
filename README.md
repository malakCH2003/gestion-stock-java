# 📦 GestionStock – Stock Management Desktop Application

A desktop inventory management application built with **Java Swing and MySQL**.  
The system allows managing products, tracking stock movements (IN / OUT),
and monitoring inventory through a **dashboard with real-time statistics**.

---

# 📖 About The Project

GestionStock is a desktop application designed to help manage inventory
efficiently.

The application provides tools to manage products, track stock entries
and exits, and visualize inventory data through a dashboard.

The project follows a **layered architecture (UI / DAO / Model / DB)** to
ensure maintainability and scalability.

---

# ✨ Features

### 📊 Dashboard

- View total stock quantity
- Display number of products
- Display number of stock movements
- Show recent inventory movements

### 📦 Product Management

- Add new products
- Edit product information
- Delete products
- Search products

### 🔄 Stock Movements

- Register stock **IN**
- Register stock **OUT**
- Automatically update product quantities
- Track movement history

### 📑 Reports

- View inventory status
- Monitor stock levels
- Track movement records

### 🔐 Authentication

- Login system
- User authentication before accessing the application

---
# 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Java | Core programming language |
| Swing | Desktop GUI framework |
| MySQL | Database management |
| JDBC | Database connectivity |
| MVC | Architecture	Project organization |

 -----------------------------------------------------------------------------------------

# 📁 Project Structure
```
GestionStock
├── src
│ └── main
│ ├── java
│ │ └── com/mycompany/gestionstock
│ │ ├── app # Application entry and frames
│ │ ├── dao # Data access layer
│ │ ├── db # Database connection
│ │ ├── model # Data models
│ │ ├── ui # Application interface panels
│ │ └── util # UI utilities and theme
│ │
│ └── resources
│ └── images # Application images
│
├── docs
│ ├── schema.sql # Database schema
│ └── screenshots # Application screenshots
│
├── .gitignore
├── .env.example
└── README.md
```

---

# 🧠 Architecture

UI Layer → Handles application interface (Panels, Frames)  
DAO Layer → Database operations  
Model Layer → Data structures  
DB Layer → Database connection management  
Utility Layer → UI helpers and theme configuration  

This architecture improves:

- Code readability
- Maintainability
- Separation of concerns

---

# 🚀 Getting Started

### Prerequisites

Make sure you have installed:

- Java JDK 17+
- MySQL Server
- Git

 ### 1️⃣ Clone the repository
 ```bash
 git clone https://github.com/YOUR_USERNAME/GestionStock.git
 cd GestionStock
```
 ### 🗄️ Database Setup
 Create the MySQL database and tables using the provided script:
  ```bash
 SOURCE docs/schema.sql;
 ```
 This will create:
   products
   movements
   
 ### 🔧 Configuration
 To avoid exposing credentials, database settings are loaded from environment variables.
 Create a .env file based on .env.example.
 Example configuration:
 ```env
 DB_URL=jdbc:mysql://localhost:3306/stock_db
 DB_USER=root
 DB_PASS=yourpassword
 ```
      
 ### ▶️ Running the Application
 Run the main application class:
 ```
 Main.java
 ```
---

# 📸 Screenshots
| Login | Dashboard | Products | Admin Movements | rapport |
|---|---|---|---|---|
| ![Login](./screenshots/docs/screenshots/Login.png) | ![Dashboard](./screenshots/docs/Dashboard.png) | ![Products](./screenshots/docs/Products.png) | ![Movements](./screenshots/docs/Movements.png) |![rapport](./screenshots/docs/rapport.png) |

---
# 💡 Skills Demonstrated

- Java desktop application development
- Swing UI design
- MySQL database
- JDBC database connectivity
- Layered architecture
- Inventory management logic

  ---

  👥 Authors
  
  - [@malakCH2003](https://github.com/malakCH2003)

------------------------------------------------------------------------
<p align="center">Built with ☕ and Java</p> 




