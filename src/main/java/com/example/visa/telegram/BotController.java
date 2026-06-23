package com.example.visa.telegram;

import com.example.visa.db.UserProfileEntity;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.MapDBContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
                case AWAITING_TRAVEL_PURPOSE:
                    message.setText(botService.handleTravelPurpose(chatId, input));
                    break;
                case AWAITING_TRAVEL_DATE:
                    message.setText(botService.handleTravelDate(chatId, input));
                    break;
                case AWAITING_PASSWORD_NUMBER:
                    message.setText(botService.handlePasswordNumber(chatId, input));
                    break;
                case AWAITING_NAME:
                    message.setText(botService.handleName(chatId, input));
                    break;
                case AWAITING_SURNAME:
                    message.setText(botService.handleSurName(chatId, input));
                    break;
                case AWAITING_TURKISH_IDENTIFICATION_NUMBER:
                    message.setText(botService.handleTurkishIdentificationNumber(chatId, input));
                    break;
                case AWAITING_BIRTH_YEAR:
                    message.setText(botService.handleYear(chatId, input));
                    message.setReplyMarkup(botService.getKeyboardAfterBirthdate());
                    break;
                case AWAITING_PHONE_NUMBER:
                    message.setText(botService.handlePhoneNumber(chatId, input));
                    break;
                case AWAITING_EMAIL:
                    message.setText(botService.handleEmail(chatId, input));
                    break;
                case AWAITING_FOR_USER_ACCEPTION:
                    message.setText(botService.handleUserAcception(chatId, input));
                    break;
                case DONE:
                    return;
            }
        } else if (update.hasCallbackQuery()) {
            String option = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            message.setChatId(chatId);

            switch (option) {
                case "yesBtnClicked":
                    message.setText(botService.handleYesOption(chatId));
                    break;
                case "noBtnClicked":
                    message.setText(botService.handleNoOption(chatId));
                    break;
            }
        }

        try {
            sender.execute(message);
        } catch (TelegramApiException ignored) {}
    }

    public void sendSuccessNotification(UserProfileEntity user) {
        silent.send(
          "Congratulations!" +
          "\nYour applience sucessfully submited!" +
          "\nNearest appointment date is: " + user.getAppointmentDate() +
          "\n Thank you for using our service!!!"
        , user.getChatId());
    }
}
