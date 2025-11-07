package com.example.petapp.domain.chatting.model.type;

public enum CommandType {
    ENTER, TALK, LEAVE, READ,//클라이언트
    CHAT_UPDATE, LIST_UPDATE,//서버 응답
    NOTIFICATION// 알림
}
