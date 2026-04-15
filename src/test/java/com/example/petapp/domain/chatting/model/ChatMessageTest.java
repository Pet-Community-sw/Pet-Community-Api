package com.example.petapp.domain.chatting.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    void updateSeq를_호출하면_seq가_변경된다() {
        ChatMessage chatMessage = ChatMessage.builder()
                .seq(1L)
                .build();

        chatMessage.updateSeq(10L);

        assertThat(chatMessage.getSeq()).isEqualTo(10L);
    }
}
