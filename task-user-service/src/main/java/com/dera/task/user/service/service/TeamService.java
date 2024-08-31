package com.dera.task.user.service.service;

import com.dera.task.user.service.model.Team;
import com.dera.task.user.service.model.User;

import java.util.List;
import java.util.Set;

public interface TeamService {
    Team createTeam(String teamName, String requesterRole);
    void assignUserToTeam(Long teamId, Long userId);
    Set<User> getTeamMembers(Long teamId);
}
