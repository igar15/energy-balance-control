DROP TABLE IF EXISTS password_reset_tokens;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE password_reset_tokens
(
    id             BIGINT DEFAULT global_seq.nextval PRIMARY KEY,
    email          VARCHAR              NOT NULL,
    token          VARCHAR              NOT NULL,
    expiry_date    TIMESTAMP            NOT NULL
);
CREATE UNIQUE INDEX password_reset_tokens_unique_email_idx ON password_reset_tokens (email);