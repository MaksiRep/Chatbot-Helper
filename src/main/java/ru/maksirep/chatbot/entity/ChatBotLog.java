package ru.maksirep.chatbot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ChatBotLogEntity")
public class ChatBotLog {

    public ChatBotLog() {}

    public ChatBotLog(ChatBot chatBot, Date lastDate) {
        this.chatBot = chatBot;
        this.lastDate = lastDate;
    }

    @Id
    @GeneratedValue
    @Column(name = "id" )
    private String id;

    @Column(name = "date" )
    private Date lastDate;

    @OneToOne(cascade = CascadeType.ALL)
    private ChatBot chatBot;

    public String getId() {
        return id;
    }

    public void setId(String chatId) {
        this.id = chatId;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public ChatBot getChatBotEntity() {
        return chatBot;
    }

    public void setChatBotEntity(ChatBot chatBot) {
        this.chatBot = chatBot;
    }
}
