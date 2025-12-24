package com.example.petapp.infrastructure.storage;

import com.example.petapp.application.out.StoragePort;
import com.example.petapp.domain.file.FileKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Adapter implements StoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Override
    public String uploadFile(MultipartFile file, FileKind kind) {
        //파일이 비어있는 경우, 기본 이미지 URL 반환
        if (file == null || file.isEmpty()) {
            return (kind == FileKind.MEMBER)
                    ? cloudFrontDomain + "/defaults/Profile_avatar_placeholder_large.png"
                    : null;
        }

        String fileName = kind.getType() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            //파일의 실제 내용을 AWS로 보낼 수 있는 형태로 변환
            //파일의 내용을 0과 1로 변환하고 파일의 크기도 전달
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return cloudFrontDomain + "/" + fileName;

        } catch (IOException e) {
            log.error("S3 파일 업로드 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
