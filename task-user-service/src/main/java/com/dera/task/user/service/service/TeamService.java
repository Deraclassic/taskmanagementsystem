package com.dera.task.user.service.service;

import com.dera.task.user.service.model.Team;
import com.dera.task.user.service.model.User;

import java.util.List;

public interface TeamService {
    Team createTeam(String teamName);
    void assignUserToTeam(Long teamId, Long userId);
    List<User> getTeamMembers(Long teamId);
}
