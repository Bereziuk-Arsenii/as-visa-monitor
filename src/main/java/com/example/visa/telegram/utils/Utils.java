package com.example.visa.telegram.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public interface Utils {
    static boolean validateName(String name) {
        if (name == null || name.isBlank()) return true;
        return name.startsWith(" ") || name.endsWith(" ");
    }

    static boolean validateDate(String date) {
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
