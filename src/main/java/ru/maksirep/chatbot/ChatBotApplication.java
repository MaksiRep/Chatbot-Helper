package ru.maksirep.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.maksirep.chatbot.other.DirectoryMaker;

// TODO: Добавить кнопочки для бота - доделать
// TODO: Найти api для 2Gis + красивый вывод - платное, но мб допиздимся
@SpringBootApplication
public class ChatBotApplication {
    public static void main(String[] args) {
        new DirectoryMaker().makeDir();
        SpringApplication.run(ChatBotApplication.class, args);
    }

}
