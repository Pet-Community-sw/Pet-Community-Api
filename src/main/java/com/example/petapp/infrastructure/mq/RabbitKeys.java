package com.example.petapp.infrastructure.mq;

public class RabbitKeys {
    public static final String MAIN_EXCHANGE = "ex.main";
    public static final String RETRY_EXCHANGE = "ex.retry";
    public static final String DLX_EXCHANGE = "ex.dlx";

    public static final String MAIL_QUEUE = "q.mail";
    public static final String NOTIFICATION_QUEUE = "q.notification";
    public static final String MEMBER_QUEUE = "q.member";

    public static final String RETRY_5S_QUEUE = "q.retry.5s";
    public static final String RETRY_30S_QUEUE = "q.retry.30s";
    public static final String DEAD_LETTER_QUEUE = "q.dead.letter";

    public static final String MAIL_ROUTING_KEY = "mail.key";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";
    public static final String MEMBER_ROUTING_KEY = "member.key";
    public static final String RETRY_5S_ROUTING_KEY = "retry.5s.key";
    public static final String RETRY_30S_ROUTING_KEY = "retry.30s.key";
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter.key";
}
