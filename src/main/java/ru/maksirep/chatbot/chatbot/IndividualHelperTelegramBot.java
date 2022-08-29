package ru.maksirep.chatbot.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maksirep.chatbot.chatbot.helptools.PhotoReader;
import ru.maksirep.chatbot.chatbot.helptools.TtsClass;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatTtsButtons;
import ru.maksirep.chatbot.other.ConstClass;
import ru.maksirep.chatbot.other.MessageConstClass;
import ru.maksirep.chatbot.service.ChatBotService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class IndividualHelperTelegramBot extends TelegramLongPollingBot {

    @Autowired
    private ChatBotService chatBotService;

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    private ReplyKeyboardMarkup replyKeyboardMarkup;

    private final PhotoReader photoReader = new PhotoReader();

    private final TtsClass ttsClass = new TtsClass();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            SendMessage sendMessage = new SendMessage();



            String chatId = update.getMessage().getChatId().toString();
            File chatFolder = new File(ConstClass.CHAT_VALUES_PATH + "/" + chatId);
            if (!chatFolder.exists()) {
                chatFolder.mkdirs();
            }

            makeCommandsKeyboard(chatId);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            Document document = update.getMessage().getDocument();
            List<PhotoSize> photos = update.getMessage().getPhoto();
            String message = null;
            if (update.getMessage().getText() != null) {
                message = update.getMessage().getText().trim();
            }

            ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
            if (chatBot == null || chatBot.getTutorSteps() < 3) {
                sendMessage = startTutor(chatBot, chatId, message);
            } else if (document != null || (photos != null && photos.size() != 0)) {
                sendMessage = getPhoto(chatId, photos, document);
                if (sendMessage.getText().equals("")) {
                    sendMessage.setText(MessageConstClass.PHOTO_ERROR_MESSAGE);
                }
            } else if (!message.equals("")) {
                String text = mainButtonController(message, chatId);
                sendMessage.setChatId(chatId);
                if (text == null || text.equals("")) {
                    sendMessage.setText(MessageConstClass.FINAL_ERROR_MESSAGE);
                } else {
                    sendMessage.setText(text);
                }
                //TODO: Работа с аудио
//                Audio audio = update.getMessage().getAudio();
//                if (audio != null) {
//                    try {
//                        SendAudio sendAudio = new SendAudio();
//                        sendAudio.setChatId(update.getMessage().getChatId().toString());
//                        sendAudio.setAudio(new InputFile(new File("C:/Users/Public/Music/test.mp3")));
//                        execute(sendAudio);
//                    } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                    }
//            }
            } else {
                sendMessage.setChatId(chatId);
                sendMessage.setText(MessageConstClass.FINAL_ERROR_MESSAGE);
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    private void makeCommandsKeyboard(String chatId) {
        ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
        if (chatBot != null) {
            replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
            //TODO: Заикинуть все в keyboardRowsHandler
            switch (chatBot.getMenuPos()) {
                case (0) : {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRows.add(keyboardRow);
                    keyboardRow.add(new KeyboardButton(MessageConstClass.ABILITY_MESSAGE));
                    keyboardRow.add(new KeyboardButton(MessageConstClass.EMERGENCY_MESSAGE));
                    keyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_MESSAGE));
                    break;
                }
                case (1): {
                    KeyboardRow firstKeyboardRow = new KeyboardRow();
                    KeyboardRow secondKeyboardRow = new KeyboardRow();
                    KeyboardRow thirdKeyboardRow = new KeyboardRow();
                    KeyboardRow fourthKeyboardRow = new KeyboardRow();
                    keyboardRows.add(firstKeyboardRow);
                    keyboardRows.add(secondKeyboardRow);
                    keyboardRows.add(thirdKeyboardRow);
                    keyboardRows.add(fourthKeyboardRow);
                    firstKeyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_FIRE));
                    secondKeyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_POLICE));
                    thirdKeyboardRow.add(new KeyboardButton(MessageConstClass.VOICE_COMMANDS_AMBULANCE));
                    fourthKeyboardRow.add(new KeyboardButton(MessageConstClass.BACK_COMMAND));
                    break;
                }
                case 2: {

                }
                case 3: {

                }
            }
            replyKeyboardMarkup.setKeyboard(keyboardRows);
        }
    }


    private String mainButtonController(String inputText, String chatId) {
        switch (inputText) {
            case MessageConstClass.ABILITY_MESSAGE: {
                return MessageConstClass.BOT_ABILITY;
            }
            case MessageConstClass.EMERGENCY_MESSAGE: {
                ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
                chatBot.setMenuPos(1);
                chatBotService.updateChatBotEntity(chatBot, chatId);
                return MessageConstClass.EMERGENCY_ANSWER_MESSAGE;
            }
            case MessageConstClass.VOICE_COMMANDS_MESSAGE: {
                chatBotService.getChatBotEntity(chatId).setMenuPos(3);
                return MessageConstClass.VOICE_COMMANDS_EDITOR_MESSAGE;
            }
        }
        return "";
    }

    private SendMessage getPhoto(String chatId, List<PhotoSize> photos, Document document) {
        SendMessage sendMessage = new SendMessage();
        GetFile getFileRequest = new GetFile();
        String textFromPhoto = "";
        try {
            if (photos != null && photos.size() != 0) {
                getFileRequest.setFileId(photos.get(photos.size() - 1).getFileId());
                textFromPhoto = photoReader.readPhoto(chatId, getFileRequest.getFileId());
            } else if (document != null) {
                getFileRequest.setFileId(document.getFileId());
                textFromPhoto = photoReader.readPhoto(chatId, getFileRequest.getFileId());
            }
            sendMessage.setText(textFromPhoto);
            sendMessage.setChatId(chatId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sendMessage;
    }

    private SendMessage startTutor(ChatBot chatBot, String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        if (chatBot == null) {
            chatBotService.saveChatBotEntity(chatId);
            sendMessage.setText(MessageConstClass.HELLO_MESSAGE + "\n" + MessageConstClass.HELLO_MESSAGE_ADDITION + "\n" + MessageConstClass.HELLO_MESSAGE_FIRST_QUESTION);
            sendMessage.setChatId(chatId);
        } else if (chatBot.getTutorSteps() <= 2) {
            sendMessage.setText(tutorChatMessage(chatBot, message, chatId));
            sendMessage.setChatId(chatId);
        }
        return sendMessage;
    }

    private String tutorChatMessage(ChatBot chatBot, String message, String chatId) {
        switch (chatBot.getTutorSteps()) {
            case 1:
                if (message.toLowerCase(Locale.ROOT).equals("да")) {
                    chatBot.setTutorSteps(2);
                    chatBot.setVisuallyImpaired(true);
                    chatBotService.updateChatBotEntity(chatBot, chatId);
                    return MessageConstClass.HELLO_MESSAGE_SECOND_QUESTION;
                } else if (message.toLowerCase(Locale.ROOT).equals("нет")) {
                    chatBot.setTutorSteps(2);
                    chatBot.setVisuallyImpaired(false);
                    chatBotService.updateChatBotEntity(chatBot, chatId);
                    return MessageConstClass.HELLO_MESSAGE_SECOND_QUESTION;
                } else {
                    return MessageConstClass.ERROR_MESSAGE;
                }
            case 2:
                if (message.toLowerCase(Locale.ROOT).equals("да")) {
                    chatBot.setTutorSteps(3);
                    chatBot.setHearingImpaired(true);
                    chatBotService.updateChatBotEntity(chatBot, chatId);
                    return getBotAbility();
                } else if (message.toLowerCase(Locale.ROOT).equals("нет")) {
                    chatBot.setTutorSteps(3);
                    chatBot.setHearingImpaired(false);
                    chatBotService.updateChatBotEntity(chatBot, chatId);
                    return getBotAbility();
                } else {
                    return MessageConstClass.ERROR_MESSAGE;
                }
        }
        return MessageConstClass.ERROR_MESSAGE;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private String getBotAbility() {
        return MessageConstClass.BOT_ABILITY;
    }
}