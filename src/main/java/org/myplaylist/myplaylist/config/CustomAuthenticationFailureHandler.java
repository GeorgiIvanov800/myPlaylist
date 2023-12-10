package org.myplaylist.myplaylist.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String email = request.getParameter("email"); // take the email and parse it to the get to fill the form

        if (exception instanceof DisabledException) {
            // Handle an inactive user case
            request.getSession().setAttribute("loginError", "Your account is not activated.");
            request.getSession().setAttribute("email", email);
        } else if (exception instanceof BadCredentialsException) {
            // Handle a bad credentials case
            request.getSession().setAttribute("loginError", "Invalid email or password.");
            request.getSession().setAttribute("email", email);
        }

        // Redirect to the login page with error
        response.sendRedirect("/users/login");
    }
}
