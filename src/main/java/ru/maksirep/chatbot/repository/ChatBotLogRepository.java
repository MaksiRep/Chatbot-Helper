package ru.maksirep.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksirep.chatbot.entity.ChatBotLogEntity;

@Repository
public interface ChatBotLogRepository extends JpaRepository<ChatBotLogEntity, Long> {
}
