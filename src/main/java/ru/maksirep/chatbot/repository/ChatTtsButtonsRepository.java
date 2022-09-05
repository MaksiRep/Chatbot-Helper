package ru.maksirep.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksirep.chatbot.entity.ChatBot;
import ru.maksirep.chatbot.entity.ChatTtsButtons;

import java.util.ArrayList;

@Repository
public interface ChatTtsButtonsRepository extends JpaRepository<ChatTtsButtons, Long> {

    public ArrayList<ChatTtsButtons> findAllByChatBot(ChatBot chatBot);

    public ChatTtsButtons findByNameAndChatBot (String name, ChatBot chatBot);
}