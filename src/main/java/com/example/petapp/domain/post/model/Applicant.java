package com.example.petapp.domain.post.model;

import lombok.*;

import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant {

    private Long memberId;

    private String memberName;

    private String memberImageUrl;

    private String content;
}

