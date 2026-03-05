CREATE DATABASE IF NOT EXISTS stock_db;
USE stock_db;

CREATE TABLE products (
  id VARCHAR(20) PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  category VARCHAR(80),
  price DOUBLE NOT NULL,
  quantity INT NOT NULL
);

CREATE TABLE movements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id VARCHAR(20),
  type ENUM('IN','OUT'),
  qty INT,
  date DATETIME,
  note VARCHAR(255),
  FOREIGN KEY(product_id) REFERENCES products(id)
);