package com.example.petapp.application.in.chatting.model.type;

public enum CommandType {
    ENTER, TALK, LEAVE, READ, ACK,//클라이언트
    CHAT_UPDATE, LIST_UPDATE,//서버 응답
    NOTIFICATION,// 알림
    LOCATION // 위치
}
