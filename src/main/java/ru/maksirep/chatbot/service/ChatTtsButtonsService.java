package ru.maksirep.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatTtsButtons;
import ru.maksirep.chatbot.repository.ChatTtsButtonsRepository;

import java.util.ArrayList;

@Service
public class ChatTtsButtonsService {

    @Autowired
    private ChatTtsButtonsRepository chatTtsButtonsRepository;

    public void saveTtsCommands (String text, ChatBot chatBot) {
        chatTtsButtonsRepository.save(new ChatTtsButtons(text, chatBot));
    }

    public ArrayList<ChatTtsButtons> getTtsCommands (ChatBot chatBot) {
        return chatTtsButtonsRepository.findAllByChatBot(chatBot);
    }
}
