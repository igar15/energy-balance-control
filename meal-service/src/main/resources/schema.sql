DROP TABLE IF EXISTS meals;
DROP TABLE IF EXISTS meal_dates;
-- DROP SEQUENCE IF EXISTS global_seq ;
--
-- CREATE SEQUENCE global_seq START WITH 100000 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807;

CREATE TABLE meal_dates
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    user_id BIGINT NOT NULL
);
CREATE UNIQUE INDEX meal_dates_unique_user_date_idx ON meal_dates (user_id, date);

CREATE TABLE meals
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time TIME NOT NULL,
    description VARCHAR NOT NULL,
    calories INTEGER NOT NULL,
    meal_date_id BIGINT NOT NULL,
    FOREIGN KEY (meal_date_id) REFERENCES meal_dates (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX meals_unique_meal_date_time_idx ON meals (meal_date_id, time);