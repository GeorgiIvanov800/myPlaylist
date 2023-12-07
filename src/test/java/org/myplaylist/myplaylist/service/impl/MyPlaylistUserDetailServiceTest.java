package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyPlaylistUserDetailServiceTest {
    private MyPlaylistUserDetailService serviceToTest;
    @Mock
    private UserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        serviceToTest = new MyPlaylistUserDetailService(
                mockUserRepository
        );
    }

    @Test
    void testUserNotFound() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> serviceToTest.loadUserByUsername("gosho@softuni.bg")
        );
    }

    @Test void testUserFoundException() {
        // Arrange
        UserEntity testUserEntity = createTestUser();
        when(mockUserRepository.findByEmail(testUserEntity.getEmail()))
                .thenReturn(Optional.of(testUserEntity));

        // Act
        UserDetails userDetails =
                serviceToTest.loadUserByUsername(testUserEntity.getEmail());

        //Assert
        assertNotNull(userDetails);
        assertEquals(
                testUserEntity.getEmail(),
                userDetails.getUsername(),
                "Username is not mapped to email.");

        assertEquals(testUserEntity.getPassword(), userDetails.getPassword());

        assertEquals(3, userDetails.getAuthorities().size());

        assertTrue(
                containsAuthority(userDetails, "ROLE_" + UserRoleEnum.ADMIN),
                "The user is not admin");

        assertTrue(
                containsAuthority(userDetails, "ROLE_" + UserRoleEnum.USER),
                "The user is not user");

        assertTrue(containsAuthority(userDetails, "ROLE_" + UserRoleEnum.MODERATOR),
                "The user is not Moderator");
    }


    private boolean containsAuthority(UserDetails userDetails, String expectedAuthority) {
        return userDetails
                .getAuthorities()
                .stream()
                .anyMatch(a -> expectedAuthority.equals(a.getAuthority()));
    }

    private static UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setEmail("testuser@example.com");
        user.setPassword("StrongPassword123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testUser");
        user.setRegisterDate(LocalDateTime.now());
        user.setRoles(List.of(
                new UserRoleEntity().setRole(UserRoleEnum.USER),
                new UserRoleEntity().setRole(UserRoleEnum.ADMIN),
                new UserRoleEntity().setRole(UserRoleEnum.MODERATOR)
        ));
        user.setActive(false);
        return user;
    }

}
