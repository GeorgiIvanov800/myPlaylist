package org.myplaylist.myplaylist.model.event;

import org.springframework.context.ApplicationEvent;

public class UserRegisterEvent extends ApplicationEvent {
    private final String userEmail;
    private final String userName;

    public UserRegisterEvent(Object source,
                             String userEmail,
                             String userName) {
        super(source);
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }
}
