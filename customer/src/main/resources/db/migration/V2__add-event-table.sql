CREATE TABLE public.events
(
  id SERIAL PRIMARY KEY,
  type varchar(255) NOT NULL,
  action varchar(20) NOT NULL DEFAULT 'c',
  processed boolean NOT NULL DEFAULT FALSE,
  payload jsonb,
  created_by varchar(50) NOT NULL,
  created_date timestamp WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
WITH (
  OIDS = FALSE
);

ALTER TABLE public.events OWNER to postgres;
