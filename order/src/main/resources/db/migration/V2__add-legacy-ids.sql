ALTER TABLE public.orders
    ADD COLUMN legacy_customer_id INT;
ALTER TABLE public.order_items
    ADD COLUMN legacy_order_id INT;
ALTER TABLE public.order_items
    ADD COLUMN legacy_product_id INT;
