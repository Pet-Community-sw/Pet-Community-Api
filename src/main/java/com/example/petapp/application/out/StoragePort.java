package com.example.petapp.application.out;

import com.example.petapp.domain.file.FileKind;
import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {

    String uploadFile(MultipartFile file, FileKind kind);
}
