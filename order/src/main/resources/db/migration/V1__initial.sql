CREATE TABLE public.customers
(
  id INT PRIMARY KEY,
  first_name VARCHAR(200) NOT NULL,
  last_name VARCHAR(200) NOT NULL,
  email VARCHAR(200) NOT NULL,
  phone VARCHAR(15),
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.customers OWNER to postgres;
ALTER TABLE public.customers REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE products(
  id INT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  list_price NUMERIC(8, 2) NOT NULL,
  quantity INT NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.products OWNER to postgres;
ALTER TABLE public.products REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE orders(
  id SERIAL PRIMARY KEY,
  customer_id INT NOT NULL,
  status VARCHAR(50) DEFAULT 'pending',
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.orders OWNER to postgres;
ALTER TABLE public.orders REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE order_items(
  id SERIAL PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL,
  unit_price NUMERIC(8, 2) NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.order_items OWNER to postgres;
ALTER TABLE public.order_items REPLICA IDENTITY FULL;
