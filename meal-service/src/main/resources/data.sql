DELETE FROM meals;
DELETE FROM meal_dates;
DELETE FROM meal_dates;
-- ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO meal_dates (date, user_id)
VALUES ('2022-02-05', 200000),
       ('2022-02-06', 200000),
       ('2022-02-05', 200001);

INSERT INTO meals (time, description, calories, meal_date_id)
VALUES ('10:00:00', '1User Breakfast', 700, 1),
       ('13:00:00', '1User Lunch', 1000, 1),
       ('19:00:00', '1User Dinner', 500, 1),
       ('09:30:00', '1User Breakfast', 400, 2),
       ('13:20:00', '1User Lunch', 1100, 2),
       ('19:40:00', '1User Dinner', 600, 2),
       ('09:30:00', '2User Breakfast', 800, 3),
       ('13:20:00', '2User Lunch', 1500, 3),
       ('19:40:00', '2User Dinner', 1000, 3);