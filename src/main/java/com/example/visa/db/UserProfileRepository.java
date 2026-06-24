package com.example.visa.db;

import com.example.visa.telegram.utils.BotState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByChatId(Long chatId);
    List<UserProfileEntity> findByBotState(BotState botState);
}