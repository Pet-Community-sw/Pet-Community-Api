package com.example.petapp.application.common.imagefile;

import lombok.Getter;
import lombok.Setter;

public enum FileImageKind {
    POST("posts"),
    MEMBER("members"),
    PROFILE("profiles");

    @Getter
    @Setter
    private String type;

    FileImageKind(String type) {
        this.type = type;
    }

}
