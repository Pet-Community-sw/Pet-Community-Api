package com.example.PetApp.domain.chatting;

import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import org.springframework.stereotype.Service;

import static com.example.PetApp.domain.chatting.model.entity.ChatMessage.*;

@Service
public interface ChattingReader  {

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, ChatRoomType chatRoomType, int page);

}
