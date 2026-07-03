package com.example.petapp.infrastructure.database.inmemory;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class ChatOnlineStoreConcurrencyTest {

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void hashMap은_동시에_같은_채팅방에_사용자를_추가하면_값이_유실될_수_있다() throws Exception {
        HashMapChatOnlineStore store = new HashMapChatOnlineStore();

        runConcurrently(
                () -> store.addOnlineUser(1L, 100L),
                () -> store.addOnlineUser(1L, 200L)
        );

        assertThat(store.getOnlineUserIds(1L)).hasSize(1);
    }

    @Test
    void concurrentHashMap은_동시에_같은_채팅방에_사용자를_추가해도_값이_유실되지_않는다() throws Exception {
        ConcurrentHashMapChatOnlineStore store = new ConcurrentHashMapChatOnlineStore();

        runConcurrently(
                () -> store.addOnlineUser(1L, 100L),
                () -> store.addOnlineUser(1L, 200L)
        );

        assertThat(store.getOnlineUserIds(1L)).hasSize(2);
    }

    private void runConcurrently(Runnable firstTask, Runnable secondTask) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Future<?> first = executorService.submit(() -> {
            ready.countDown();
            await(start);
            firstTask.run();
        });

        Future<?> second = executorService.submit(() -> {
            ready.countDown();
            await(start);
            secondTask.run();
        });

        await(ready);
        start.countDown();

        first.get();
        second.get();

        executorService.shutdown();
    }

    private static class HashMapChatOnlineStore {

        private final Map<Long, Set<Long>> onlineMap = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(2);

        void addOnlineUser(Long chatRoomId, Long memberId) {
            Set<Long> onlineUsers = onlineMap.get(chatRoomId);

            if (onlineUsers == null) {
                latch.countDown();
                await(latch);

                onlineUsers = new HashSet<>();
                onlineUsers.add(memberId);
                onlineMap.put(chatRoomId, onlineUsers);
                return;
            }

            onlineUsers.add(memberId);
        }

        Set<Long> getOnlineUserIds(Long chatRoomId) {
            return onlineMap.getOrDefault(chatRoomId, Set.of());
        }
    }

    private static class ConcurrentHashMapChatOnlineStore {

        private final ConcurrentHashMap<Long, Set<Long>> onlineMap = new ConcurrentHashMap<>();

        void addOnlineUser(Long chatRoomId, Long memberId) {

            onlineMap.compute(chatRoomId, (key, onlineUsers) -> {
                if (onlineUsers == null) onlineUsers = ConcurrentHashMap.newKeySet();
                onlineUsers.add(memberId);
                return onlineUsers;
            });
        }

        Set<Long> getOnlineUserIds(Long chatRoomId) {
            return onlineMap.getOrDefault(chatRoomId, Set.of());
        }
    }
}
