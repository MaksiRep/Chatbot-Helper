package ru.maksirep.chatbot.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maksirep.chatbot.chatbot.helptools.KeyboardController;
import ru.maksirep.chatbot.chatbot.helptools.PhotoReader;
import ru.maksirep.chatbot.chatbot.helptools.TtsClass;
import ru.maksirep.chatbot.chatbot.helptools.Tutorial;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.other.ConstClass;
import ru.maksirep.chatbot.other.MessageConstClass;
import ru.maksirep.chatbot.service.ChatBotService;

import java.io.File;
import java.io.IOException;
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

    private KeyboardController keyboardController = new KeyboardController();

    private final PhotoReader photoReader = new PhotoReader();

    private final Tutorial tutorial = new Tutorial();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            SendMessage sendMessage = new SendMessage();

            String chatId = update.getMessage().getChatId().toString();
            File chatFolder = new File(ConstClass.CHAT_VALUES_PATH + "/" + chatId);
            if (!chatFolder.exists()) {
                chatFolder.mkdirs();
            }

            sendMessage.setReplyMarkup(keyboardController.makeCommandsKeyboard(chatId, chatBotService));

            Document document = update.getMessage().getDocument();
            List<PhotoSize> photos = update.getMessage().getPhoto();
            String message = null;
            if (update.getMessage().getText() != null) {
                message = update.getMessage().getText().trim();
            }

            ChatBot chatBot = chatBotService.getChatBotEntity(chatId);
            if (chatBot == null || chatBot.getTutorSteps() < 3) {
                sendMessage = tutorial.startTutor(chatBot, chatId, message, chatBotService);
            } else if (document != null || (photos != null && photos.size() != 0)) {
                sendMessage = photoReader.getPhoto(chatId, photos, document);
                if (sendMessage.getText().equals("")) {
                    sendMessage.setText(MessageConstClass.PHOTO_ERROR_MESSAGE);
                }
            } else if (!message.equals("")) {
                String text = keyboardController.buttonController(message, chatId, chatBotService);
                sendMessage.setReplyMarkup(keyboardController.makeCommandsKeyboard(chatId, chatBotService));
                sendMessage.setChatId(chatId);
                if (text.equals("")) {
                    File audioFile = keyboardController.buttonVoiceController(message, chatId, chatBotService);
                    if (audioFile != null) {
                        try {
                            SendAudio sendAudio = new SendAudio();
                            sendAudio.setAudio(new InputFile(audioFile));
                            sendAudio.setChatId(chatId);
                            sendMessage.setText(message);
                            execute(sendAudio);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String addRemoveCommand = keyboardController.addRemoveVoiceCommands(message, chatId, chatBotService);
                        if (!addRemoveCommand.equals("")) {
                            sendMessage.setText(addRemoveCommand);
                            sendMessage.setReplyMarkup(keyboardController.makeCommandsKeyboard(chatId, chatBotService));
                        } else {
                            sendMessage.setText(MessageConstClass.FINAL_ERROR_MESSAGE);
                        }
                    }
                } else {
                    sendMessage.setText(text);
                }
            } else {
                sendMessage.setText(MessageConstClass.FINAL_ERROR_MESSAGE);
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}