package org.myplaylist.myplaylist.utils.impl;

import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRoleChecker {

    private final UserRepository userRepository;

    public UserRoleChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRoles()
                .stream()
                .anyMatch(role -> role.getRole() == UserRoleEnum.ADMIN))
                .orElse(false);
    }

}
