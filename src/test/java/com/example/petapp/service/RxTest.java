package com.example.petapp.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

public class RxTest {

    @Test
    public void test() throws InterruptedException {
        String[] args = {"A", "B", "C", "D", "E"};
        Observable.fromArray(args)
                .doOnNext(data -> System.out.println("Received: " + data))
                .map(String::toLowerCase)
                .subscribeOn(Schedulers.newThread())
                .subscribe(data -> System.out.println("Thread : " + Thread.currentThread().getName() + "Processed: " + data));

        Observable.fromArray(args)
                .doOnNext(data -> System.out.println("Received2: " + data))
                .map(String::toLowerCase)
                .subscribeOn(Schedulers.newThread())
                .subscribe(data -> System.out.println("Thread : " + Thread.currentThread().getName() + "Processed2: " + data));
    }
}


