package com.example.petapp.common.aop;

import com.example.petapp.common.aop.annotation.Notification;
import com.example.petapp.common.base.util.notification.SendNotificationUtil;
import com.example.petapp.domain.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationAop {

    private final SendNotificationUtil sendNotificationUtil;
    private final ExpressionParser parser = new SpelExpressionParser();

    @AfterReturning(value = "@annotation(notification)", returning = "ret")
    public void sendNotification(JoinPoint joinPoint, Notification notification, Object ret) {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("ret", ret);

        Member ownerMember = parser.parseExpression(notification.recipient()).getValue(ctx, Member.class);
        String message = parser.parseExpression(notification.message()).getValue(ctx, String.class);
        if (ownerMember == null || message == null) {
            throw new RuntimeException();
        }

        log.info("[NOTIFICATION] Method : {}", joinPoint.getSignature().toShortString());
        sendNotificationUtil.sendNotification(ownerMember, message);
    }
}
