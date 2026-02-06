package com.example.petapp.application.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    public <T> T fromJson(String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (Exception e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
}
