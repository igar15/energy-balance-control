DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE users
(
    id         BIGINT DEFAULT global_seq.nextval  PRIMARY KEY,
    name       VARCHAR                            NOT NULL,
    sex        VARCHAR                            NOT NULL,
    weight     INTEGER                            NOT NULL,
    growth     INTEGER                            NOT NULL,
    age        INTEGER                            NOT NULL,
    email      VARCHAR                            NOT NULL,
    password   VARCHAR                            NOT NULL,
    enabled    BOOL                DEFAULT FALSE  NOT NULL,
    registered TIMESTAMP           DEFAULT now()  NOT NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_unique_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);