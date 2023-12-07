package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.entity.UserActivationLinkEntity;
import org.myplaylist.myplaylist.model.event.UserRegisterEvent;

public interface UserActivationService {

    void userRegistered(UserRegisterEvent event);

    void cleanUpObsoleteActivationLinks();

    String createActivationLink(String userEmail);

    UserActivationLinkEntity getToken(String token);
}
