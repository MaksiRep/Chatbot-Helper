package ru.maksirep.chatbot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ChatBot", indexes = @Index(columnList = "chatId"))
public class ChatBotEntity {

    public ChatBotEntity () {}

    public ChatBotEntity(String chatId, boolean isVisuallyImpaired, boolean isHearingImpaired) {
        this.chatId = chatId;
        this.isVisuallyImpaired = isVisuallyImpaired;
        this.isHearingImpaired = isHearingImpaired;
    }

    @Id
    @Column(name = "chatId" )
    private String chatId;

    @Column(name = "isVisuallyImpaired" )
    private boolean isVisuallyImpaired;

    @Column(name = "isHearingImpaired" )
    private boolean isHearingImpaired;

    @Column(name = "isTutorialEnd")
    private boolean isTutorialEnd;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ChatBotLogEntity lastDate;

    public boolean isTutorialEnd() {
        return isTutorialEnd;
    }

    public void setTutorialEnd(boolean tutorialEnd) {
        isTutorialEnd = tutorialEnd;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isVisuallyImpaired() {
        return isVisuallyImpaired;
    }

    public void setVisuallyImpaired(boolean visuallyImpaired) {
        isVisuallyImpaired = visuallyImpaired;
    }

    public boolean isHearingImpaired() {
        return isHearingImpaired;
    }

    public void setHearingImpaired(boolean hearingImpaired) {
        isHearingImpaired = hearingImpaired;
    }

}
