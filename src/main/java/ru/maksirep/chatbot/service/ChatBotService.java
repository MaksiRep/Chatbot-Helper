package ru.maksirep.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatTtsButtons;
import ru.maksirep.chatbot.repository.ChatBotRepository;

import java.util.ArrayList;

@Service
public class ChatBotService {

    @Autowired
    private ChatBotRepository chatBotRepository;

    @Autowired
    private ChatBotLogService chatBotLogService;

    @Autowired
    private ChatTtsButtonsService chatTtsButtonsService;

    public ChatBot getChatBotEntity (String chatId) {
        return chatBotRepository.getByChatId(chatId);
    }

    public void saveChatBotEntity (String chatId) {
        ChatBot chatBot = new ChatBot();
        chatBot.setChatId(chatId);
        chatBot.setTutorSteps(1);
        chatBot.setMenuPos(0);
        chatBotRepository.save(chatBot);
    }

    public void updateChatBotEntity (ChatBot updateChatBot, String chatId) {
        updateChatBot.setChatId(chatId);
        chatBotRepository.saveAndFlush(updateChatBot);
    }

    public ArrayList<ChatTtsButtons> getTtsCommands (String chatId) {
        return chatTtsButtonsService.getTtsCommands(chatBotRepository.getByChatId(chatId));
    }

    public String addTtsCommands (String text, String chatId) {
        return chatTtsButtonsService.saveTtsCommands(text, chatBotRepository.getByChatId(chatId));
    }

    public String deleteTtsCommands (String text, String chatId) {
        return chatTtsButtonsService.deleteTtsCommands(text, chatBotRepository.getByChatId(chatId));
    }

    public void saveLog (String chatId) {
        chatBotLogService.saveLog(chatBotRepository.getByChatId(chatId));
    }
}
