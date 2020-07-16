CREATE TABLE public.addresses
(
  id SERIAL PRIMARY KEY,
  address_1 VARCHAR(300) NOT NULL,
  address_2 VARCHAR(200),
  city VARCHAR(100) NOT NULL,
  state VARCHAR(2) NOT NULL,
  zip VARCHAR(10) NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50),
  updated_date TIMESTAMP WITHOUT TIME ZONE,
  legacy_id INT
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.addresses OWNER to postgres;
ALTER TABLE public.addresses REPLICA IDENTITY FULL;

----------------------------------------
CREATE TABLE public.customers
(
  id SERIAL PRIMARY KEY,
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
CREATE TABLE public.customer_addresses
(
  id SERIAL PRIMARY KEY,
  customer_id INT NOT NULL,
  address_id INT NOT NULL,
  address_type VARCHAR(15) NOT NULL,
  is_default BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (customer_id) REFERENCES customers (id),
  FOREIGN KEY (address_id) REFERENCES addresses (id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.customer_addresses OWNER to postgres;
ALTER TABLE public.customer_addresses REPLICA IDENTITY FULL;
