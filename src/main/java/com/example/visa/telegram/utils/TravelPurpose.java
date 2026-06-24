package com.example.visa.telegram.utils;

import lombok.Getter;

@Getter
public enum TravelPurpose {
    TOURIST("Tourist"),
    VOLUNTARY_SERVICE("Voluntary Service"),
    CULTURAL("Cultural"),
    CONFERENCE("Conference"),
    SPORT("Sport"),
    FAMILY_REUNION("Family Reunion"),
    OFFICIAL("Official"),
    VISITING_FAMILY_OR_FRIENDS("Visiting Family or Friends"),
    WORK_STUDY("Work (Study)"),
    PROFITABLE_ACTIVITIES("Profitable Activities"),
    WORK_COMMERCIAL("Work (Commercial)"),
    EDUCATION("Education"),
    TRANSIT("Transit"),
    HEALTH_REASONS("Health Reasons"),
    OTHER("Other");

    private final String value;

    TravelPurpose(String value) {
        this.value = value;
    }

    public static TravelPurpose fromValue(String text) {
        for (TravelPurpose purpose : TravelPurpose.values()) {
            if (purpose.value.equalsIgnoreCase(text)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("No enum constant with text: " + text);
    }
}
