package com.example.petapp.application.service.member;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.member.object.dto.request.ResetPasswordDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.application.out.cache.MemberAutoCompleteSearchCachePort;
import com.example.petapp.application.out.cache.MemberRecentViewCachePort;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.infrastructure.database.jpa.member.JpaMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberQueryUseCase memberQueryUseCase;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenUseCase tokenUseCase;

    @Mock
    private StoragePort storagePort;

    @Mock
    private MemberSearchPort memberSearchPort;

    @Mock
    private MemberSearchCachePort memberSearchCachePort;

    @Mock
    private MemberAutoCompleteSearchCachePort memberAutoCompleteSearchCachePort;

    @Mock
    private MemberRecentViewCachePort memberRecentViewCachePort;

    @Mock
    private JpaMemberRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 새_비밀번호가_기존과_다르면_비밀번호를_변경한다() {
        Member member = 회원을_생성한다(1L, "encoded-old-password");
        ResetPasswordDto request = ResetPasswordDto.builder()
                .newPassword("new-password")
                .build();

        when(memberQueryUseCase.findOrThrow(1L)).thenReturn(member);
        when(passwordEncoder.matches("new-password", "encoded-old-password")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        memberService.resetPassword(request, 1L);

        assertThat(member.getPassword()).isEqualTo("encoded-new-password");
    }

    @Test
    void 새_비밀번호가_기존과_같으면_예외가_발생한다() {
        Member member = 회원을_생성한다(1L, "encoded-old-password");
        ResetPasswordDto request = ResetPasswordDto.builder()
                .newPassword("same-password")
                .build();

        when(memberQueryUseCase.findOrThrow(1L)).thenReturn(member);
        when(passwordEncoder.matches("same-password", "encoded-old-password")).thenReturn(true);

        assertThatThrownBy(() -> memberService.resetPassword(request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("전 비밀번호와 다르게 설정해야합니다.");
    }

    @Test
    void 자동완성_캐시_미스면_조회후_최근조회순으로_재정렬한다() {
        List<MemberSearchResponseDto> searchResult = List.of(
                회원검색_응답을_생성한다(1L, "Alice"),
                회원검색_응답을_생성한다(2L, "Bob"),
                회원검색_응답을_생성한다(3L, "Carol")
        );

        when(memberAutoCompleteSearchCachePort.get("alice")).thenReturn(null);
        when(memberSearchPort.searchSuggestions("alice")).thenReturn(searchResult);
        when(memberRecentViewCachePort.findList(10L)).thenReturn(List.of(3L, 1L, 99L, 3L));

        List<MemberSearchResponseDto> response = memberService.searchSuggestions(" A l i c e ", 10L);

        assertThat(response).extracting(MemberSearchResponseDto::getMemberId)
                .containsExactly(3L, 1L, 2L);
        verify(memberSearchPort).searchSuggestions("alice");
        verify(memberAutoCompleteSearchCachePort).create("alice", searchResult);
    }

    @Test
    void 자동완성_캐시_히트면_검색포트를_호출하지_않고_캐시를_반환한다() {
        List<MemberSearchResponseDto> cached = List.of(
                회원검색_응답을_생성한다(1L, "Alice"),
                회원검색_응답을_생성한다(2L, "Bob")
        );

        when(memberAutoCompleteSearchCachePort.get("alice")).thenReturn(cached);
        when(memberRecentViewCachePort.findList(10L)).thenReturn(List.of());

        List<MemberSearchResponseDto> response = memberService.searchSuggestions("Alice", 10L);

        assertThat(response).isEqualTo(cached);
        verify(memberSearchPort, never()).searchSuggestions("alice");
    }

    @Test
    void 회원검색_캐시_미스면_조회후_페이지_캐시에_저장한다() {
        List<MemberSearchResponseDto> searchResult = List.of(
                회원검색_응답을_생성한다(1L, "Alice"),
                회원검색_응답을_생성한다(2L, "Bob")
        );

        when(memberSearchCachePort.get("alice", 2)).thenReturn(null);
        when(memberSearchPort.search("alice", 2)).thenReturn(searchResult);

        List<MemberSearchResponseDto> response = memberService.searchMembers(" Alice ", 2, 10L);

        assertThat(response).isEqualTo(searchResult);
        verify(memberSearchPort).search("alice", 2);
        verify(memberSearchCachePort).create("alice", 2, searchResult);
    }

    private Member 회원을_생성한다(Long id, String password) {
        return Member.builder()
                .id(id)
                .name("tester")
                .email("user@test.com")
                .password(password)
                .phoneNumber("01012345678")
                .memberImageUrl("image.png")
                .build();
    }

    private MemberSearchResponseDto 회원검색_응답을_생성한다(Long memberId, String memberName) {
        return MemberSearchResponseDto.builder()
                .memberId(memberId)
                .memberName(memberName)
                .memberImageUrl(memberName + ".png")
                .build();
    }
}
