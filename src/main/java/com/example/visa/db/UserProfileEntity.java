package com.example.visa.db;

import com.example.visa.telegram.utils.BotState;
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

    @Column(nullable = false, name = "first_name")
    private String firstName = "";

    @Column(nullable = false, name = "last_name")
    private String lastName = "";

    @Column(nullable = false, name = "birthdate")
    private LocalDate birthDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "bot_state")
    private BotState botState =  BotState.IDLE;
}
