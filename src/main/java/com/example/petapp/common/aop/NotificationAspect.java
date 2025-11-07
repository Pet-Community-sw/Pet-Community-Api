package com.example.petapp.common.aop;

import com.example.petapp.common.aop.annotation.Notification;
import com.example.petapp.common.base.util.notification.SendNotificationUtil;
import com.example.petapp.domain.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationAspect {
    private final SendNotificationUtil notificationUtil; // send(Member, String)

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ExpressionParser parser = new SpelExpressionParser();

    private final ApplicationContext applicationContext;

    @AfterReturning(value = "@annotation(notification)", returning = "result")
    public void sendNotification(JoinPoint joinPoint, Notification notification, Object result) {
        try {
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            ctx.setBeanResolver(new BeanFactoryResolver(applicationContext));

            ctx.setVariable("result", result);
            if (!notification.condition().isBlank()) {
                Boolean ok = parser.parseExpression(notification.condition()).getValue(ctx, Boolean.class);
                if (Boolean.FALSE.equals(ok)) {
                    log.info("[Notification] 알림전송 x ");
                    return;
                }
            }

            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                ctx.setVariable("p" + i, args[i]);
            }

            Member member = parser.parseExpression(notification.recipient()).getValue(ctx, Member.class);
            if (member == null) {
                log.warn("[Notification] 받는사람(Member)가 없습니다.");
                return;
            }

            String message = parser.parseExpression(notification.message()).getValue(ctx, String.class);
            if (message == null) {
                log.warn("[Notification] 메세지가 없습니다.");
                return;
            }
            notificationUtil.sendNotification(member, message);
        } catch (Exception e) {
            log.error("[Notification] 실패", e);
        }
    }
}