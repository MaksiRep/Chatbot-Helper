package ru.maksirep.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksirep.chatbot.entity.ChatBotEntity;
import ru.maksirep.chatbot.repository.ChatBotRepository;

@Service
public class ChatBotService {

    @Autowired
    private ChatBotRepository chatBotRepository;

    @Autowired
    private ChatBotLogService chatBotLogService;

    public ChatBotEntity getChatBotEntity (String chatId) {
        return chatBotRepository.getByChatId(chatId);
    }

    public void saveChatBotEntity (String chatId) {
        ChatBotEntity chatBotEntity = new ChatBotEntity();
        chatBotEntity.setChatId(chatId);
        chatBotRepository.save(chatBotEntity);
        saveLog(chatId);
    }

    public void updateChatBotEntity (ChatBotEntity updateChatBotEntity, String chatId) {
        updateChatBotEntity.setChatId(chatId);
        chatBotRepository.saveAndFlush(updateChatBotEntity);
        saveLog(chatId);
    }

    public void saveLog (String chatId) {
        chatBotLogService.saveLog(chatId);
    }
}
