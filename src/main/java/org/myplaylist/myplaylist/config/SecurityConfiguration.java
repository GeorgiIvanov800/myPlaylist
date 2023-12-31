package org.myplaylist.myplaylist.config;

import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.impl.MyPlaylistUserDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private final String rememberMeKey;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfiguration(@Value("${myPlaylist.remember.me.key}")
                                 String rememberMeKey,
                                 CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.rememberMeKey = rememberMeKey;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //Config
        //Define which urls are visible by which users

        return httpSecurity.authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                        //all resources which are static in js, images, css are available for anyone
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        //allow anyone to see the home page, the registration page and the login form
                        .requestMatchers("/", "/users/login", "/users/register", "/users/login-error", "/about").permitAll()
                        .requestMatchers("/user/activate", "/activation-failed").permitAll()
                        .requestMatchers("/error").permitAll()
                        //only admins have access to admin page
                        .requestMatchers("/admin/panel").hasAnyRole("ADMIN")
                        //all other requests are authenticated
                        .anyRequest().authenticated()
        ).formLogin(
                formLogin -> {
                    formLogin
                            //redirect here when we access something which is not allowed.
                            // also, this is the page where we perform login.
                            .loginPage("/users/login")
                            //the names of the input fields (user-register)
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/users/dashboard")
                            .failureHandler(customAuthenticationFailureHandler);
                }
        ).logout(
                logout -> {
                    logout
                            //the url where we should POST in order to perform the logout
                            .logoutUrl("/users/logout")
                            //where to go after logout
                            .logoutSuccessUrl("/")
                            //delete(invalidate) the session
                            .invalidateHttpSession(true);
                }

        ).rememberMe(
                rememberMe -> { // Remember me cookie
                    rememberMe
                            .key(rememberMeKey)
                            .rememberMeParameter("rememberme")
                            .rememberMeCookieName("rememberme")
                            .tokenValiditySeconds(7 * 24 * 60 * 60); //Cookie will live only 1 week
                })
//                .csrf().disable()
                .build();

    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        // This service translates the myPlaylist users and roles
        //to representation which spring security understands
        return new MyPlaylistUserDetailService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

}