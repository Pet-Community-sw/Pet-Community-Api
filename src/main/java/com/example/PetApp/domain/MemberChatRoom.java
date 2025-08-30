package com.example.PetApp.domain;

import com.example.PetApp.domain.superclass.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class MemberChatRoom extends BaseEntity {

    @Builder.Default//이미 초기화가 되어있기때문에 notnull이 필요 없음.
    @Setter
    @OneToMany(fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

}
