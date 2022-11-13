package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardMarkupGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.text.MessageFormat;
import java.util.LinkedHashMap;

@Slf4j
@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private final RosstinderClient rosstinderClient;
    private final AnswerSender answerSender;
    private final ReplyKeyboardMarkupGetter replyKeyboardMarkupGetter;

    public UpdateController(RosstinderClient rosstinderClient, AnswerSender answerSender, ReplyKeyboardMarkupGetter replyKeyboardMarkupGetter) {
        this.rosstinderClient = rosstinderClient;
        this.answerSender = answerSender;
        this.replyKeyboardMarkupGetter = replyKeyboardMarkupGetter;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.debug("Получен пустой update");
            return;
        }
        if (update.getMessage() == null) {
            log.debug("Получено пустое сообщение");
            return;
        }
        if (update.getMessage().getText() != null) {
            processTextMessage(update);
        }
        log.debug("Получен неподдерживаемый тип сообщения");
    }

    private void processTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotState userStatus = BotState.valueOfLabel(rosstinderClient.getUserStatus(chatId));
        log.info(MessageFormat.format("Получено сообщение \"{0}\" от пользователя #{1} со статусом \"{2}\"",
                message.getText(), chatId, userStatus));
        switch (userStatus) {
            case NEW -> processNew(update);
            case CHOOSE_GENDER -> processChooseGender(update);
            case INPUT_NAME -> processInputName(update);
            case INPUT_DESCRIPTION -> processInputDescription(update);
            case CHOOSE_PREFERENCE -> processChoosePreference(update);
            case MENU -> processMenu(update);
            case SEARCH -> processSearch(update);
            case PROFILE -> processProfile(update);
            case FAVORITES -> processFavorites(update);
            case UPDATE_GENDER -> processUpdateGender(update);
            case UPDATE_NAME -> processUpdateName(update);
            case UPDATE_DESCRIPTION -> processUpdateDescription(update);
            case UPDATE_PREFERENCE -> processUpdatePreference(update);
        }
    }

    private void isMessageStart(Update update) {
        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();
        if (textMessage.equals("/start")) {
            String caption = rosstinderClient.getGenderAndName(chatId);
            setView(answerSender.sendPhotoWithCaption(update, caption, rosstinderClient.getImageProfile(chatId)));
        }
    }

    private void processNew(Update update) {
        ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForGender();
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите пол:", keyboard));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "choose gender");
    }

    private void processChooseGender(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня")) {
            rosstinderClient.setGender(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлен пол {1}", chatId, textMessage));
            setView(answerSender.sendMessageWithText(update, "Введите имя"));
            rosstinderClient.setNewStatus(update.getMessage().getChatId(), "input name");
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForGender();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пол:", keyboard));
        }
    }

    private void processInputName(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setName(chatId, textMessage);
        log.info(MessageFormat.format("Для пользователя #{0} установлено имя {1}", chatId, textMessage));
        setView(answerSender.sendMessageWithText(update, "Введите описание"));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "input description");
    }

    private void processInputDescription(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setDescription(chatId, textMessage);
        log.info(MessageFormat.format("Для пользователя #{0} установлено описание {1}", chatId, textMessage));
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите предпочтения:", replyKeyboardMarkupGetter.getKeyboardForPreference()));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "choose preference");
    }

    private void processChoosePreference(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня") || textMessage.equals("Всех")) {
            rosstinderClient.setPreference(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлены предпочтения {1}", chatId, textMessage));
            String caption = rosstinderClient.getGenderAndName(chatId);
            setView(answerSender.sendPhotoWithCaption(update, caption, rosstinderClient.getImageProfile(chatId)));
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт меню", replyKeyboardMarkupGetter.getKeyboardForMenu()));
            rosstinderClient.setNewStatus(update.getMessage().getChatId(), "menu");
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForPreference();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите предпочтения:", keyboard));
        }
    }

    private void processMenu(Update update) {
        isMessageStart(update);
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (textMessage) {
            case "Поиск" -> {
                LinkedHashMap<String, Object> nextProfile = rosstinderClient.getNextProfile(chatId);
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        (String) nextProfile.get("nameAndGender"),
                        (byte[]) nextProfile.get("image"),
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "search");
            }
            case "Любимцы" -> {
                LinkedHashMap<String, Object> nextFavorite = rosstinderClient.getNextFavorite(chatId);
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        (String) nextFavorite.get("nameAndGender"),
                        (byte[]) nextFavorite.get("image"),
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "favorites");
            }
            case "Анкета" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForProfile();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getGenderAndName(chatId),
                        rosstinderClient.getImageProfile(chatId),
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "profile");
            }
            default -> {
                log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт меню:", keyboard));
            }
        }
    }

    private void processFavorites(Update update) {
        isMessageStart(update);
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (textMessage) {
            case "->" -> {
                LinkedHashMap<String, Object> nextFavorite = rosstinderClient.getNextFavorite(chatId);
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        (String) nextFavorite.get("nameAndGender"),
                        (byte[]) nextFavorite.get("image"),
                        keyboard));
            }
            case "<-" -> {
                LinkedHashMap<String, Object> previousFavorite = rosstinderClient.getPreviousFavorite(chatId);
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        (String) previousFavorite.get("nameAndGender"),
                        (byte[]) previousFavorite.get("image"),
                        keyboard));
            }
            case "Меню" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update,
                        "Выберите пункт меню:",
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "menu");
            }
            default -> {
                log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите доступное действие:", keyboard));
            }
        }
    }

    private void processProfile(Update update) {
        isMessageStart(update);
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (textMessage) {
            case "Изменить имя" -> {
                rosstinderClient.setNewStatus(chatId, "update name");
                setView(answerSender.sendMessageWithText(update, "Введите новое имя"));
            }
            case "Изменить пол" -> {
                rosstinderClient.setNewStatus(chatId, "update gender");
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите новый пол:", replyKeyboardMarkupGetter.getKeyboardForGender()));
            }
            case "Изменить описание" -> {
                rosstinderClient.setNewStatus(chatId, "update description");
                setView(answerSender.sendMessageWithText(update, "Введите новое описание"));
            }
            case "Изменить предпочтения" -> {
                rosstinderClient.setNewStatus(chatId, "update preference");
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите новые предпочтения:", replyKeyboardMarkupGetter.getKeyboardForPreference()));
            }
            case "Меню" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update,
                        "Выберите пункт меню:",
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "menu");
            }
            default -> {
                log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForProfile();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите доступное действие:", keyboard));
            }
        }
    }

    private void processSearch(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (textMessage) {
            case "->" -> {
                rosstinderClient.setLikeOrDislike(chatId, "true");
                if (!rosstinderClient.getRelationship(chatId).equals("")) {
                    setView(answerSender.sendMessageWithText(update, "Вы любимы"));
                    log.info(MessageFormat.format("Обнаружена взаимная симпатия для пользователя #{0}", chatId));
                }
                LinkedHashMap<String, Object> nextProfile = rosstinderClient.getNextProfile(chatId);
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        (String) nextProfile.get("nameAndGender"),
                        (byte[]) nextProfile.get("image"),
                        keyboard));
            }
            case "<-" -> {
                LinkedHashMap<String, Object> nextProfile = rosstinderClient.getNextProfile(chatId);
                rosstinderClient.setLikeOrDislike(chatId, "false");
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        (String) nextProfile.get("nameAndGender"),
                        (byte[]) nextProfile.get("image"),
                        keyboard));
            }
            case "Меню" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update,
                        "Выберите пункт меню:",
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "menu");
            }
            default -> {
                log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите доступное действие:", keyboard));
            }
        }
    }

    private void processUpdateGender(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня")) {
            rosstinderClient.setGender(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлен пол {1}", chatId, textMessage));
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
            rosstinderClient.setNewStatus(chatId, "profile");
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForGender();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пол:", keyboard));
        }
    }

    private void processUpdateName(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setName(chatId, textMessage);
        log.info(MessageFormat.format("Для пользователя #{0} установлено имя {1}", chatId, textMessage));
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
        rosstinderClient.setNewStatus(chatId, "profile");
    }

    private void processUpdateDescription(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setDescription(chatId, textMessage);
        log.info(MessageFormat.format("Для пользователя #{0} установлено описание {1}", chatId, textMessage));
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
        rosstinderClient.setNewStatus(chatId, "profile");
    }

    private void processUpdatePreference(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня") || textMessage.equals("Всех")) {
            rosstinderClient.setPreference(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлены предпочтения {1}", chatId, textMessage));
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
            rosstinderClient.setNewStatus(chatId, "profile");
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForPreference();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите предпочтения:", keyboard));
        }
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }
}
