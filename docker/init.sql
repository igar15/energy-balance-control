CREATE SEQUENCE IF NOT EXISTS global_seq START WITH 100000;

CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    name       VARCHAR                            NOT NULL,
    email      VARCHAR                            NOT NULL,
    sex        VARCHAR                            NOT NULL,
    weight     INTEGER                            NOT NULL,
    growth     INTEGER                            NOT NULL,
    age        INTEGER                            NOT NULL,
    password   VARCHAR                            NOT NULL,
    enabled    BOOL                DEFAULT FALSE  NOT NULL,
    registered TIMESTAMP           DEFAULT now()  NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS users_unique_email_idx ON users (email);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_unique_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS meals
(
    id          BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    date_time   TIMESTAMP NOT NULL,
    description VARCHAR   NOT NULL,
    calories    INTEGER   NOT NULL,
    user_id     BIGINT    NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS meals_unique_user_datetime_idx ON meals (user_id, date_time);

CREATE TABLE IF NOT EXISTS exercise_types
(
    id              BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    description     VARCHAR            NOT NULL,
    measure         VARCHAR            NOT NULL,
    calories_burned INTEGER            NOT NULL,
    deleted         BOOL DEFAULT FALSE NOT NULL,
    user_id         BIGINT             NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS exercise_types_unique_user_description_idx ON exercise_types (user_id, description) WHERE deleted = false;

CREATE TABLE IF NOT EXISTS exercises
(
    id               BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    date_time        TIMESTAMP NOT NULL,
    amount           INTEGER   NOT NULL,
    exercise_type_id BIGINT    NOT NULL,
    FOREIGN KEY (exercise_type_id) REFERENCES exercise_types (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    id             BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    email          VARCHAR              NOT NULL,
    token          VARCHAR              NOT NULL,
    expiry_date    TIMESTAMP            NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS password_reset_tokens_unique_email_idx ON password_reset_tokens (email);

CREATE TABLE IF NOT EXISTS verification_tokens
(
    id             BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    email          VARCHAR              NOT NULL,
    token          VARCHAR              NOT NULL,
    expiry_date    TIMESTAMP            NOT NULL,
    email_verified BOOL   DEFAULT FALSE NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS verification_tokens_unique_email_idx ON verification_tokens (email);

CREATE TABLE IF NOT EXISTS basic_exchanges
(
    id          BIGINT PRIMARY KEY  DEFAULT nextval('global_seq'),
    date        DATE      NOT NULL,
    calories    INTEGER   NOT NULL,
    user_id     BIGINT    NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS basic_exchanges_unique_user_date_idx ON basic_exchanges (user_id, date);