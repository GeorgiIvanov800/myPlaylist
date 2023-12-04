package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.event.UserRegisterEvent;

public interface UserActivationService {

    void userRegistered(UserRegisterEvent event);

    void cleanUpObsoleteActivationLinks();

    String createActivationLink(String userEmail);
}
