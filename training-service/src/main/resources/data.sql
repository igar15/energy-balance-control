DELETE FROM exercises;
DELETE FROM exercise_types;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO exercise_types (description, measure, calories_burned, deleted, user_id)
VALUES ('1User Exercise Type 1', 'times', 2, false, 200000),
       ('1User Exercise Type 2', 'meters', 3, false, 200000),
       ('1User Exercise Type 3', 'times', 1, false, 200000),
       ('1User Exercise Type Deleted', 'times', 2, true, 200000),
       ('2User Exercise Type 1', 'times', 4, false, 200001);

INSERT INTO exercises (date_time, amount, exercise_type_id)
VALUES ('2022-02-05 11:20:00', 30, 100000),
       ('2022-02-05 13:50:00', 100, 100001),
       ('2022-02-05 15:15:00', 20, 100000),
       ('2022-02-06 14:15:00', 50, 100000),
       ('2022-02-06 18:15:00', 20, 100002),
       ('2022-02-05 8:15:00', 90, 100004);