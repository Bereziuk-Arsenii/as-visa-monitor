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
    WORK_COMMERTIAL("Work (Commercial)"),
    EDUCATION("Education"),
    TRANSIT("Transit"),
    HEALTH_REASONS("Health Reasons"),
    OTHER("Other");

    private final String value;

    TravelPurpose(String value) {
        this.value = value;
    }
}
