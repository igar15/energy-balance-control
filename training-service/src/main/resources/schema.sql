DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS exercise_types;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE exercise_types
(
    id              BIGINT DEFAULT global_seq.nextval PRIMARY KEY,
    description     VARCHAR            NOT NULL,
    measure         VARCHAR            NOT NULL,
    calories_burned INTEGER            NOT NULL,
    deleted         BOOL DEFAULT FALSE NOT NULL,
    user_id         BIGINT             NOT NULL,
    h2_extra_column VARCHAR AS CASE WHEN deleted = TRUE THEN NULL ELSE description END
);
-- FOR POSTGRES USE COMMAND BELOW (INSTEAD OF CREATION extra_column)
-- CREATE UNIQUE INDEX exercise_types_unique_user_description_idx ON exercise_types (user_id, description) WHERE deleted = false;
CREATE UNIQUE INDEX exercise_types_unique_user_description_idx ON exercise_types (user_id, h2_extra_column);

CREATE TABLE exercises
(
    id               BIGINT DEFAULT global_seq.nextval PRIMARY KEY,
    date_time        TIMESTAMP NOT NULL,
    amount           INTEGER   NOT NULL,
    exercise_type_id BIGINT    NOT NULL,
    FOREIGN KEY (exercise_type_id) REFERENCES exercise_types (id) ON DELETE CASCADE
);