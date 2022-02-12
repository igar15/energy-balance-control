DROP TABLE IF EXISTS basic_exchanges;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE basic_exchanges
(
    id          BIGINT DEFAULT global_seq.nextval PRIMARY KEY,
    date        DATE      NOT NULL,
    calories    INTEGER   NOT NULL,
    user_id     BIGINT    NOT NULL
);
CREATE UNIQUE INDEX basic_exchanges_unique_user_date_idx ON basic_exchanges (user_id, date);