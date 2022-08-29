package ru.maksirep.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksirep.chatbot.entity.ChatBot;

@Repository
public interface ChatBotRepository extends JpaRepository<ChatBot, Long> {
    ChatBot getByChatId (String chatId);
}
