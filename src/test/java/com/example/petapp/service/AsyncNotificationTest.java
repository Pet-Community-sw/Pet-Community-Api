package com.example.petapp.service;

import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.out.cache.AppOnlineCachePort;
import com.example.petapp.infrastructure.event.notification.NotificationAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AsyncNotificationTest {

    @SpyBean
    private NotificationAdapter notificationAdapter;
    
    @MockBean
    private AppOnlineCachePort appOnlineCachePort;

    @Test
    @DisplayName("재시도가 모두 실패하면 recover 메서드가 호출되어야 한다.")
    void recoverTest() throws InterruptedException {
        //given
        NotificationEvent event = new NotificationEvent(1L, "테스트");

        when(appOnlineCachePort.exist(any())).thenThrow(new RuntimeException("redis 에러"));

        //when
        notificationAdapter.handle(event);

        Thread.sleep(15000);

        //then
        // handle 메서드가 4번 호출되고, recover 메서드가 1번 호출되는지 검증
        verify(notificationAdapter, times(4)).handle(event);
        verify(notificationAdapter, times(1)).recover(any(RuntimeException.class), eq(event));
    }
}
