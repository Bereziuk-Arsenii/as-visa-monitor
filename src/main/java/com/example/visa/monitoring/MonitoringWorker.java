package com.example.visa.monitoring;


import com.example.visa.config.SiteConfig;
import com.example.visa.Utils;
import com.example.visa.db.UserProfileEntity;
import com.example.visa.db.UserProfileRepository;
import com.example.visa.telegram.BotController;
import com.example.visa.telegram.utils.BotState;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringWorker {

    private final SiteConfig siteConfig;
    private final BotController botController;
    private final UserProfileRepository userProfileRepository;

    @Async("playwrightExecutor")
    public CompletableFuture<Void> monitor(UserProfileEntity user) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch()
        ) {
            Page page = browser.newPage();
            page.setDefaultNavigationTimeout(60000);
            page.navigate(siteConfig.getUrl());

            if (checkSlot(page, user)) {
                botController.notifyAboutFreeSlot(user);
                byte[] screenshot = handleFreeSlot(page, user);
                botController.sendSuccessNotification(user, screenshot);
                user.setBotState(BotState.SUBMITTED);
                userProfileRepository.save(user);
                page.waitForTimeout(3000);
            }
        } catch (Exception e) {
            System.err.println("Monitoring failed for user " + user.getChatId() + ": " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private boolean checkSlot(Page page, UserProfileEntity user) {
        log.info("Checking slot for user {}", user.getChatId());
        LocalDate travelDate = user.getTravelDate();

        int day = travelDate.getDayOfMonth();
        String month = Utils.capitalize(travelDate.getMonth().toString());
        int year = travelDate.getYear();

        String currentMonth = Utils.capitalize(LocalDate.now().getMonth().toString());
        int currentYear = LocalDate.now().getYear();


        Locator travelDateInput = page.locator("input[id='TravelDate']");
        travelDateInput.click();
        page.locator("th[class='datepicker-switch']").and(page.getByText(currentMonth + " " + currentYear)).click();
        page.locator("th[class='datepicker-switch']").and(page.getByText(currentYear+"", new Page.GetByTextOptions().setExact(true))).click();
        page.locator("span[class = 'year']").and(page.getByText(year + "")).click();
        page.locator("span[class = 'month']").and(page.getByText(month.substring(0, 3))).click();
        page.locator("td.day:not(.old):not(.new)")
                .and(page.getByText(String.valueOf(day), new Page.GetByTextOptions().setExact(true)))
                .click();

        page.waitForTimeout(500);
        Locator appointmentInput = page.locator("input[id='datepicker']");
        appointmentInput.click(new Locator.ClickOptions().setForce(true));

        Locator appointmentDiv = page.locator("div[class='datepicker-days']");
        Locator appointmentCalendar = appointmentDiv.locator("table[class='table-condensed']");
        Locator availableDays = appointmentCalendar.locator("td.day:not(.disabled):not(.old):not(.new)");

        if (availableDays.count() > 0) {
            availableDays.first().click();
            saveFoundDate(page, appointmentInput, user);
            log.info("Avaliable appointment date for user {}", user.getChatId());
            return true;
        }

        appointmentDiv.locator("th[class='next']").click(); // next month btn
        page.waitForTimeout(500);

        if (availableDays.count() > 0) {
            availableDays.first().click();
            saveFoundDate(page, appointmentInput, user);
            log.info("Available appointment date for user {}", user.getChatId());
            return true;
        }

        log.info("No avaliable appointment date for user {}", user.getChatId());
        return false;
    }

    private void saveFoundDate(Page page, Locator appointmentInput, UserProfileEntity user) {
        page.waitForTimeout(500);
        String dateString = appointmentInput.inputValue();

        if (dateString != null && !dateString.isEmpty()) {
            user.setAppointmentDate(LocalDate.parse(
                    dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            userProfileRepository.save(user);
        }
    }

    private byte[] handleFreeSlot(Page page, UserProfileEntity user) {
        page.locator("span[id='select2-TravelSubject-container']").click();
        page.locator("li[class='select2-results__option']")
                .and(page.getByText(user.getTravelPurpose().getValue())).click();

        Locator passwordInput = page.locator("input[name='PassaportNumber']");
        passwordInput.fill(user.getPassportNumber());

        Locator nameInput = page.locator("input[name='Name']");
        nameInput.fill(user.getName());

        Locator surnameInput = page.locator("input[name='Surname']");
        surnameInput.fill(user.getSurname());

        Locator tcIdInput = page.locator("input[name='TcKimlikNo']");
        tcIdInput.fill(user.getTcId());

        Locator birthYearInput = page.locator("input[name='DogumYili']");
        birthYearInput.fill(user.getBirthYear() + "");

        Locator phoneNumberInput = page.locator("input[name='Phone']");
        phoneNumberInput.fill(user.getPhoneNumber());

        Locator emailInput = page.locator("input[name='Email']");
        emailInput.fill(user.getEmail());

        Locator makeAnAppointmentBtn = page.locator("button[class='theme_btn']")
                .and(page.getByText("Make an appointment"));
        makeAnAppointmentBtn.click();

        page.waitForTimeout(500);
        Locator yesBtn = page.locator("button.swal2-confirm").and(page.getByText("Yes"));
        yesBtn.click();
        log.info("USER {} WAS SUBMITTED", user.getChatId());

        page.waitForTimeout(5000);
        return page.screenshot();
    }
}
