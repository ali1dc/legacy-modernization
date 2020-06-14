ALTER TABLE public.orders ADD COLUMN customer_id INT;
ALTER TABLE public.orders
    ADD CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(id);
