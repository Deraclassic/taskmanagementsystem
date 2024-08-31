package com.dera.task.user.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private Long id;
    private String password;
    private String email;
    private String role;
    private String fullName;
    private String profilePicture;
}
