CREATE TABLE profiles (
    chat_id BIGINT PRIMARY KEY,
    travel_purpose TEXT,
    travel_date DATE,
    password_number TEXT,
    name TEXT,
    surname TEXT,
    tc_id TEXT,
    birth_year INT,
    phone_number TEXT,
    email TEXT,
    bot_state TEXT
);