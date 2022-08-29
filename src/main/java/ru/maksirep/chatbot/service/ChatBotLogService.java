package ru.maksirep.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatBotLog;
import ru.maksirep.chatbot.entity.ChatTtsButtons;
import ru.maksirep.chatbot.repository.ChatBotLogRepository;

import java.util.Date;

@Service
public class ChatBotLogService {

    @Autowired
    private ChatBotLogRepository chatBotLogRepository;

    public void saveLog (ChatBot chatBot) {
        chatBotLogRepository.save(new ChatBotLog(chatBot, new Date()));
    }
}
