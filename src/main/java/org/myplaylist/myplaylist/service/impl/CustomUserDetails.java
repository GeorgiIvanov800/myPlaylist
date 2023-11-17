package org.myplaylist.myplaylist.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
//Class to display the actual username of the User in the thymeleaf template instead of the email
public class CustomUserDetails extends User {
    private String displayUsername; // hold the username
    private Long userId; // will be needed for database calls
    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String displayUsername,
                             Long userId) {
        super(username, password, authorities);
        this.displayUsername = displayUsername;
        this.userId = userId;
    }

    public String getDisplayUsername() {
        return displayUsername;
    }

    public void setDisplayUsername(String displayUsername) {
        this.displayUsername = displayUsername;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
