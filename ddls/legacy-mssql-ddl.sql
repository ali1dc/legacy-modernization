CREATE DATABASE legacy_order;
GO
USE legacy_order;
EXEC sys.sp_cdc_enable_db;

CREATE LOGIN $(MSSQL_USER) WITH PASSWORD = '$(MSSQL_PASSWORD)';
GO
CREATE USER $(MSSQL_USER) FOR LOGIN $(MSSQL_USER);
GO
ALTER SERVER ROLE sysadmin ADD MEMBER [$(MSSQL_USER)];
GO

-- Create and populate our categories table and enable CDC
create table categories(
  id INTEGER IDENTITY(1,1) NOT NULL PRIMARY KEY,
  category_name VARCHAR(255) NOT NULL,
  created_by VARCHAR(50)
);
INSERT INTO categories (category_name, created_by)
  VALUES ('Beverages', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Bakery', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Goods', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Electronics', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Laptop', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Mobil', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Flowers', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Books', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Cleaning Supplies', 'legacy');
INSERT INTO categories (category_name, created_by)
  VALUES ('Medical Supplies', 'legacy');
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'categories', @role_name = NULL, @supports_net_changes = 0;

-- Create our products table and enable CDC
create table products(
  id INTEGER IDENTITY(1,1) NOT NULL PRIMARY KEY,
  product_name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  list_price decimal(15,2) NOT NULL,
  quantity INTEGER NOT NULL,
  category_id INTEGER,
  created_by VARCHAR(50),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'products', @role_name = NULL, @supports_net_changes = 0;

-- Create our customers table and enable CDC
create table customers(
  id INT IDENTITY(1,1) PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  billing_address1 VARCHAR(300) NOT NULL,
  billing_address2 VARCHAR(200),
  billing_city VARCHAR(100) NOT NULL,
  billing_state VARCHAR(2) NOT NULL,
  billing_zip VARCHAR(10) NOT NULL,
  shipping_address1 VARCHAR(300) NOT NULL,
  shipping_address2 VARCHAR(200),
  shipping_city VARCHAR(100) NOT NULL,
  shipping_state VARCHAR(2) NOT NULL,
  shipping_zip VARCHAR(10) NOT NULL,
  email VARCHAR(200) NOT NULL,
  phone VARCHAR(15) NOT NULL,
  created_by VARCHAR(50)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'customers', @role_name = NULL, @supports_net_changes = 0;

-- Create our orders table and enable CDC
create table orders(
  id INT IDENTITY(1,1) PRIMARY KEY,
  customer_id INTEGER NOT NULL,
  status VARCHAR(50) DEFAULT 'pending',
  order_date datetime DEFAULT GETDATE(),
  created_by VARCHAR(50),
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'orders', @role_name = NULL, @supports_net_changes = 0;

-- Create our order_items table and enable CDC
create table order_items(
  id INT IDENTITY(1,1) PRIMARY KEY,
  order_id int NOT NULL,
  product_id int NOT NULL,
  quantity int NOT NULL,
  unit_price decimal(15,2) NOT NULL,
  created_by VARCHAR(50),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'order_items', @role_name = NULL, @supports_net_changes = 0;

-- Create our payments table and enable CDC
create table payments(
  id INT IDENTITY(1,1) PRIMARY KEY,
  customer_id int NOT NULL,
  order_id int NOT NULL,
  charged_amount decimal(15,2) NOT NULL,
  successful BIT,
  created_by VARCHAR(50),
  FOREIGN KEY (customer_id) REFERENCES customers(id),
  FOREIGN KEY (order_id) REFERENCES orders(id)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'payments', @role_name = NULL, @supports_net_changes = 0;

-- Create our shipping table and enable CDC
create table shipping(
  id INT IDENTITY(1,1) PRIMARY KEY,
  order_id int NOT NULL,
  customer_id int NOT NULL,
  shipping_date datetime DEFAULT GETDATE(),
  delivered BIT,
  created_by VARCHAR(50),
  FOREIGN KEY (customer_id) REFERENCES customers(id),
  FOREIGN KEY (order_id) REFERENCES orders(id)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'shipping', @role_name = NULL, @supports_net_changes = 0;
GO
