//package com.example.petapp.domain.notification.manager;
//
//import com.example.petapp.application.in.member.MemberQueryUseCase;
//import com.example.petapp.common.jwt.util.JwtTokenizer;
//import com.example.petapp.domain.member.model.Member;
//import com.example.petapp.port.InMemoryService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class SseEmitterManager {
//
//    private final static Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;
//    private final MemberQueryUseCase memberQueryUseCase;
//    private final JwtTokenizer jwtTokenizer;
//    private final InMemoryService inMemoryService;
//    private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();//스레드 중복 방지
//
//    @Transactional(readOnly = true)
//    public SseEmitter subscribe(String token) {
//        String email = jwtTokenizer.parseAccessToken(token).getSubject();
//        Member member = memberQueryUseCase.findOrThrow(email);
//
//        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
//
//        sseEmitterMap.put(member.getId(), sseEmitter);
//
//        sseEmitter.onCompletion(() -> sseEmitterMap.remove(member.getId()));
//
//        sseEmitter.onTimeout(() -> {
//            sseEmitterMap.remove(member.getId());
//            log.info("[SSE] timeout memberId:{}", member.getId());
//        });
//
//        sseEmitter.onError(e -> {
//            sseEmitterMap.remove(member.getId());
//            log.error("[SSE] 오류 memberId:{}", member.getId(), e);
//        });
//
//        try {
//            sseEmitter.send(SseEmitter.event().name("connect").data("connected"));//503에러를 막고자 더미코드를 보냄.
//        } catch (IOException e) {
//            sseEmitterMap.remove(member.getId());
//            log.error("[SSE] 더미 코드 보내는 중 오류 발생 ", e);
//        }
//
//        inMemoryService.createForeGroundData(member.getId());
//        return sseEmitter;
//    }
//
//    public void sendNotification(Long memberId, String message) {
//        SseEmitter sseEmitter = sseEmitterMap.get(memberId);
//        if (sseEmitter != null) {
//            try {
//                log.info("알림 전송.");
//                sseEmitter.send(SseEmitter.event().name("notification").data(message));
//
//            } catch (IOException e) {
//                sseEmitterMap.remove(memberId);
//                inMemoryService.deleteForeGroundData(memberId);//todo : sse 연결 관리(하트비트)
//                log.error("[SSE] 오류 notification memberId : {}", memberId, e);
//            }
//        }
//    }
//}
