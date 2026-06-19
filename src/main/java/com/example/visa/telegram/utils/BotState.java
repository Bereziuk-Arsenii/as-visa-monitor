package com.example.visa.telegram.utils;

public enum BotState {
    IDLE,
    AWAITING_FIRST_NAME,
    AWAITING_LAST_NAME,
    AWAITING_BIRTHDATE,
    AWAITING_FOR_USER_ACCEPTION,
    DONE
}