DELETE FROM basic_exchanges;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO basic_exchanges (date, calories, user_id)
VALUES ('2022-02-05', 1891, 100000),
       ('2022-02-06', 1891, 100000),
       ('2022-02-07', 1891, 100000),
       ('2022-02-05', 1308, 100001),
       ('2022-02-06', 1308, 100001);