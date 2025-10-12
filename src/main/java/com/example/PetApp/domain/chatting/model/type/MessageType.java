package com.example.PetApp.domain.chatting.model.type;

public enum MessageType {
    ENTER, TALK, LEAVE, READ,//클라이언트
    CHAT_UPDATE, LIST_UPDATE//서버 응답
}
