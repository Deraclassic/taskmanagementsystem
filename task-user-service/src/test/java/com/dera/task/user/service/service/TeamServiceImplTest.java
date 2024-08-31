package com.dera.task.user.service.service;

import com.dera.task.user.service.exceptions.ResourceNotFoundException;
import com.dera.task.user.service.model.Team;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.repository.TeamRepository;
import com.dera.task.user.service.repository.UserRepository;
import com.dera.task.user.service.service.impl.TeamServiceImpl;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks  // Inject the mocks into the actual service implementation
    private TeamServiceImpl teamService;

    private Team team;
    private User user;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Test Team");
        team.setUsers(new HashSet<>());

        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
    }

    @Test
    void testCreateTeamWithAdminRole() {
        when(teamRepository.save(any(Team.class))).thenReturn(team); // Mocking save to return a sample team.

        Team createdTeam = teamService.createTeam("Test Team", "ROLE_ADMIN");

        assertThat(createdTeam).isNotNull();
        assertThat(createdTeam.getName()).isEqualTo("Test Team");
        verify(teamRepository, times(1)).save(any(Team.class)); // Ensure save is called exactly once.
    }

    @Test
    void testCreateTeamWithNonAdminRoleThrowsException() {
        assertThatThrownBy(() -> teamService.createTeam("Test Team", "ROLE_USER"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only admins can create teams"); // Verify exception message.
    }


    @Test
    void testAssignUserToTeam() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        teamService.assignUserToTeam(1L, 1L);

        assertThat(team.getUsers()).contains(user);
        verify(teamRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testAssignUserToTeamThrowsExceptionWhenTeamOrUserNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> teamService.assignUserToTeam(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Team or User not found");

        verify(teamRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTeamMembers() {
        Set<User> users = new HashSet<>();
        users.add(user);
        team.setUsers(users);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        Set<User> teamMembers = teamService.getTeamMembers(1L);

        assertThat(teamMembers).isNotEmpty();
        assertThat(teamMembers).contains(user);
        verify(teamRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTeamMembersThrowsExceptionWhenTeamNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.getTeamMembers(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Team not found");

        verify(teamRepository, times(1)).findById(1L);
    }
}
