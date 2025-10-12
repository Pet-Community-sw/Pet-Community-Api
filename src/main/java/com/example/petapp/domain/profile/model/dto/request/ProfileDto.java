package com.example.petapp.domain.profile.model.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    @NotNull(message = "반려견 이미지는 필수입니다.")
    private MultipartFile petImageUrl;

    @NotNull(message = "반려견 종이름은 필수입니다.")
    private Long petBreedId;

    @NotBlank(message = "반려견 이름은 필수입니다.")
    private String petName;//이거 연결해야함.

    @NotNull(message = "반려견 생일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate petBirthDate;

    private List<Long> avoidBreeds;

    private String extraInfo;

}
