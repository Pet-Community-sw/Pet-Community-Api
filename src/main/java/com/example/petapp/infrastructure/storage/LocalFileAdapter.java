package com.example.petapp.infrastructure.storage;


import com.example.petapp.application.out.StoragePort;
import com.example.petapp.domain.file.FileKind;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
public class LocalFileAdapter implements StoragePort {

    @Override
    public String uploadFile(MultipartFile file, FileKind kind) {
        return "local";
    }
}
