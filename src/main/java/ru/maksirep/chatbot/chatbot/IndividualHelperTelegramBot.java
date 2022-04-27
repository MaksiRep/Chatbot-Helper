package ru.maksirep.chatbot.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatPhoto;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.maksirep.chatbot.entity.ChatBotEntity;
import ru.maksirep.chatbot.other.ConstClass;
import ru.maksirep.chatbot.service.ChatBotService;

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

    private int tutorFlag;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {

            SendMessage sendMessage = new SendMessage();

            String chatId = update.getMessage().getChatId().toString();
            String message = update.getMessage().getText().trim();

            ChatBotEntity chatBotEntity = chatBotService.getChatBotEntity(chatId);

            if (chatBotEntity == null || !chatBotEntity.isTutorialEnd()) {
                sendMessage = startTutor(chatBotEntity, chatId , message);
            } else  {

                sendMessage.setText(message);
                sendMessage.setChatId(chatId);
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

    public SendMessage startTutor (ChatBotEntity chatBotEntity, String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        if (chatBotEntity == null) {
            tutorFlag = 1;
            chatBotService.saveChatBotEntity(chatId);
            sendMessage.setText(ConstClass.HELLO_MESSAGE + "\n" + ConstClass.HELLO_MESSAGE_ADDITION + "\n" + ConstClass.HELLO_MESSAGE_FIRST_QUESTION);
            sendMessage.setChatId(chatId);
        } else if (!chatBotEntity.isTutorialEnd()) {
            sendMessage.setText(tutorChatMessage(chatBotEntity, message, chatId));
            sendMessage.setChatId(chatId);
        }
        return sendMessage;
    }

    private String tutorChatMessage (ChatBotEntity chatBotEntity, String message, String chatId) {
        switch (tutorFlag) {
            case 1 :
                if (message.toLowerCase(Locale.ROOT).equals("да")) {
                    tutorFlag++;
                    chatBotEntity.setVisuallyImpaired(true);
                    chatBotService.updateChatBotEntity(chatBotEntity, chatId);
                    return ConstClass.HELLO_MESSAGE_SECOND_QUESTION;
                } else if (message.toLowerCase(Locale.ROOT).equals("нет")) {
                    tutorFlag++;
                    chatBotEntity.setVisuallyImpaired(false);
                    chatBotService.updateChatBotEntity(chatBotEntity, chatId);
                    return ConstClass.HELLO_MESSAGE_SECOND_QUESTION;
                } else {
                    return ConstClass.ERROR_MESSAGE;
                }
            case 2 :
                if (message.toLowerCase(Locale.ROOT).equals("да")) {
                    tutorFlag++;
                    chatBotEntity.setHearingImpaired(true);
                    chatBotEntity.setTutorialEnd(true);
                    chatBotService.updateChatBotEntity(chatBotEntity, chatId);
                    return getBotAbility();
                } else if (message.toLowerCase(Locale.ROOT).equals("нет")) {
                    tutorFlag++;
                    chatBotEntity.setHearingImpaired(false);
                    chatBotEntity.setTutorialEnd(true);
                    chatBotService.updateChatBotEntity(chatBotEntity, chatId);
                    return getBotAbility();
                } else {
                    return ConstClass.ERROR_MESSAGE;
                }
        }
        return ConstClass.ERROR_MESSAGE;
    }

    private String getBotAbility () {
        return ConstClass.BOT_ABILITY;
    }
}