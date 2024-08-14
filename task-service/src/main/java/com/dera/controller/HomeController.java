package com.dera.controller;

import com.dera.model.Task;
import com.dera.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @PutMapping("/tasks")
    public ResponseEntity<String> assignedTaskToUser(@PathVariable Long id,
                                                   @PathVariable Long userid, @RequestHeader("Authorization") String jwt) throws Exception {

        return new ResponseEntity<>("Welcome to the task service", HttpStatus.OK);
    }
}
