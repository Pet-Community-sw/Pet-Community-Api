package com.example.petapp.service;

public class Code {

    public String solution(String s) {
        int num = s.length() / 2;
        return s.length() % 2 == 0 ? s.substring(num - 1, num + 1) : s.charAt(num) + "";
    }


    public int[] solution2(int n) {
        return new StringBuilder().append(n).reverse().chars().map(Character::getNumericValue).toArray();
    }

    public long solution(int a, int b) {
        return ((long) (a + b) * (Math.abs(a - b) + 1)) / 2;
    }


    public int solution(int[] absolutes, boolean[] signs) {
        int answer = 0;

        for (int i = 0; i < absolutes.length; i++) {
            answer += signs[i] ? absolutes[i] : -absolutes[i];
        }
        return answer;
    }


}
