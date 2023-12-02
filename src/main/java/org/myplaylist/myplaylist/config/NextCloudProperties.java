package org.myplaylist.myplaylist.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nextcloud")
public class NextCloudProperties {

    private String webdavUrl;
    private String username;
    private String password;
    private String shareLink;

    public String getWebdavUrl() {
        return webdavUrl;
    }

    public void setWebdavUrl(String webdavUrl) {
        this.webdavUrl = webdavUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }
}
