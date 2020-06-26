CREATE TABLE public.addresses
(
  id INT PRIMARY KEY,
  address_1 VARCHAR(300) NOT NULL,
  address_2 VARCHAR(200),
  city VARCHAR(100) NOT NULL,
  state VARCHAR(2) NOT NULL,
  zip VARCHAR(10) NOT NULL,
  legacy_id INT,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.addresses OWNER to postgres;
ALTER TABLE public.addresses REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE public.customers
(
  id INT PRIMARY KEY,
  first_name VARCHAR(200) NOT NULL,
  last_name VARCHAR(200) NOT NULL,
  email VARCHAR(200) NOT NULL,
  phone VARCHAR(15),
  address_id INT,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  FOREIGN KEY (address_id) REFERENCES public.addresses(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.customers OWNER to postgres;
ALTER TABLE public.customers REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE orders(
  id INT PRIMARY KEY,
  status VARCHAR(50) NOT NULL,
  customer_id INT,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  legacy_customer_id INT,
  FOREIGN KEY (customer_id) REFERENCES public.customers(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.orders OWNER to postgres;
ALTER TABLE public.orders REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE shippings(
  id SERIAL PRIMARY KEY,
  order_id INT NOT NULL,
  delivered BOOLEAN DEFAULT FALSE,
  shipping_date TIMESTAMP WITHOUT TIME ZONE,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  legacy_order_id INT,
  FOREIGN KEY (order_id) REFERENCES public.orders(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.shippings OWNER to postgres;
ALTER TABLE public.shippings REPLICA IDENTITY FULL;
