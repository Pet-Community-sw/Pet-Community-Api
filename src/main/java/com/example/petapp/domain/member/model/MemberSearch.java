package com.example.petapp.domain.member.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Document(indexName = "members")
public class MemberSearch {

    @Id
    private Long memberId;

    @Field(type = FieldType.Text)
    private String memberName;

    @Field(type = FieldType.Text)
    private String memberNameChosung;

    @Field(type = FieldType.Keyword)
    private String memberImageUrl;

    @Field(type = FieldType.Long)
    private Long outboxEventId;

    @Builder.Default
    @Field(type = FieldType.Boolean)
    private boolean isDeleted = false;
}


