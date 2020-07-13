ALTER TABLE public.payments
    DROP CONSTRAINT payments_customer_id_fkey;
ALTER TABLE public.payments
    DROP COLUMN customer_id;
