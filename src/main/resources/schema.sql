CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS sku (
    id uuid DEFAULT uuid_generate_v4(),
    price decimal(19,4) NOT NULL,
    quote decimal(19,4) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    exchange VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);