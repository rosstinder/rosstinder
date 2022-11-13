package org.rosstinder.prerevolutionarytindertgbotclient.controller;

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
            // todo: добавить логирование
            return;
        }
        if (update.getMessage() == null) {
            // todo: добавить логирование
            return;
        }
        if (update.getMessage().getText() != null) {
            processTextMessage(update);
        }
    }

    private void processTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotState userStatus = BotState.valueOfLabel(rosstinderClient.getUserStatus(chatId));

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
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня")) {
            Long chatId = update.getMessage().getChatId();
            rosstinderClient.setGender(chatId, textMessage);
            setView(answerSender.sendMessageWithText(update, "Введите имя"));
            rosstinderClient.setNewStatus(update.getMessage().getChatId(), "input name");
        } else {
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForGender();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пол:", keyboard));
        }
    }

    private void processInputName(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setName(chatId, textMessage);
        setView(answerSender.sendMessageWithText(update, "Введите описание"));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "input description");
    }

    private void processInputDescription(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setDescription(chatId, textMessage);
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите предпочтения:", replyKeyboardMarkupGetter.getKeyboardForPreference()));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "choose preference");
    }

    private void processChoosePreference(Update update) {
        String textMessage = update.getMessage().getText();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня") || textMessage.equals("Всех")) {
            Long chatId = update.getMessage().getChatId();
            rosstinderClient.setPreference(chatId, textMessage);
            String caption = rosstinderClient.getGenderAndName(chatId);
            setView(answerSender.sendPhotoWithCaption(update, caption, rosstinderClient.getImageProfile(chatId)));
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт меню", replyKeyboardMarkupGetter.getKeyboardForMenu()));
            rosstinderClient.setNewStatus(update.getMessage().getChatId(), "menu");
        } else {
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForPreference();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите предпочтения:", keyboard));
        }
    }

    private void processMenu(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (textMessage) {
            case "Поиск" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getNameAndGenderForNextProfile(chatId),
                        rosstinderClient.getImageNextProfile(chatId),
                        keyboard));
                rosstinderClient.setNewStatus(chatId, "search");
            }
            case "Любимцы" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getNameAndGenderAndStatusForNextFavorite(chatId),
                        rosstinderClient.getImageNextFavorite(chatId),
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
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт меню:", keyboard));
            }
        }
    }

    private void processFavorites(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (textMessage) {
            case "->" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getNameAndGenderAndStatusForNextFavorite(chatId),
                        rosstinderClient.getImageNextFavorite(chatId),
                        keyboard));
            }
            case "<-" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getNameAndGenderAndStatusForPreviousFavorite(chatId),
                        rosstinderClient.getImagePreviousFavorite(chatId),
                        keyboard));
            }
            case "Меню" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update,
                        "Выберите пункт меню:",
                        keyboard));
            }
            default -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите доступное действие:", keyboard));
            }
        }
    }

    private void processProfile(Update update) {
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
            default -> {
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
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getNameAndGenderForNextProfile(chatId),
                        rosstinderClient.getImageNextProfile(chatId),
                        keyboard));
            }
            case "<-" -> {
                rosstinderClient.setLikeOrDislike(chatId, "false");
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendPhotoWithKeyboard(update,
                        rosstinderClient.getNameAndGenderForNextProfile(chatId),
                        rosstinderClient.getImageNextProfile(chatId),
                        keyboard));
            }
            case "Меню" -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForMenu();
                setView(answerSender.sendMessageWithKeyboard(update,
                        "Выберите пункт меню:",
                        keyboard));
            }
            default -> {
                ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForSearchAndFavorites();
                setView(answerSender.sendMessageWithKeyboard(update, "Выберите доступное действие:", keyboard));
            }
        }
    }

    private void processUpdateGender(Update update) {
        String textMessage = update.getMessage().getText();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня")) {
            Long chatId = update.getMessage().getChatId();
            rosstinderClient.setGender(chatId, textMessage);
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
            rosstinderClient.setNewStatus(update.getMessage().getChatId(), "profile");
        } else {
            ReplyKeyboardMarkup keyboard = replyKeyboardMarkupGetter.getKeyboardForGender();
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пол:", keyboard));
        }
    }

    private void processUpdateName(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setName(chatId, textMessage);
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "profile");
    }

    private void processUpdateDescription(Update update) {
        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        rosstinderClient.setDescription(chatId, textMessage);
        setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
        rosstinderClient.setNewStatus(update.getMessage().getChatId(), "profile");
    }

    private void processUpdatePreference(Update update) {
        String textMessage = update.getMessage().getText();
        if (textMessage.equals("Сударъ") || textMessage.equals("Сударыня") || textMessage.equals("Всех")) {
            Long chatId = update.getMessage().getChatId();
            rosstinderClient.setPreference(chatId, textMessage);
            setView(answerSender.sendMessageWithKeyboard(update, "Выберите пункт анкеты, который хотите изменить", replyKeyboardMarkupGetter.getKeyboardForProfile()));
            rosstinderClient.setNewStatus(update.getMessage().getChatId(), "profile");
        } else {
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
