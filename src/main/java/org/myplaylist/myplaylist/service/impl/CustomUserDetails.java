package org.myplaylist.myplaylist.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
//Class to display the actual username of the User in the thymeleaf template instead of the email
public class CustomUserDetails extends User {
    private String displayUsername; // hold the username
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String displayUsername) {
        super(username, password, authorities);
        this.displayUsername = displayUsername;
    }

    public String getDisplayUsername() {
        return displayUsername;
    }

    public void setDisplayUsername(String displayUsername) {
        this.displayUsername = displayUsername;
    }
}
