package ru.petrutik.smartbuy.gateway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    private Long chatId;
    private int requestAdded;
    @Enumerated(EnumType.STRING)
    private ConversationStatus status;
    private String clientInput;

    public Conversation() {
    }

    public Conversation(Long chatId, int requestAdded, ConversationStatus status, String clientInput) {
        this.chatId = chatId;
        this.requestAdded = requestAdded;
        this.status = status;
        this.clientInput = clientInput;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public int getRequestAdded() {
        return requestAdded;
    }

    public void setRequestAdded(int requestAdded) {
        this.requestAdded = requestAdded;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public String getClientInput() {
        return clientInput;
    }

    public void setClientInput(String clientInput) {
        this.clientInput = clientInput;
    }
}
