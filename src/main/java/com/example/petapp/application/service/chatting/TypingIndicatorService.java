package com.example.petapp.application.service.chatting;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatting.TypingIndicatorUseCase;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.dto.TypingMessageDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.TypingCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MessageTypeStrategy에 전략을 추가하여 개발하려고 하였으나
 * 타이핑 상태는 메시지 저장이나 기타 로직이 필요 없고 단순히 상태 전송만 하면 되기에
 * 별도의 전략 패턴을 적용하지 않고 TypingIndicatorService에서 바로 구현
 * <p>
 * 타이핑은 빈도가 높은걸 생각
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TypingIndicatorService implements TypingIndicatorUseCase {

    private final SendPort sendPort;
    private final ChatRoomUseCase chatRoomUseCase;
    private final TypingCachePort typingCachePort;
    private final MemberUseCase memberUseCase;

    /**
     * 타이핑 상태 전송
     * redis로 해당 방에 타이핑 중인 유저목록 반환 ttl 3초(클라이언트는 3초이내 마다 갱신)
     * 현재 name을 전송하기 위해 db조회가 발생하나 이 후 Principal에서 name을 가져오도록 리펙토링 예정
     */
    @Override
    public void sendTypingStatus(TypingMessageDto typingMessageDto, Long id) {
        log.info("[STOMP] 타이핑 상태 전송 chatRoomId: {}, senderId: {}", typingMessageDto.roomId(), id);
        ChatRoom chatRoom = chatRoomUseCase.find(typingMessageDto.roomId());
        chatRoom.validateUser(id);
        //타이핑 중이라면 redis에 저장
        if (typingMessageDto.isTyping())
            typingCachePort.create(typingMessageDto.roomId(), id, 3 * 1000L);
            //false면 redis에서 삭제
        else
            typingCachePort.delete(typingMessageDto.roomId(), id);

        List<Long> typingUserIds = typingCachePort.getList(typingMessageDto.roomId());

        List<String> userNames = memberUseCase.findNamesOrThrowByIds(typingUserIds);

        sendPort.send("/sub/chat/typing/" + typingMessageDto.roomId(),
                SendResponseDto.builder().commandType(CommandType.TYPING).body(userNames).build());
    }

}
