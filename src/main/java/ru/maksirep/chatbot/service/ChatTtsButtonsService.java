package ru.maksirep.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatTtsButtons;
import ru.maksirep.chatbot.other.MessageConstClass;
import ru.maksirep.chatbot.repository.ChatTtsButtonsRepository;

import java.util.ArrayList;

@Service
public class ChatTtsButtonsService {

    @Autowired
    private ChatTtsButtonsRepository chatTtsButtonsRepository;

    public String saveTtsCommands (String text, ChatBot chatBot) {
        ChatTtsButtons chatTtsButtons = chatTtsButtonsRepository.findByNameAndChatBot(text, chatBot);
        if (chatTtsButtons == null) {
            chatTtsButtonsRepository.save(new ChatTtsButtons(text, chatBot));
            return MessageConstClass.ADDED_VOICE_COMMAND;
        } else {
            return MessageConstClass.EXIST_VOICE_COMMAND;
        }
    }

    public String deleteTtsCommands (String text, ChatBot chatBot) {
        ChatTtsButtons chatTtsButtons = chatTtsButtonsRepository.findByNameAndChatBot(text, chatBot);
        if (chatTtsButtons != null) {
            chatTtsButtonsRepository.delete(chatTtsButtons);
            return MessageConstClass.DELETED_VOICE_COMMAND;
        } else {
            return MessageConstClass.NOT_EXIST_VOICE_COMMAND;
        }
    }

    public ArrayList<ChatTtsButtons> getTtsCommands (ChatBot chatBot) {
        return chatTtsButtonsRepository.findAllByChatBot(chatBot);
    }
}
