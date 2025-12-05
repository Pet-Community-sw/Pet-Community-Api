package com.example.petapp.service;


import com.example.petapp.application.in.member.dto.request.LoginDto;
import com.example.petapp.application.in.member.dto.request.MemberSignDto;
import com.example.petapp.application.in.member.dto.request.ResetPasswordDto;
import com.example.petapp.application.in.member.dto.request.SendEmailDto;
import com.example.petapp.application.in.member.dto.response.MemberSignResponseDto;
import com.example.petapp.application.service.member.MemberService;
import com.example.petapp.common.aop.LogAspect;
import com.example.petapp.common.base.util.imagefile.FileImageKind;
import com.example.petapp.common.base.util.imagefile.FileUploadUtil;
import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.email.EmailService;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.RoleRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.token.TokenService;
import com.example.petapp.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(LogAspect.class)
@ExtendWith(MockitoExtension.class)
class MemberUseCaseTest {

    Member member = Mapper.createFakeMember();
    @InjectMocks
    private MemberService memberServiceImp;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("signup_성공")
    void test1() {
        //given
        MemberSignDto memberSignDto = MemberSignDto.builder()
                .name("최선재")
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();

        when(memberRepository.exist(memberSignDto.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(Member.builder().memberId(100L).build());
//        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
//            Member fakeMember = invocation.getArgument(0);
//            return fakeMember.toBuilder().memberId(100L).build();
//        });

        //when
        MemberSignResponseDto member = memberServiceImp.createMember(memberSignDto);

        //then
        assertThat(member).isNotNull();
        assertThat(member.getMemberId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("signup_비밀번호 형식_실패")
    void test2() {
        //given

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        MemberSignDto memberSignDto = MemberSignDto.builder()
                .name("최선재")
                .email("chltjswo890@naver.com")
                .password("fpdlswj365")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
        //when
        Set<ConstraintViolation<MemberSignDto>> validate = validator.validate(memberSignDto);

        //then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
    }

    @Test
    @DisplayName("사진 저장_이미지가 null이면 기본경로를 반환")
    void test3() {
        // given

        // when
        String result = FileUploadUtil.fileUpload(null, "/uploads", FileImageKind.MEMBER);

        // then
        assertThat(result).isEqualTo("/image/basic/Profile_avatar_placeholder_large.png");
    }

//    @Test
//    @DisplayName("login_성공")
//    void test4() {
//        // given
//        LoginDto loginDto = LoginDto.builder()
//                .email("chltjswo789@naver.com")
//                .password("1234")
//                .build();
//
//        Role role = Role.builder()
//                .roleId(1L)
//                .name("ROLE_USER")
//                .build();
//
//        Member member = Mapper.createFakeMember();
//        MockHttpServletResponse response = new MockHttpServletResponse(); // ❗ 추가
//
//        when(memberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(member));
//        when(passwordEncoder.matches("1234", member.getPassword())).thenReturn(true);
//        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
//
//        // tokenService.save()은 mock 제거하여 실제 메서드 실행되도록 해야 cookie 추가됨
//        // when(tokenService.save(member, response)).thenReturn(loginResponseDto); ← ❌ 주석처리
//
//        // when
//        LoginResponseDto result = memberServiceImp.login(loginDto, response);
//
//        // then
//        assertThat(result.getName()).isEqualTo(member.getName());
//        assertThat(result.getAccessToken()).isNotNull();
//
//        Cookie refreshCookie = response.getCookie("refreshToken");
//        assertThat(refreshCookie).isNotNull(); // ✅ 쿠키가 존재해야 함
//        assertThat(refreshCookie.isHttpOnly()).isTrue();
//        assertThat(refreshCookie.getSecure()).isTrue();
//        assertThat(refreshCookie.getPath()).isEqualTo("/");
//    }
//    @Test
//    @DisplayName("login_성공")
//    void test4() {
//        //given
//        LoginDto loginDto = LoginDto.builder()
//                .email("chltjswo789@naver.com")
//                .password("1234")
//                .build();
//
//        Role role = Role.builder()
//                .roleId(1L)
//                .name("ROLE_USER")
//                .build();
//
//        Member member = Mapper.createFakeMember();
//
//        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
//                .name("최선재")
//                .accessToken("accessToken")
//                .build();
//
//        when(memberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(member));
//        when(passwordEncoder.matches("1234", "fpdlswj365!")).thenReturn(true);
//        when(tokenService.save(member, response)).thenReturn(loginResponseDto);
//        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
//        //when
//        LoginResponseDto result = memberServiceImp.login(loginDto, response);
//        //then
//        assertThat(result.getName()).isEqualTo(member.getName());
//    }


    @Test
    @DisplayName("login_실패")
    void test5() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .build();

        MockHttpServletResponse response = new MockHttpServletResponse();


        when(memberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());
        //when&then
        assertThatThrownBy(() -> memberServiceImp.login(loginDto, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("이메일 혹은 비밀번호가 일치하지 않습니다.");

    }

    @Test
    @DisplayName("sendEmail_성공")
    void test6() {
        //given
        SendEmailDto sendEmailDto = new SendEmailDto("chltjswo789@naver.com");
        Member member = Mapper.createFakeMember();

        when(memberRepository.findByEmail(sendEmailDto.getEmail())).thenReturn(Optional.of(member));

        //when
        memberServiceImp.sendEmail(sendEmailDto);

        //then
        verify(emailService).sendMail(sendEmailDto.getEmail());
    }

    @Test
    @DisplayName("resetPassword_성공")
    void test7() {
        //given
        String email = "chltjswo789@naver.com";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("fpdlswj365!!");
        Member member = Mapper.createFakeMember();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(resetPasswordDto.getNewPassword(), member.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(resetPasswordDto.getNewPassword())).thenReturn(resetPasswordDto.getNewPassword());

        //when
        memberServiceImp.resetPassword(resetPasswordDto, email);

        //then
        assertThat(member.getPassword()).isEqualTo(resetPasswordDto.getNewPassword());
    }

    @Test
    @DisplayName("resetPassword_실패")
    void test8() {
        //given
        String email = "chltjswo789@naver.com";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("fpdlswj365!");
        Member member = Mapper.createFakeMember();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(resetPasswordDto.getNewPassword(), member.getPassword())).thenReturn(true);

        //when & then
        assertThatThrownBy(() -> memberServiceImp.resetPassword(resetPasswordDto, email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("전 비밀번호와 다르게 설정해야합니다.");
    }


}