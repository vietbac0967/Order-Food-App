package com.bac.se.server.repositories;

import com.bac.se.server.exceptions.NotFoundException;
import com.bac.se.server.models.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void findUserByEmail() {
        User user = User.builder()
                .email("vietbacnguyen2002@gmail.com")
                .address("HCM")
                .phone("0928152154")
                .name("Viet Bac")
                .password("123")
                .build();
        final var userByEmail = userRepository.getUserByEmail(user.getEmail()).orElseThrow(() -> new NotFoundException("User is not found"));
        assertThat(userByEmail).isNotNull();

    }
}