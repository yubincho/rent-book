package com.example.bookrent2.websocket.model.chat;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    private String sender;

    @Column(length = 100)
    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime sendDate;

    public static Chat of(ChatMessage chatMessage) {
        return new Chat(chatMessage.getSender(), chatMessage.getMessage());
    }

    public Chat(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
