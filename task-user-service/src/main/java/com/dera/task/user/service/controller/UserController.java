package com.dera.task.user.service.controller;

import com.dera.task.user.service.exceptions.ResourceNotFoundException;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.request.UserProfileUpdateRequest;
import com.dera.task.user.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) {
        User user = userService.getUserProfile(jwt);
        return new ResponseEntity<> (user, HttpStatus.OK);
    }
    @GetMapping()
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String jwt) {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<> (users, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return new ResponseEntity<> (user, HttpStatus.OK);
    }
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @RequestParam("email") String email,
            @RequestParam("file") MultipartFile profilePicture) {
        // Delegate the task to the service layer
        return userService.uploadProfilePicture(profilePicture, email);
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileUpdateRequest updateRequest) {
        try {
            userService.updateUserProfile(userId, updateRequest);
            return ResponseEntity.ok("User profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update user profile");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }
}
