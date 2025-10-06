package com.example.PetApp.common.stomp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.security.Principal;

import static com.example.PetApp.domain.chatting.model.entity.ChatMessage.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SubscribeInfo {

    private String destination;

    private Principal principal;
}
