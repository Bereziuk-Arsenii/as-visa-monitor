package com.example.visa.telegram;

import com.example.visa.db.UserProfileEntity;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.MapDBContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;

@Component
public class BotController extends AbilityBot {
    private final long creatorid;
    private final BotService botService;

    protected BotController(TelegramConfig telegramConfig, BotService botService) {
        super(telegramConfig.getToken(), telegramConfig.getUsername(), new MapDBContext(DBMaker.memoryDB().make()));
        this.creatorid = telegramConfig.getCreatorId();
        this.botService = botService;
    }

    @Override
    public long creatorId() {
        return creatorid;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String input = update.getMessage().getText();
            message.setChatId(chatId);

            switch (botService.getBotState(chatId)) {
                case IDLE:
                    message.setText(botService.handleIdle(chatId));
                    break;
                case AWAITING_NAME:
                    message.setText(botService.handleName(chatId, input));
                    break;
                case AWAITING_SURNAME:
                    message.setText(botService.handleSurname(chatId, input));
                    break;
                case AWAITING_BIRTH_YEAR:
                    message.setText(botService.handleYear(chatId, input));
                    if (!message.getText().startsWith("Invalid")) {
                        message.setReplyMarkup(botService.travelPurposeReplyMarkup());
                    }
                    break;
                case AWAITING_TRAVEL_DATE:
                    message.setText(botService.handleTravelDate(chatId, input));
                    break;
                case AWAITING_PASSPORT_NUMBER:
                    message.setText(botService.handlePassportNumber(chatId, input));
                    break;
                case AWAITING_TURKISH_IDENTIFICATION_NUMBER:
                    message.setText(botService.handleTurkishIdentificationNumber(chatId, input));
                    break;
                case AWAITING_PHONE_NUMBER:
                    message.setText(botService.handlePhoneNumber(chatId, input));
                    break;
                case AWAITING_EMAIL:
                    message.setText(botService.handleEmail(chatId, input));
                    if (!message.getText().startsWith("Invalid")) {
                        message.setReplyMarkup(botService.acceptionReplyMarkup());
                    }
                    break;
                case AWAITING_FOR_USER_ACCEPTION:
                    message.setText("Please, use the buttons 'Yes' or 'No' to confirm your data.");
                    break;
                case READY_FOR_APPLICATION:
                    message.setText("We will submit your documents as soon as possible.");
                    break;
                case SUBMITTED:
                    message.setText("Thank you for using our service!");
                default:
                    message.setText("I don't understand you. Please follow the instructions.");
                    break;
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            message.setChatId(chatId);

            if (callbackData.startsWith("TRAVEL_PURPOSE_")) {
                String purpose = callbackData.replace("TRAVEL_PURPOSE_", "");
                message.setText(botService.handleTravelPurpose(chatId, purpose));
            } else if (callbackData.startsWith("CONFIRM_")) {
                switch (callbackData) {
                    case "CONFIRM_YES":
                        message.setText(botService.handleYesOption(chatId));
                        break;
                    case "CONFIRM_NO":
                        message.setText(botService.handleNoOption(chatId));
                        break;
                }
            }
        }

        try {
            sender.execute(message);
        } catch (TelegramApiException ignored) {}
    }

    public void sendSuccessNotification(UserProfileEntity user, byte[] screenshot) {
        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(user.getChatId().toString());

        InputFile inputFile = new InputFile(new ByteArrayInputStream(screenshot), "result.png");
        sendPhoto.setPhoto(inputFile);

        sendPhoto.setCaption(
            "Congratulations!\n" +
            "Your application was successfully submitted!\n" +
            "Nearest appointment date is: " + user.getAppointmentDate() + "\n" +
            "Thank you for using our service!!!"
        );

        try {
            sender.sendPhoto(sendPhoto);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send success photo to user " + user.getChatId() + ": " + e.getMessage());
        }
    }

    public void notifyAboutFreeSlot(UserProfileEntity user) {
        silent.send(
                """
                        🚨 !!!FREE APPOINTMENT DATE FOUND!!! 🚨
                        
                        The bot is trying to book it right now. Please wait for the confirmation screenshot.
                        
                        If you don't receive the screenshot in 30 seconds, it means something went wrong on the site. \
                        Go and book it manually IMMEDIATELY!"""
        , user.getChatId());
    }
}
