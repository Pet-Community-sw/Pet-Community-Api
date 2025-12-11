package com.example.petapp.infrastructure.database.redis;

public final class RedisKeys {


    // Notifications
    public static String notifications(long memberId) {
        return "notification:" + memberId;
    }

    // Walk path
    public static String walkPath(long walkId) {
        return "walk:path:" + walkId;
    }

    // Online users in chat room
    public static String onlineUsers(long chatRoomId) {
        return "chatRoomId:" + chatRoomId + ":onlineUsers";
    }

    // Foreground members
    public static String foregroundMembers() {
        return "foreGroundMembers";
    }

    // Read hash per chat room
    public static String readHash(long chatRoomId) {
        return "chatRoomId:" + chatRoomId + ":read";
    }

    // Last message info per chat room
    public static String lastMessageInfo(long chatRoomId) {
        return "chat:lastMessageInfo:" + chatRoomId;
    }

    // Room Seq
    public static String seqByRoomId(Long roomId) {
        return "chatRoom:" + roomId + ":seq";
    }
}
