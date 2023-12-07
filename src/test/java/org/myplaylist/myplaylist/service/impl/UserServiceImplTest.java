package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.config.mapper.UserMapper;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserActivationLinkEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.event.UserRegisterEvent;
import org.myplaylist.myplaylist.repository.UserActivationLinkRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.repository.UserRoleRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private UserServiceImpl serviceToTest;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;
    @Mock
    private UserRoleRepository mockUserRoleRepository;
    @Mock
    private UserActivationLinkRepository mockUserActivationLinkRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private UserMapper mockUserMapper;

    @Mock
    private UserDetailsService mockUserDetailsService;

    @BeforeEach
    void SetUp() {
        serviceToTest = new UserServiceImpl(
                mockUserRepository,
                mockUserActivationLinkRepository,
                mockUserRoleRepository,
                mockPasswordEncoder,
                mockUserMapper,
                mockApplicationEventPublisher,
                mockUserDetailsService

        );
    }

    @Test
    void registerUser_Success() {
        // Arrange

        UserRegistrationBindingModel userModel = mock(UserRegistrationBindingModel.class);
        UserEntity testUserEntity = createTestUser();
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRole(UserRoleEnum.USER);

        when(mockUserMapper.userDTOtoUserEntity(userModel)).thenReturn(testUserEntity);
        when(mockUserRoleRepository.findByRole(UserRoleEnum.USER)).thenReturn(userRole);
        when(mockPasswordEncoder.encode(anyString())).thenReturn("StrongPassword123");

        // Act
        serviceToTest.registerUser(userModel);

        // Assert
        verify(mockUserMapper).userDTOtoUserEntity(userModel);
        verify(mockUserRoleRepository).findByRole(UserRoleEnum.USER);
        verify(mockPasswordEncoder).encode(testUserEntity.getPassword());
        verify(mockUserRepository).save(testUserEntity);
        verify(mockApplicationEventPublisher).publishEvent(any(UserRegisterEvent.class));

    }

    @Test
    void findById_Success() {
        // Arrange
        Long userId = 1L;
        UserEntity expectedUser = new UserEntity();
        expectedUser.setId(userId);
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        UserEntity result = serviceToTest.findById(userId);

        // Assert
        assertEquals(expectedUser, result);
        verify(mockUserRepository).findById(userId);
    }

    @Test
    void findById_NotFound() {
        // Arrange
        Long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ObjectNotFoundException.class, () -> {
            // Act
            serviceToTest.findById(userId);
        });
        verify(mockUserRepository).findById(userId);
    }


    @Test
    void findByEmail_UserFound() {
        // Arrange
        UserEntity userEntity = createTestUser();

        when(mockUserRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(userEntity));

        // Act
        String result = serviceToTest.finByEmail("testuser@example.com");

        // Assert
        assertEquals("testUser", result);
        verify(mockUserRepository).findByEmail("testuser@example.com");
    }

    @Test
    void findByEmail_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        String result = serviceToTest.finByEmail(email);

        // Assert
        assertNull(result);
        verify(mockUserRepository).findByEmail(email);
    }

    @Test
    void activateUser_Success() {
        // Arrange
        String token = "validToken";
        UserEntity user = createTestUser();
        user.setActive(false);

        UserActivationLinkEntity userActivationLink = new UserActivationLinkEntity();
        userActivationLink.setUser(user);

        when(mockUserActivationLinkRepository.findByActivationLink(token)).thenReturn(userActivationLink);

        // Act
        boolean result = serviceToTest.activateUser(token);

        // Assert
        assertTrue(result);
        verify(mockUserActivationLinkRepository).findByActivationLink(token);
        verify(mockUserRepository).save(user);
        verify(mockUserActivationLinkRepository).delete(userActivationLink);
    }

    @Test
    void activateUser_InvalidToken() {
        // Arrange
        String token = "invalidToken";
        when(mockUserActivationLinkRepository.findByActivationLink(token)).thenReturn(null);

        // Act
        boolean result = serviceToTest.activateUser(token);

        // Assert
        assertFalse(result);
        verify(mockUserActivationLinkRepository).findByActivationLink(token);
        verifyNoInteractions(mockUserRepository);
    }

    @Test
    void activateUser_UserAlreadyActive() {
        // Arrange
        String token = "validToken";
        UserEntity user = new UserEntity();
        user.setActive(true);

        UserActivationLinkEntity userActivationLink = new UserActivationLinkEntity();
        userActivationLink.setUser(user);

        when(mockUserActivationLinkRepository.findByActivationLink(token)).thenReturn(userActivationLink);

        // Act
        boolean result = serviceToTest.activateUser(token);

        // Assert
        assertFalse(result);
        verify(mockUserActivationLinkRepository).findByActivationLink(token);
        verifyNoMoreInteractions(mockUserRepository, mockUserActivationLinkRepository);
    }

    @Test
    void addOrRemoveRole_UserNotFound() {
        // Arrange
        Long userId = 1L;
        Long roleId = 1L;
        String action = "addRole";
        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ObjectNotFoundException.class, () -> {
            // Act
            serviceToTest.addOrRemoveRole(userId, roleId, action);
        });
    }

    @Test
    void addOrRemoveRole_RoleNotFound() {
        // Arrange
        Long userId = 1L;
        Long roleId = 1L;
        String action = "addRole";
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(mockUserRoleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ObjectNotFoundException.class, () -> {
            // Act
            serviceToTest.addOrRemoveRole(userId, roleId, action);
        });
    }

    @Test
    void addOrRemoveRole_AddRole() {
        // Arrange
        Long userId = 1L;
        Long roleId = 1L;
        String action = "addRole";
        UserEntity user = new UserEntity();
        UserRoleEntity role = new UserRoleEntity();

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRoleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        serviceToTest.addOrRemoveRole(userId, roleId, action);

        // Assert
        verify(mockUserRepository).findById(userId);
        verify(mockUserRoleRepository).findById(roleId);
        // Verify that addRoleToUser was called
    }

    @Test
    void addOrRemoveRole_RemoveRole() {
        // Arrange
        Long userId = 1L;
        Long roleId = 1L;
        String action = "removeRole";
        UserEntity user = new UserEntity();
        UserRoleEntity role = new UserRoleEntity();

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRoleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        serviceToTest.addOrRemoveRole(userId, roleId, action);

        // Assert
        verify(mockUserRepository).findById(userId);
        verify(mockUserRoleRepository).findById(roleId);
        // Verify that removeRoleFromUser was called
    }

    @Test
    void addOrRemoveRole_UnknownAction() {
        // Arrange
        Long userId = 1L;
        Long roleId = 1L;
        String action = "unknownAction";
        UserEntity user = new UserEntity();
        UserRoleEntity role = new UserRoleEntity();

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserRoleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        serviceToTest.addOrRemoveRole(userId, roleId, action);

        // Assert
        verify(mockUserRepository).findById(userId);
        verify(mockUserRoleRepository).findById(roleId);
        // Verify that LOGGER.error was called with the unknown action
    }


    private static UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setEmail("testuser@example.com");
        user.setPassword("StrongPassword123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testUser");
        user.setRegisterDate(LocalDateTime.now());
        user.setActive(false);
        List<UserRoleEntity> roles = new ArrayList<>();
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.USER));
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.ADMIN));
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.MODERATOR));
        user.setRoles(roles);
        return user;
    }
}
