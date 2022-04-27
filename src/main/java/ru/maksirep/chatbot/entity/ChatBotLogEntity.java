package ru.maksirep.chatbot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ChatBotLogEntity")
public class ChatBotLogEntity {

    public ChatBotLogEntity () {}

    public ChatBotLogEntity(String chatId, Date lastDate) {
        this.chatId = chatId;
        this.lastDate = lastDate;
    }

    @Id
    @Column(name = "chatId" )
    private String chatId;

    @Column(name = "date" )
    private Date lastDate;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }
}
