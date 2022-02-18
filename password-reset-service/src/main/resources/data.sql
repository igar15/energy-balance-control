DELETE FROM password_reset_tokens;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO password_reset_tokens (email, token, expiry_date)
VALUES ('user1@test.com', 'b5fc98f1-40ec-485e-b316-8453f560bd78', '2052-02-05 12:10:00'),
       ('user2@test.com', '5a99dd09-d23f-44bb-8d41-b6ff44275d01', '2052-02-07 14:18:31'),
       ('user3@test.com', '52bde839-9779-4005-b81c-9131c9590d79', '2022-02-06 19:35:56');