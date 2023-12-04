package org.myplaylist.myplaylist.service;


public interface EmailService {

    void sendRegistrationEmail(String userEmail,
                               String userName,
                               String activationLink);
}
