package ru.maksirep.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksirep.chatbot.entity.ChatBotLogEntity;
import ru.maksirep.chatbot.repository.ChatBotLogRepository;

import java.util.Date;

@Service
public class ChatBotLogService {

    @Autowired
    private ChatBotLogRepository chatBotLogRepository;

    public void saveLog (String chatId) {
        chatBotLogRepository.save(new ChatBotLogEntity(chatId, new Date()));
    }
}
