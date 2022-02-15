DELETE FROM verification_tokens;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO verification_tokens (email, token, expiry_date, email_verified)
VALUES ('user1@test.com', '04c478df-aca0-4348-9a7e-4823435c6c11', '2052-02-05 10:00:00', false),
       ('user2@test.com', 'a6480258-c4dc-4f43-9a58-4c1f7a1dda06', '2052-02-07 18:15:24', true),
       ('user3@test.com', 'a8977264-d5dd-4fg6-8n66-8c1f7a1ccc99', '2022-02-06 10:15:12', false);