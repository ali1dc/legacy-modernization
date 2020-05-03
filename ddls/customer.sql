CREATE DATABASE customer
  WITH 
  OWNER = postgres
  ENCODING = 'UTF8'
  CONNECTION LIMIT = -1;

---------------- TABLES ----------------
CREATE TABLE public.addresses
(
  id SERIAL PRIMARY KEY,
  address_1 VARCHAR(300) NOT NULL,
  address_2 VARCHAR(200),
  city VARCHAR(100) NOT NULL,
  zip VARCHAR(10) NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50) NOT NULL,
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.addresses
  OWNER to postgres;

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
  updated_by VARCHAR(50) NOT NULL,
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.customers
    OWNER to postgres;

----------------------------------------
CREATE TABLE public.customers_addresses
(
  id SERIAL PRIMARY KEY,
  customers_id INT NOT NULL,
  address_id INT NOT NULL,
  address_type VARCHAR(15) DEFAULT 'billing',
  is_default BOOLEAN NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(50) NOT NULL,
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (customers_id) REFERENCES customers (id),
  FOREIGN KEY (address_id) REFERENCES addresses (id)
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.customers_addresses
  OWNER to postgres;
