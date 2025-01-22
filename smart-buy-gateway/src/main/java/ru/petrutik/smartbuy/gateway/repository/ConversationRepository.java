package ru.petrutik.smartbuy.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.petrutik.smartbuy.gateway.model.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
