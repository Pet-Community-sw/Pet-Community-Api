package com.example.petapp.domain.post.delegate.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDelegateWalkPostDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "가격은 필수입니다.")
    private Long price;

    @NotNull(message = "위치경도는 필수입니다.")
    private Double locationLongitude;

    @NotNull(message = "위치위도는 필수입니다.")
    private Double locationLatitude;

    @NotNull(message = "산책 범위는 필수입니다.")
    private Integer allowedRadiusMeters;

    @NotBlank(message = "시간은 필수입니다.")
    @Schema(example = "13:30")
    private String scheduledTime;

    @NotNull(message = "프로필 여부는 필수입니다.")
    private boolean requireProfile;
}
