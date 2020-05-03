CREATE DATABASE product
  WITH 
  OWNER = postgres
  ENCODING = 'UTF8'
  CONNECTION LIMIT = -1;

---------------- TABLES ----------------
CREATE TABLE categories(
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(400),
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50) NOT NULL,
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.categories
  OWNER to postgres;

----------------------------------------
CREATE TABLE products(
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  list_price MONEY NOT NULL,
  quantity INT NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50) NOT NULL,
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.products
  OWNER to postgres;

----------------------------------------
CREATE TABLE products_categories(
  product_id INT NOT NULL,
  category_id INT NOT NULL,
  FOREIGN KEY (product_id) REFERENCES products (id),
  FOREIGN KEY (category_id) REFERENCES categories (id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.products_categories
  OWNER to postgres;
