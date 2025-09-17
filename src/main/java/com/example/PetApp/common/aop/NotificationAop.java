package com.example.PetApp.common.aop;

import com.example.PetApp.common.annotation.Notification;
import com.example.PetApp.common.util.notification.SendNotificationUtil;
import com.example.PetApp.domain.member.model.entity.Member;
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

    @AfterReturning(value = "@annotation(notification)",returning = "ret")
    public void sendNotification(JoinPoint joinPoint, Notification notification, Object ret) {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("ret", ret);

        Member ownerMember = parser.parseExpression(notification.recipient()).getValue(ctx, Member.class);
        String message = parser.parseExpression(notification.message()).getValue(ctx, String.class);

        sendNotificationUtil.sendNotification(ownerMember, message);
    }
}
