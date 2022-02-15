DROP TABLE IF EXISTS verification_tokens;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE verification_tokens
(
    id             BIGINT DEFAULT global_seq.nextval PRIMARY KEY,
    email          VARCHAR              NOT NULL,
    token          VARCHAR              NOT NULL,
    expiry_date    TIMESTAMP            NOT NULL,
    email_verified BOOL   DEFAULT FALSE NOT NULL
);
CREATE UNIQUE INDEX verification_tokens_unique_email_idx ON verification_tokens (email);