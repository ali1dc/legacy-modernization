create table categories(
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(255) NOT NULL,
  created_by VARCHAR(50)
);
INSERT INTO categories (category_name, created_by)
VALUES ('Beverages', 'legacy'),
       ('Bakery', 'legacy'),
       ('Goods', 'legacy'),
       ('Electronics', 'legacy'),
       ('Laptop', 'legacy'),
       ('Mobil', 'legacy'),
       ('Flowers', 'legacy'),
       ('Books', 'legacy'),
       ('Cleaning Supplies', 'legacy'),
       ('Medical Supplies', 'legacy');

create table products(
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  list_price decimal(15,2) NOT NULL,
  quantity int NOT NULL,
  category_id int,
  created_by VARCHAR(50),
  FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

create table customers(
  customer_id INT AUTO_INCREMENT PRIMARY KEY,
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

create table orders(
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id int NOT NULL,
  status VARCHAR(50) DEFAULT 'pending',
  order_date datetime DEFAULT now(),
  created_by VARCHAR(50),
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

create table order_items(
  item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id int NOT NULL,
  product_id int NOT NULL,
  quantity int NOT NULL,
  unit_price decimal(15,2) NOT NULL,
  created_by VARCHAR(50),
  FOREIGN KEY (order_id) REFERENCES orders(order_id),
  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

create table payments(
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id int NOT NULL,
  charged_amount decimal(15,2) NOT NULL,
  successful boolean,
  created_by VARCHAR(50),
  FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

create table shipping(
  shipping_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id int NOT NULL,
  shipping_date datetime DEFAULT now(),
  delivered boolean,
  created_by VARCHAR(50),
  FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
