package com.dera.task.user.service.service;

import com.dera.task.user.service.config.JwtProvider;
import com.dera.task.user.service.exceptions.ResourceNotFoundException;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.repository.UserRepository;
import com.dera.task.user.service.request.UserProfileUpdateRequest;
import com.dera.task.user.service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private MultipartFile profilePicture;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("John Doe");
    }

    @Test
    void testGetUserProfile() {
        String jwt = "dummyJwtToken"; // Dummy token, we won't actually parse it
        String email = "test@example.com";

        // Mock the static method JwtProvider.getEmailFromJwtToken to return the email directly
        mockStatic(JwtProvider.class);
        when(JwtProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.getUserProfile(jwt);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository, times(1)).findByEmail(email);
    }


    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertThat(users).isNotEmpty();
        assertThat(users).contains(user);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUploadProfilePictureSuccess() throws IOException {
        String userEmail = "test@example.com";
        String fileUrl = "http://example.com/profile.jpg";

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(fileUploadService.uploadFile(profilePicture)).thenReturn(fileUrl);

        ResponseEntity<String> response = userService.uploadProfilePicture(profilePicture, userEmail);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Uploaded successfully: " + fileUrl);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUploadProfilePictureUserNotFound() {
        String userEmail = "test@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        ResponseEntity<String> response = userService.uploadProfilePicture(profilePicture, userEmail);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void testUploadProfilePictureIOException() throws IOException {
        String userEmail = "test@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(fileUploadService.uploadFile(profilePicture)).thenThrow(new IOException("Failed"));

        ResponseEntity<String> response = userService.uploadProfilePicture(profilePicture, userEmail);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("Failed to upload profile picture");
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserProfile(1L, updateRequest);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserProfileThrowsExceptionWhenUserNotFound() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserProfile(1L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUserThrowsExceptionWhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }
}
