CREATE TABLE categories(
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(400),
  legacy_id INT,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.categories OWNER to postgres;
ALTER TABLE public.categories REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE products(
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(255),
  list_price NUMERIC(8, 2) NOT NULL,
  quantity INT NOT NULL,
  legacy_id INT,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.products OWNER to postgres;
ALTER TABLE public.products REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE products_categories(
  id SERIAL PRIMARY KEY,
  product_id INT NOT NULL,
  category_id INT NOT NULL,
  FOREIGN KEY (product_id) REFERENCES products (id),
  FOREIGN KEY (category_id) REFERENCES categories (id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.products_categories OWNER to postgres;
ALTER TABLE public.products_categories REPLICA IDENTITY FULL;
