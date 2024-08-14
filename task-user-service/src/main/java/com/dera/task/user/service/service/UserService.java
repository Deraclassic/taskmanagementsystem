package com.dera.task.user.service.service;

import com.dera.task.user.service.model.User;

import java.util.List;

public interface UserService {
    public User getUserProfile(String jwt);
    public List<User> getAllUsers();
}
