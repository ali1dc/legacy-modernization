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
CREATE TABLE payments(
  id SERIAL PRIMARY KEY,
  customer_id INT NOT NULL,
  order_id INT NOT NULL,
  amount NUMERIC(8, 2) NOT NULL,
  successfull BOOLEAN DEFAULT FALSE,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT,
  FOREIGN KEY (customer_id) REFERENCES customers(id),
  FOREIGN KEY (order_id) REFERENCES customers(id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.payments OWNER to postgres;
ALTER TABLE public.payments REPLICA IDENTITY FULL;
