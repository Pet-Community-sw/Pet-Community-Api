package com.example.petapp.infrastructure.stomp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.security.Principal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SubscribeInfo {

    private String subscriptionId;

    private String destination;

    private Principal principal;
}
