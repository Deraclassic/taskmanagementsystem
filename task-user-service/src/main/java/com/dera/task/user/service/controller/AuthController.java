package com.dera.task.user.service.controller;

import com.dera.task.user.service.config.JwtProvider;
import com.dera.task.user.service.model.User;
import com.dera.task.user.service.repository.UserRepository;
import com.dera.task.user.service.request.LoginRequest;
import com.dera.task.user.service.response.AuthResponse;
import com.dera.task.user.service.service.CustomUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserServiceImpl customUserService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        String password = user.getPassword();
        String email = user.getEmail();
        String fullName = user.getFullName();
        String role = user.getRole();

        User isEmailExists = userRepository.findByEmail(email);
        if (isEmailExists != null) {
            throw new Exception("Email Is Already Used With Another Account");
        }

        //create user
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(createdUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = JwtProvider.generateToken(auth);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Registered Successfully");
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        System.out.println(username + " " + password);

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);
        System.out.println("Sign in details: " + userDetails);

        if (userDetails == null){
            System.out.println("Sign in details not found");
        throw new BadCredentialsException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("Password does not match" +userDetails);
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
