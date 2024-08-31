package com.dera.task.user.service.controller;

import com.dera.task.user.service.model.Team;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.request.CreateTeamRequest;
import com.dera.task.user.service.service.TeamService;
import com.dera.task.user.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(
            @RequestBody CreateTeamRequest request,
            @RequestHeader("Authorization") String jwt) throws Exception {


        User user = userService.getUserProfile(jwt);
        Team team = teamService.createTeam(request.getTeamName(), user.getRole());
        return ResponseEntity.ok(team);
    }



    @PostMapping("/{teamId}/assign-user")
    public ResponseEntity<String> assignUserToTeam(@PathVariable Long teamId, @RequestParam Long userId) {
        teamService.assignUserToTeam(teamId, userId);
        return ResponseEntity.ok("User assigned to team successfully.");
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<Set<User>> getTeamMembers(@PathVariable Long teamId) {
        Set<User> members = teamService.getTeamMembers(teamId);
        return ResponseEntity.ok(members);
    }
}
