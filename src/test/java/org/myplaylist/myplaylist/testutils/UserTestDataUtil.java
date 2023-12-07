package org.myplaylist.myplaylist.testutils;

import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserTestDataUtil {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserEntity createTestUser(String email) {
        return createUser(email, List.of(UserRoleEnum.USER));
    }

    public UserEntity createTestAdmin(String email) {
        return createUser(email, List.of(UserRoleEnum.ADMIN));
    }

    private UserEntity createUser(String email, List<UserRoleEnum> roles) {

        var roleEntities = userRoleRepository.findAllByRoleIn(roles);

        UserEntity newUser = createTestUser();
        newUser.setRoles(roleEntities);

        return userRepository.save(newUser);
    }

    public void cleanUp() {
        userRepository.deleteAll();
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
        return user;
    }
}
