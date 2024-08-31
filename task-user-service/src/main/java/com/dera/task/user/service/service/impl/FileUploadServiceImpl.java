package com.dera.task.user.service.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dera.task.user.service.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        // Upload the file to Cloudinary and retrieve the secure URL
        Map<?, ?> uploadResult = cloudinary.uploader()
                .upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}
