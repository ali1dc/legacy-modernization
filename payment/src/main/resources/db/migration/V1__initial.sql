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
  FOREIGN KEY (customer_id) REFERENCES public.customers(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.orders OWNER to postgres;
ALTER TABLE public.orders REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE payments(
  id SERIAL PRIMARY KEY,
  customer_id INT NOT NULL,
  order_id INT NOT NULL,
  amount NUMERIC(8, 2) NOT NULL,
  successful BOOLEAN DEFAULT FALSE,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  FOREIGN KEY (customer_id) REFERENCES public.customers(id),
  FOREIGN KEY (order_id) REFERENCES public.orders(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.payments OWNER to postgres;
ALTER TABLE public.payments REPLICA IDENTITY FULL;
