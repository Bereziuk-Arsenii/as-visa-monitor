package com.example.visa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public interface Utils {
    static boolean validateName(String name) {
        if (name == null || name.isBlank()) return false;
        return !(name.startsWith(" ") || name.endsWith(" "));
    }

    static boolean isValidFutureTravelDate(String dateStr) {

        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
                .ofPattern("dd.MM.uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        if (dateStr == null || !dateStr.matches("^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.\\d{4}$")) {
            return false;
        }
        try {
            LocalDate travelDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            return travelDate.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    static boolean validateYear(String year) {
        if (year == null || year.isBlank() || year.length() != 4) return false;

        int iYear = Integer.parseInt(year);
        int currentYear = LocalDate.now().getYear();

        return iYear <= currentYear && iYear >= 1900;
    }

    static LocalDate getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(date, formatter);
    }

    static boolean validatePhone(String phone) {
        if (phone == null || phone.isBlank()) return false;

        String regex = "^\\+\\d{12}$";

        return phone.matches(regex);
    }

    static boolean validateEmail(String email) {
        if (email == null || email.isBlank()) return false;

        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        return email.matches(regex);
    }
}
