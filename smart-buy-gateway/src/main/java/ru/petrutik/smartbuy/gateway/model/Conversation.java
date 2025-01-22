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
    private short requestAdded;
    @Enumerated(EnumType.STRING)
    private ConversationStatus status;
    private String clientInput;
}
