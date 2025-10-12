package com.example.petapp.common.base.superclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
// Spring Data JPA에서 createdAt, updatedAt 같은 시간 필드를 자동으로 채워주는 기능을 활성화하기 위해 사용하는 어노테이션.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    /*
     * //@LastModifiedDate vs @UpdateTimestamp
     * spring date Jpa           hibernate
     * dirty checking            update query문
     * */
    @LastModifiedDate
    @Column(name = "updated_at", updatable = true)
    @JsonIgnore
    private LocalDateTime updatedAt;
}
