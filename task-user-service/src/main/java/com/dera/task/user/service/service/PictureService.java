package com.dera.task.user.service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
    ResponseEntity<String> uploadProfilePicture(MultipartFile file);
}
