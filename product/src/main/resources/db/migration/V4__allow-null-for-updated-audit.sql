ALTER TABLE public.categories
    ALTER COLUMN updated_by DROP NOT NULL,
    ALTER COLUMN updated_date DROP NOT NULL,
    ALTER COLUMN updated_date DROP DEFAULT;
ALTER TABLE public.products
    ALTER COLUMN updated_by DROP NOT NULL,
    ALTER COLUMN updated_date DROP NOT NULL,
    ALTER COLUMN updated_date DROP DEFAULT;
