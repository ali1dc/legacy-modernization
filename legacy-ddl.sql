create table categories(
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(255) NOT NULL
);
INSERT INTO categories (category_name)
VALUES ('Beverages'),
       ('Bakery'),
       ('Goods'),
       ('Electronics'),
       ('Laptop'),
       ('Mobil'),
       ('Flowers'),
       ('Books'),
       ('Cleaning Supplies '),
       ('Medical Supplies')

create table products(
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  list_price decimal(15,2) NOT NULL,
  quantity int NOT NULL,
  category_id int,
  FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
-- INSERT INTO products (name, description)
-- VALUES ('ipad', 'ipad model 2');

-- create table inventories(
--   product_id INT NOT NULL,
--   quantity int NOT NULL,
--   FOREIGN KEY (product_id) REFERENCES products(product_id)
-- );

create table customers(
  customer_id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  billing_address VARCHAR(300) NOT NULL,
  shipping_address VARCHAR(300),
  email VARCHAR(200) NOT NULL,
  phone VARCHAR(15) NOT NULL
);

create table orders(
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id int NOT NULL,
  status VARCHAR(50) DEFAULT 'pending',
  order_date datetime DEFAULT now(),
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

create table order_items(
  item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id int NOT NULL,
  product_id int NOT NULL,
  quantity int NOT NULL,
  unit_price decimal(15,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(order_id),
  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

create table payments(
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id int NOT NULL,
  order_id int NOT NULL,
  charged_amount decimal(15,2) NOT NULL,
  successfull boolean,
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
  FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

create table shipping(
  shipping_id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id int NOT NULL,
  order_id int NOT NULL,
  shipping_date datetime DEFAULT now(),
  delivered boolean,
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
  FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
