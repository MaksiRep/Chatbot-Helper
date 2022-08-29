package ru.maksirep.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksirep.chatbot.entity.ChatBotLog;

@Repository
public interface ChatBotLogRepository extends JpaRepository<ChatBotLog, Long> {
}
