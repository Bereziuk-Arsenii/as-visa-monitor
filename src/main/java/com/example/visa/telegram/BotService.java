package com.example.visa.telegram;

import com.example.visa.db.UserProfileEntity;
import com.example.visa.db.UserProfileRepository;
import com.example.visa.telegram.utils.BotState;
import com.example.visa.Utils;
import com.example.visa.telegram.utils.TravelPurpose;
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

        if (!Utils.validateName(input)) {
            return "Invalid first name. Try again please.";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setName(input);
        entity.setBotState(BotState.AWAITING_SURNAME);
        repository.save(entity);

        return "Enter your surname: ";
    }

    public String handleSurname(Long chatId, String input) {

        if (!Utils.validateName(input)) {
            return "Invalid last name. Try again please.";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setSurname(input);
        entity.setBotState(BotState.AWAITING_BIRTH_YEAR);
        repository.save(entity);

        return "Enter your birth year: ";
    }

    public String handleYear(Long chatId, String input) {

        if (!Utils.validateYear(input)) {
            return "Invalid year format. Enter full year like: 2005";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setBirthYear(Integer.parseInt(input));
        entity.setBotState(BotState.AWAITING_TRAVEL_PURPOSE);
        repository.save(entity);

        return "Choose your travel purpose: ";
    }

    public ReplyKeyboard travelPurposeReplyMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<String> purposes = List.of(
                "Tourist", "Voluntary Service", "Cultural",
                "Conference", "Sport", "Family Reunion",
                "Official", "Visiting Family or Friends", "Work (Study)",
                "Profitable Activities", "Work (Commercial)", "Education",
                "Transit", "Health Reasons", "Other"
        );

        List<List<InlineKeyboardButton>> keyboard = purposes.stream()
                .map(purpose -> List.of(
                        InlineKeyboardButton.builder()
                                .text(purpose)
                                .callbackData("TRAVEL_PURPOSE_" + purpose)
                                .build()
                ))
                .toList();

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public String handleTravelPurpose(Long chatId, String input) {
        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setTravelPurpose(TravelPurpose.fromValue(input));
        entity.setBotState(BotState.AWAITING_TRAVEL_DATE);
        repository.save(entity);

        return "Please enter your travel date in format dd.mm.yyyy: ";
    }

    public String handleTravelDate(Long chatId, String input) {

        if (!Utils.isValidFutureTravelDate(input)) {
            return "Invalid date. Please enter it in dd.mm.yyyy format (like 12.12.2012) and try again:";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setTravelDate(Utils.getDate(input));
        entity.setBotState(BotState.AWAITING_PASSPORT_NUMBER);
        repository.save(entity);

        return "Please enter your password number: ";
    }

    public String handlePassportNumber(Long chatId, String input) {

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setPassportNumber(input);
        entity.setBotState(BotState.AWAITING_TURKISH_IDENTIFICATION_NUMBER);
        repository.save(entity);

        return "Enter your turkish identification number: ";
    }

    public String handleTurkishIdentificationNumber(Long chatId, String input) {

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setTcId(input);
        entity.setBotState(BotState.AWAITING_PHONE_NUMBER);
        repository.save(entity);

        return "Enter your phone number like this: +905224596999";
    }

    public String handlePhoneNumber(Long chatId, String input) {

        if (!Utils.validatePhone(input)) {
            return "Invalid phone number. Please enter a valid phone number like this: +905224596999";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setPhoneNumber(input);
        entity.setBotState(BotState.AWAITING_EMAIL);
        repository.save(entity);

        return "Enter your email: ";
    }

    public String handleEmail(Long chatId, String input) {

        if (!Utils.validateEmail(input)) {
            return "Invalid email. Please enter a valid email like this: mymail@gmail.com";
        }

        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setEmail(input);
        entity.setBotState(BotState.AWAITING_FOR_USER_ACCEPTION);
        repository.save(entity);

        return "Please check is everything is correct" +
                "\nName: " + entity.getName() +
                "\nSurname: " + entity.getSurname() +
                "\nBirth year: " + entity.getBirthYear() +
                "\nTravel purpose: " + entity.getTravelPurpose().getValue() +
                "\nTravel date: " + entity.getTravelDate() +
                "\nPassword number: " + entity.getPassportNumber() +
                "\nTurkish identification number: " + entity.getTcId() +
                "\nPhone number: " + entity.getPhoneNumber() +
                "\nEmail: " + entity.getEmail();
    }

    public String handleYesOption(Long chatId) {
        UserProfileEntity entity = repository.findByChatId(chatId).orElseThrow();
        entity.setBotState(BotState.READY_FOR_APPLICATION);
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

    public ReplyKeyboard acceptionReplyMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder()
                        .text("Yes")
                        .callbackData("CONFIRM_YES")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("No")
                        .callbackData("CONFIRM_NO")
                        .build()
        );

        inlineKeyboardMarkup.setKeyboard(List.of(row1));

        return inlineKeyboardMarkup;
    }
}
