package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    void registerUser(UserRegistrationBindingModel userRegistrationBindingModel);

    UserEntity findById(Long id);

    String finByEmail(String email);

    boolean activateUser(String token);

    UserStatus getUserStatus(String email);

    Page<UserEntity> getAllUsers(Pageable pageable);

    List<UserRoleEntity> getAllRoles();

    void addOrRemoveRole(Long userId, Long roleId, String action);

    boolean isAdmin(String email);

    boolean isActive(String email);
}
