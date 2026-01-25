package com.example.petapp.application.common;

public class NameChosungUtil {
    private static final char CHOSUNG_START = 0xAC00;
    private static final char[] CHOSUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    public static String getChosung(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= CHOSUNG_START && c <= 0xD7A3) {
                // 한글인 경우 초성 추출
                int index = (c - CHOSUNG_START) / 588;
                sb.append(CHOSUNG[index]);
            } else {
                // 한글이 아닌 경우 그대로 추가
                sb.append(c);
            }
        }
        return sb.toString().replaceAll("\\s+", "").toLowerCase();
    }
}
