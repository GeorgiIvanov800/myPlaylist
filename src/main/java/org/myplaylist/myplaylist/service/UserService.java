package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    void registerUser(UserRegistrationBindingModel userRegistrationBindingModel);

    Authentication login(String email);

    UserEntity findById(Long id);

    String finByEmail(String email);

    boolean activateUser(String token);

    boolean isUserActive(String email);

    Page<UserEntity> getAllUsers(Pageable pageable);

    List<UserRoleEntity> getAllRoles();

    void addRole(Long userId, Long roleId);

    boolean isAdmin(String email);
}
