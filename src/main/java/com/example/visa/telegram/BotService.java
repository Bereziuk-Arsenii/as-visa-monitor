package com.example.visa.telegram;

import com.example.visa.db.UserProfileEntity;
import com.example.visa.db.UserProfileRepository;
import com.example.visa.telegram.utils.BotState;
import com.example.visa.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotService {

    private final UserProfileRepository repository;

    public BotState getBotState(Long chatId) {
        Optional<UserProfileEntity> entity = repository.findByChatId(chatId);
        return entity.isPresent() ? entity.get().getBotState() : BotState.IDLE;
    }

    public String handleIdle(Long chatId) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setChatId(chatId);
        entity.setBotState(BotState.AWAITING_NAME);
        repository.save(entity);

        return  """
                Hey there!\s
                I'm here to help you with AS VISA.\s
                Please, enter your first name:
                """;
    }

    public String handleName(Long chatId, String input) {

        if (Utils.validateName(input)) {
            return "Invalid first name. Try again please.";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setName(input);
        entity.setBotState(BotState.AWAITING_SURNAME);
        repository.save(entity);

        return "Enter your last name: ";
    }

    public String handleSurName(Long chatId, String input) {

        if (Utils.validateName(input)) {
            return "Invalid last name. Try again please.";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setSurname(input);
        entity.setBotState(BotState.AWAITING_BIRTH_YEAR);
        repository.save(entity);

        return "Enter your birthdate in format DD.MM.YYYY: ";
    }

    public String handleYear(Long chatId, String input) {

        if (!Utils.validateDate(input)) {
            return "Invalid date format. Enter it in this way DD.MM.YYYY and try again please.";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setBirthYear(Integer.parseInt(input));
        entity.setBotState(BotState.AWAITING_FOR_USER_ACCEPTION);
        repository.save(entity);

        return "Please, check carefully is everything is correct:" +
                "\nFirst name: " + entity.getName() +
                "\nLast name: " + entity.getSurname() +
                "\nBirth date: " + input;
    }

    public String handleYesOption(Long chatId) {
        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setBotState(BotState.DONE);
        repository.save(entity);

        return """
                Thank you.\s
                Your data will be apllied as soon as possible.\s
                We will message you, when everything will be done.\s
                Have a nice day!""";
    }

    public String handleNoOption(Long chatId) {
        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setBotState(BotState.AWAITING_NAME);
        repository.save(entity);

        return "Okay, let's try again." +
                "\nPlease, enter your first name: ";
    }

    public ReplyKeyboard getKeyboardAfterBirthdate() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder()
                        .text("Yes")
                        .callbackData("yesBtnClicked")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("No")
                        .callbackData("noBtnClicked")
                        .build()
        );
        inlineKeyboardMarkup.setKeyboard(List.of(row1));

        return inlineKeyboardMarkup;
    }

    public @NonNull String handleTravelPurpose(Long chatId, String input) {
        return null;
    }

    public @NonNull String handleTravelDate(Long chatId, String input) {
        return "";
    }

    public @NonNull String handlePasswordNumber(Long chatId, String input) {
        return "";
    }

    public @NonNull String handleTurkishIdentificationNumber(Long chatId, String input) {
        return "";
    }

    public @NonNull String handlePhoneNumber(Long chatId, String input) {
        return "";
    }

    public @NonNull String handleEmail(Long chatId, String input) {
        return "";
    }

    public @NonNull String handleUserAcception(Long chatId, String input) {
        return "";
    }
}
