package com.example.visa.db;

import com.example.visa.telegram.utils.BotState;
import com.example.visa.telegram.utils.TravelPurpose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "profiles")
public class UserProfileEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_purpose")
    private TravelPurpose travelPurpose;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    @Column(name = "password_number")
    private String passwordNumber;

    @Column(name = "name")
    private String name = "";

    @Column(name = "surname")
    private String surname;

    @Column(name = "tc_id")
    private String tcId;

    @Column(name = "birth_year")
    private int birthDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "bot_state")
    private BotState botState =  BotState.IDLE;
}
