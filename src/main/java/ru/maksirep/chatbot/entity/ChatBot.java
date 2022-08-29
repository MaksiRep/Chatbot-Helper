package ru.maksirep.chatbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "ChatBotUser", indexes = @Index(columnList = "chatId"))
public class ChatBot {

    public ChatBot() {}

    public ChatBot(String chatId, boolean isVisuallyImpaired, boolean isHearingImpaired, int menuPos) {
        this.chatId = chatId;
        this.isVisuallyImpaired = isVisuallyImpaired;
        this.isHearingImpaired = isHearingImpaired;
        this.menuPos = menuPos;
    }

    @Id
    @Column(name = "chatId" )
    private String chatId;

    @Column(name = "isVisuallyImpaired" )
    private boolean isVisuallyImpaired;

    @Column(name = "isHearingImpaired" )
    private boolean isHearingImpaired;

    @Column(name = "tutorSteps")
    private int tutorSteps;

    @Column(name = "menuPos")
    private int menuPos;

    public int getTutorSteps() {
        return tutorSteps;
    }

    public void setTutorSteps(int tutorialEnd) {
        tutorSteps = tutorialEnd;
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

    public int getMenuPos() {
        return menuPos;
    }

    public void setMenuPos(int menuPos) {
        this.menuPos = menuPos;
    }
}
