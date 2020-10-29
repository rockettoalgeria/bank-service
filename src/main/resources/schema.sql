CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
                          id uuid DEFAULT uuid_generate_v4 (),
                          balance decimal DEFAULT NULL,
                          PRIMARY KEY (id)
);

DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions (
                               id SERIAL PRIMARY KEY,
                               from_account_id uuid DEFAULT NULL,
                               to_account_id uuid DEFAULT NULL,
                               amount decimal DEFAULT NULL
);