DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, sex, weight, growth, age, email, password, enabled)
VALUES ('John Smith', 'MAN', 90, 185, 34, 'user@gmail.com', '{noop}password', true),
       ('Viktor Wran', 'MAN', 85, 178, 41, 'admin@gmail.com', '{noop}admin', true),
       ('Jack London', 'MAN', 83, 174, 38, 'jack@gmail.com', '{noop}password', false);

INSERT INTO user_roles(role, user_id)
VALUES ('USER', 100000),
       ('USER', 100001),
       ('ADMIN', 100001),
       ('USER', 100002);