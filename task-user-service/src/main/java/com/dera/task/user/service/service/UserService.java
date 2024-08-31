package com.dera.task.user.service.service;

import com.dera.task.user.service.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public User getUserProfile(String jwt);

    public List<User> getAllUsers();
    public ResponseEntity<String> uploadProfilePicture(MultipartFile profilePicture, String userEmail);
}