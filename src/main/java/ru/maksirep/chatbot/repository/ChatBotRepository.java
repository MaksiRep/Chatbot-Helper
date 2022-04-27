package ru.maksirep.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksirep.chatbot.entity.ChatBotEntity;

@Repository
public interface ChatBotRepository extends JpaRepository<ChatBotEntity, Long> {

    boolean existsByChatId (String chatId);

    ChatBotEntity getByChatId (String chatId);

}
