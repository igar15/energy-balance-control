DROP TABLE IF EXISTS meals;
DROP SEQUENCE IF EXISTS global_seq ;

CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE meals
(
    id BIGINT DEFAULT global_seq.nextval PRIMARY KEY,
    date_time TIMESTAMP NOT NULL,
    description VARCHAR NOT NULL,
    calories INTEGER NOT NULL,
    user_id BIGINT NOT NULL
);
CREATE UNIQUE INDEX meals_unique_user_datetime_idx ON meals (user_id, date_time);