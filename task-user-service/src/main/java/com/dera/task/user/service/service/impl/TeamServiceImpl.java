package com.dera.task.user.service.service.impl;

import com.dera.task.user.service.exceptions.ResourceNotFoundException;
import com.dera.task.user.service.model.Team;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.repository.TeamRepository;
import com.dera.task.user.service.repository.UserRepository;
import com.dera.task.user.service.service.TeamService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public Team createTeam(String teamName, String requesterRole) {
        // Log incoming parameters for debugging
        System.out.println("Creating team with name: " + teamName);
        System.out.println("Requester role: " + requesterRole);

        // Validate role
        if (!"ROLE_ADMIN".equals(requesterRole)) {
            throw new IllegalArgumentException("Only admins can create teams");
        }

        // Create and save the team
        Team team = new Team();
        team.setName(teamName);
        return teamRepository.save(team);
    }

    @Override
    public void assignUserToTeam(Long teamId, Long userId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (teamOptional.isPresent() && userOptional.isPresent()) {
            Team team = teamOptional.get();
            User user = userOptional.get();

            team.getUsers().add(user);
            teamRepository.save(team);
        } else {
            throw new RuntimeException("Team or User not found");
        }
    }

    @Override
    public Set<User> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return team.getUsers();
    }
}
