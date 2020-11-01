CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
                          id uuid DEFAULT uuid_generate_v4 (),
                          balance decimal DEFAULT NULL,
                          PRIMARY KEY (id)
);