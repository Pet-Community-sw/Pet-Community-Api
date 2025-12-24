package com.example.petapp.domain.file;

import lombok.Getter;

public enum FileKind {
    POST("posts"),
    MEMBER("members"),
    PROFILE("profiles");

    @Getter
    private final String type;

    FileKind(String type) {
        this.type = type;
    }

}
