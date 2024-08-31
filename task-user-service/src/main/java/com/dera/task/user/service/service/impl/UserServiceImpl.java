package com.dera.task.user.service.service.impl;

import com.dera.task.user.service.config.JwtProvider;
import com.dera.task.user.service.exceptions.ResourceNotFoundException;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.repository.UserRepository;
import com.dera.task.user.service.request.UserProfileUpdateRequest;
import com.dera.task.user.service.service.FileUploadService;
import com.dera.task.user.service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    @Override
    public User getUserProfile(String jwt) {
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ResponseEntity<String> uploadProfilePicture(MultipartFile profilePicture, String userEmail) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(userEmail));
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();
        try {
            String fileUrl = fileUploadService.uploadFile(profilePicture);
            user.setProfilePicture(fileUrl);
            userRepository.save(user);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload profile picture");
        }
        return ResponseEntity.ok("Uploaded successfully: " + user.getProfilePicture());
    }

    @Transactional
    @Override
    public void updateUserProfile(Long userId, UserProfileUpdateRequest updateRequest) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getFullName() != null) {
            user.setFullName(updateRequest.getFullName());
        }
        if(updateRequest.getPassword() != null){
            user.setPassword(updateRequest.getPassword());
        }
        if (updateRequest.getProfilePicture() != null) {
            user.setProfilePicture(updateRequest.getProfilePicture());
        }

        userRepository.save(user);
    }
}
