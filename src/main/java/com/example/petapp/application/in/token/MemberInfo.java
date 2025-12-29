package com.example.petapp.application.in.token;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemberInfo {

    private Long memberId;

    private Long profileId;

    private String name;

    private List<String> roles;
}
