package com.example.visa;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

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

    static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
