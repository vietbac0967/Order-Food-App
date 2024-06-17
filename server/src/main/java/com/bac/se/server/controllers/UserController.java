package com.bac.se.server.controllers;

import com.bac.se.server.dto.requests.RegisterRequest;
import com.bac.se.server.dto.requests.UserLogin;
import com.bac.se.server.dto.responses.LoginResponse;
import com.bac.se.server.dto.responses.UserResponse;
import com.bac.se.server.exceptions.UserBadRequestException;
import com.bac.se.server.models.Token;
import com.bac.se.server.models.User;
import com.bac.se.server.services.JWTService;
import com.bac.se.server.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public static final String ACCESS_SECRET_KEY = System.getenv("JWT_ACCESS_TOKEN");
    public static final String REFRESH_SECRET_KEY = System.getenv("JWT_REFRESH_TOKEN");

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody RegisterRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(@RequestBody UserLogin login) {

        if (login.username().isEmpty() || login.password().isEmpty()) {
            throw new UserBadRequestException("Input is required");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username(), login.password())
        );

        if (authentication.isAuthenticated()) {
            long expiredAccessToken = 45 * 1000;
            long expiredRefreshToken = 365L * 12 * 60 * 60 * 1000;
            String accessToken = jwtService.generateToken(login.username(), expiredAccessToken, ACCESS_SECRET_KEY);
            String refreshToken = jwtService.generateRefreshToken(login.username(), expiredRefreshToken, REFRESH_SECRET_KEY);
            return new LoginResponse(accessToken, refreshToken);
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/refreshToken")
    public LoginResponse refreshToken(@RequestBody Token token) {
        return userService.refreshToken(token.getToken());
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }


}
