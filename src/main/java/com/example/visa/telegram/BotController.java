package com.example.visa.telegram;

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
                case AWAITING_FIRST_NAME:
                    message.setText(botService.handleFirstName(chatId, input));
                    break;
                case AWAITING_LAST_NAME:
                    message.setText(botService.handleLastName(chatId, input));
                    break;
                case AWAITING_BIRTHDATE:
                    message.setText(botService.handleBirthday(chatId, input));
                    message.setReplyMarkup(botService.getKeyboardAfterBirthdate());
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
}
