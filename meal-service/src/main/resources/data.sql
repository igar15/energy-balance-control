DELETE FROM meals;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2022-02-05 10:00:00', '1User Breakfast', 700, 100000),
       ('2022-02-05 13:00:00', '1User Lunch', 1000, 100000),
       ('2022-02-05 19:00:00', '1User Dinner', 500, 100000),
       ('2022-02-06 09:30:00', '1User Breakfast', 400, 100000),
       ('2022-02-06 13:20:00', '1User Lunch', 1100, 100000),
       ('2022-02-06 19:40:00', '1User Dinner', 600, 100000),
       ('2022-02-07 00:00:00', '1User Night Eating', 100, 100000),
       ('2022-02-05 09:30:00', '2User Breakfast', 800, 100001),
       ('2022-02-05 13:20:00', '2User Lunch', 1500, 100001);
