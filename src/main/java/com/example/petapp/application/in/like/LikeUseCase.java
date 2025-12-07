package com.example.petapp.application.in.like;

public interface LikeUseCase {

    boolean createAndDelete(Long postId, String email);

}
