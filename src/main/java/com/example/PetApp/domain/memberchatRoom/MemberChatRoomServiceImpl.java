package com.example.PetApp.domain.memberchatRoom;

import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.chatting.ChattingReader;
import com.example.PetApp.domain.chatting.model.type.ChatRoomType;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.mapper.MemberChatRoomMapper;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.CreateMemberChatRoomResponseDto;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.MemberChatRoomsResponseDto;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor//리펙토링 필수
@Slf4j
public class MemberChatRoomServiceImpl implements MemberChatRoomService {

    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChattingReader chattingReader;
    private final StringRedisTemplate redisTemplate;
    private final QueryService queryService;

    @Transactional(readOnly = true)
    @Override
    public List<MemberChatRoomsResponseDto> getMemberChatRooms(String email) {
        Member member = queryService.findByMember(email);
        List<MemberChatRoom> memberChatRooms = memberChatRoomRepository.findAllByMembersContains(member);

        return getMemberChatRoomsResponseDtos(memberChatRooms, member);
    }

    @Transactional
    @Override
    public CreateMemberChatRoomResponseDto createMemberChatRoom(Member fromMember, Member member) {//방 제목을 어떻게 할까 대리 산책자 구인했을 때 채팅방
        MemberChatRoom memberChatRoom = getMemberChatRoom(fromMember, member);
        memberChatRoomRepository.save(memberChatRoom);
        return new CreateMemberChatRoomResponseDto(memberChatRoom.getId(), null);
    }

    @Transactional
    @Override
    public CreateMemberChatRoomResponseDto createMemberChatRoom(Long memberId, String email) {
        Member fromMember = queryService.findByMember(memberId);
        Member member = queryService.findByMember(email);
        MemberChatRoom memberChatRoom = getMemberChatRoom(fromMember, member);
        MemberChatRoom newMemberChatRoom = memberChatRoomRepository.save(memberChatRoom);
        return new CreateMemberChatRoomResponseDto(newMemberChatRoom.getId(), null);
    }

    private static MemberChatRoom getMemberChatRoom(Member fromMember, Member member) {
        List<Member> members = new ArrayList<>();
        members.add(fromMember);
        members.add(member);
        return MemberChatRoom.builder()
                .members(members)
                .build();
    }

    @Transactional
    @Override
    public void updateMemberChatRoom(Long memberChatRoomId, String userChatRoomName, String email) {
        //여기에 무엇을
    }

    @Transactional
    @Override
    public void deleteMemberChatRoom(Long memberChatRoomId, String email) {
        Member member = queryService.findByMember(email);
        MemberChatRoom memberChatRoom = queryService.findByMemberChatRoom(memberChatRoomId);
        if (!(memberChatRoom.getMembers().contains(member))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        memberChatRoomRepository.deleteById(memberChatRoomId);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getMessages(Long memberChatRoomId, String email, int page) {
        Member member = queryService.findByMember(email);
        return chattingReader.getMessages(memberChatRoomId, member.getId(), ChatRoomType.ONE, page);
    }

    private static Member filterMember(List<Member> members, Member member) {
        Member returnMember = null;
        for (Member member1 : members) {
            if (!(member1.equals(member))) {
                returnMember = member1;
            }
        }
        return returnMember;
    }

    @NotNull
    private List<MemberChatRoomsResponseDto> getMemberChatRoomsResponseDtos(List<MemberChatRoom> memberChatRooms, Member member) {
        return memberChatRooms.stream()
                .map(memberChatRoom -> {
                    Member anotherMember = filterMember(memberChatRoom.getMembers(), member);
                    String roomName = anotherMember.getName();
                    String roomImageUrl = anotherMember.getMemberImageUrl();
                    String lastMessage = redisTemplate.opsForValue().get("memberChat:lastMessage" + memberChatRoom.getId());
                    String count = redisTemplate.opsForValue().get("unReadMemberChat:" + memberChatRoom.getId() + ":" + member.getId());
                    String lastMessageTime = redisTemplate.opsForValue().get("memberChat:lastMessageTime:" + memberChatRoom.getId());
                    return MemberChatRoomMapper.toMemberChatRoomsResponseDto(roomName, roomImageUrl, lastMessage, count, lastMessageTime);
                }).collect(Collectors.toList());
    }
}
