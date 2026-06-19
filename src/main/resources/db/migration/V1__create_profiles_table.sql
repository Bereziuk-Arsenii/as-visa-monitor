CREATE TABLE profiles (
    chat_id BIGINT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    birthdate DATE NOT NULL,
    bot_state TEXT NOT NULL
);