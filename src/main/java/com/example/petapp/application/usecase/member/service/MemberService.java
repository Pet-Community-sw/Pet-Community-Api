package com.example.petapp.application.usecase.member.service;

import com.example.petapp.application.common.NameChosungUtil;
import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.application.out.cache.MemberRecentViewCachePort;
import com.example.petapp.application.usecase.member.MemberQueryUseCase;
import com.example.petapp.application.usecase.member.MemberUseCase;
import com.example.petapp.application.usecase.member.mapper.MemberMapper;
import com.example.petapp.application.usecase.member.object.MemberEvent;
import com.example.petapp.application.usecase.member.object.MethodType;
import com.example.petapp.application.usecase.member.object.dto.request.MemberSignDto;
import com.example.petapp.application.usecase.member.object.dto.request.ResetPasswordDto;
import com.example.petapp.application.usecase.member.object.dto.request.UpdateMemberRequestDto;
import com.example.petapp.application.usecase.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.usecase.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.usecase.member.object.dto.response.MemberSignResponseDto;
import com.example.petapp.application.usecase.token.TokenUseCase;
import com.example.petapp.domain.file.FileKind;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberQueryUseCase memberQueryUseCase;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUseCase tokenUseCase;
    private final StoragePort storagePort;
    private final MemberRecentViewCachePort memberRecentViewCachePort;

    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    @Override
    public MemberSignResponseDto create(MemberSignDto memberSignDto) {
        if (memberRepository.exist(memberSignDto.getEmail())) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "이미 가입된 회원입니다.");
        }
        String imageFileName = storagePort.uploadFile(memberSignDto.getMemberImageUrl(), FileKind.MEMBER);
        Member member = MemberMapper.toEntity(memberSignDto, passwordEncoder.encode(memberSignDto.getPassword()), imageFileName);
        Member savedMember = memberRepository.save(member);

        //outbox 이벤트 발행
        eventPublisher.publishEvent(MemberEvent.builder()
                .methodType(MethodType.CREATE)
                .memberId(savedMember.getId())
                .memberName(savedMember.getName())
                .memberNameChosung(NameChosungUtil.getChosung(memberSignDto.getName()))
                .memberImageUrl(savedMember.getMemberImageUrl())
                .build()
        );

        return new MemberSignResponseDto(savedMember.getId());
    }

    @Override
    public FindByIdResponseDto findById(String phoneNumber) {
        Member member = memberQueryUseCase.findOrThrowByPhoneNumber(phoneNumber);
        return new FindByIdResponseDto(member.getEmail());
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        if (member.isSamePassword(passwordEncoder, resetPasswordDto.getNewPassword())) {
            throw new IllegalArgumentException("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.updatePassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        }
    }

    @Override
    public GetMemberResponseDto get(Long targetId, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(targetId);

        memberRecentViewCachePort.createRecentView(memberId, targetId);

        return MemberMapper.toGetMemberResponseDto(member);
    }

    @Override
    @Transactional
    public void update(UpdateMemberRequestDto requestDto, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        String imageFileName = storagePort.uploadFile(requestDto.getMemberImageUrl(), FileKind.MEMBER);

        member.updateInfo(requestDto.getName(), imageFileName);
        String chosung = NameChosungUtil.getChosung(requestDto.getName());

        eventPublisher.publishEvent(MemberEvent.builder()
                .methodType(MethodType.UPDATE)
                .memberId(memberId)
                .memberName(requestDto.getName())
                .memberNameChosung(chosung)
                .memberImageUrl(imageFileName)
                .build()
        );
    }

    @Transactional
    @Override
    public void delete(Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        tokenUseCase.delete(memberId);
        memberRepository.delete(member);

        eventPublisher.publishEvent(MemberEvent.builder()
                .methodType(MethodType.DELETE)
                .memberId(memberId)
                .build()
        );
    }
}
