package com.example.bookrent2.websocket.model.chat;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    @Enumerated(EnumType.STRING)
    @Getter
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;

}
