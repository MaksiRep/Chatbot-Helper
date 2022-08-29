package ru.maksirep.chatbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "ChatTtsButtons")
public class ChatTtsButtons {

    public ChatTtsButtons() {}

    public ChatTtsButtons(String name, ChatBot chatBot) {
        this.name = name;
        this.chatBot = chatBot;
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private ChatBot chatBot;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatBot getChatBotEntity() {
        return chatBot;
    }

    public void setChatBotEntity(ChatBot chatBot) {
        this.chatBot = chatBot;
    }
}
