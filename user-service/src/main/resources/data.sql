DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, sex, weight, growth, age, password, enabled)
VALUES ('John Smith', 'user@gmail.com', 'MAN', 90, 185, 34, '{noop}password', true),
       ('Viktor Wran', 'admin@gmail.com', 'MAN', 85, 178, 41, '{noop}admin', true),
       ('Jack London', 'jack@gmail.com', 'MAN', 83, 174, 38, '{noop}password', false);

INSERT INTO user_roles(role, user_id)
VALUES ('USER', 100000),
       ('USER', 100001),
       ('ADMIN', 100001),
       ('USER', 100002);