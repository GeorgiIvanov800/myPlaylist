package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.springframework.security.core.Authentication;

public interface UserService {

    void registerUser(UserRegistrationBindingModel userRegistrationBindingModel);

    Authentication login(String email);

}
