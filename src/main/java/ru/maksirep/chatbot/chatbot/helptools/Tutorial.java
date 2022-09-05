package ru.maksirep.chatbot.chatbot.helptools;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.other.MessageConstClass;
import ru.maksirep.chatbot.service.ChatBotService;

import java.util.Locale;

public class Tutorial {

    public SendMessage startTutor(ChatBot chatBot, String chatId, String message, ChatBotService chatBotService) {
        SendMessage sendMessage = new SendMessage();
        if (chatBot == null) {
            chatBotService.saveChatBotEntity(chatId);
            sendMessage.setText(MessageConstClass.HELLO_MESSAGE + "\n" + MessageConstClass.HELLO_MESSAGE_ADDITION + "\n" + MessageConstClass.HELLO_MESSAGE_FIRST_QUESTION);
            sendMessage.setChatId(chatId);
        } else if (chatBot.getTutorSteps() <= 2) {
            sendMessage.setText(tutorChatMessage(chatBot, message, chatId, chatBotService));
            sendMessage.setChatId(chatId);
        }
        return sendMessage;
    }

    private String tutorChatMessage(ChatBot chatBot, String message, String chatId, ChatBotService chatBotService) {
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

    private String getBotAbility() {
        return MessageConstClass.BOT_ABILITY;
    }
}
