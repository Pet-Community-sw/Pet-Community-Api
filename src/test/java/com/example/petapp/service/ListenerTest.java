package com.example.petapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

@SpringBootTest
public class ListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    public void test() {

        Long id = 123L;
        publisher.publishEvent(id);

    }

    @EventListener
    public void listener1(Long id) {
        System.out.println("Listner1 : " + id);

        String string = "second";
//        publisher.publishEvent(string);
    }

//    @EventListener/*
//    public void listener2(String s) {
//        System.out.println("Listener2 : " + s);
//    }*/
}
