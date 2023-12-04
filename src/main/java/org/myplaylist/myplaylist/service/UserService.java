package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.UserLoginBindingModel;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;

public interface UserService {

    void registerUser(UserRegistrationBindingModel userRegistrationBindingModel);

    boolean emailExists(String email);

    boolean usernameExists(String username);

}
