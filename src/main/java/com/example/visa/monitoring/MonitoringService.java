package com.example.visa.monitoring;
import com.example.visa.db.UserProfileEntity;
import com.example.visa.db.UserProfileRepository;
import com.example.visa.telegram.utils.BotState;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MonitoringService {
    private final MonitoringWorker monitoringWorker;
    private final UserProfileRepository userProfileRepository;

    @Scheduled(fixedDelay = 120000) // 2 min
    public void runParallelChecks() {
        List<UserProfileEntity> userProfiles = userProfileRepository.findByBotState(BotState.DONE);

        if (userProfiles.isEmpty()) {
            return;
        }

        CompletableFuture<?>[] futures = userProfiles.stream()
                .map(monitoringWorker::monitor)
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }
}