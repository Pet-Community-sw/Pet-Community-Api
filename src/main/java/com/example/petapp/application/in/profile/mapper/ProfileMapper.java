package com.example.petapp.application.in.profile.mapper;

import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.application.in.profile.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.application.in.profile.dto.response.GetProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.ProfileListResponseDto;
import com.example.petapp.common.base.util.AgeUtil;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.profile.model.Profile;

import java.time.LocalDate;
import java.time.MonthDay;

public class ProfileMapper {
    public static Profile toEntity(ProfileDto profileDto, Member member, String imageFileName, PetBreed petBreed) {
        return Profile.builder()
                .member(member)
                .petImageUrl(imageFileName)
                .petBirthDate(profileDto.getPetBirthDate())
                .extraInfo(profileDto.getExtraInfo())
                .petBreed(petBreed)
                .petAge(AgeUtil.CalculateAge(profileDto.getPetBirthDate()) + "살")
                .petName(profileDto.getPetName())
                .build();
    }

    public static GetProfileResponseDto toGetProfileResponseDto(Profile profile, Member member) {
        return GetProfileResponseDto.builder()
                .profileId(profile.getId())
                .petBreedName(profile.getPetBreed().getName())
                .petImageUrl(profile.getPetImageUrl())
                .memberId(profile.getMember().getId())
                .petName(profile.getPetName())
                .petAge(profile.getPetAge())
                .petBirthDate(profile.getPetBirthDate())
                .avoidBreeds(profile.getAvoidBreeds())
                .isOwner(member.equals(profile.getMember()))
                .build();
    }

    public static ProfileListResponseDto toProfileListResponseDto(Profile profile) {
        boolean isBirthday = MonthDay.from(LocalDate.now())
                .equals(MonthDay.from(profile.getPetBirthDate()));
        return ProfileListResponseDto.builder()
                .profileId(profile.getId())
                .petImageUrl(profile.getPetImageUrl())
                .petName(profile.getPetName())
                .hasBirthday(isBirthday)
                .build();
    }

    public static AccessTokenByProfileIdResponseDto toAccessTokenToProfileIdResponseDto(Long profileId, String accessToken) {
        return AccessTokenByProfileIdResponseDto.builder()
                .profileId(profileId)
                .accessToken(accessToken)
                .build();
    }

}
