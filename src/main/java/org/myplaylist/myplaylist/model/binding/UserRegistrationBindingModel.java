package org.myplaylist.myplaylist.model.binding;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.myplaylist.myplaylist.model.validation.anotations.FieldMatch;
import org.myplaylist.myplaylist.model.validation.anotations.UniqueUserEmail;
import org.myplaylist.myplaylist.model.validation.anotations.UniqueUsername;

@FieldMatch(
        first = "password",
        second = "confirmPassword",
        message = "Passwords did not match"
)
public class UserRegistrationBindingModel {
    @NotEmpty
    @Size(min = 3, max = 20, message = "Username should be between 3 and 20 characters")
    @UniqueUsername
    private String username;
    @NotEmpty(message = "First Name cannot be empty")
    private String firstName;
    @NotEmpty(message = "Last Name cannot be empty")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty")
    @Pattern(regexp = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$", message = "Invalid Email")
    @UniqueUserEmail
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must contain both characters and numbers")
    @Size(min = 8, max = 20, message = "Password must be between 5 and 20 characters long")
    private String password;
    private String confirmPassword;

    public UserRegistrationBindingModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }


    public String fullName() {
        return firstName + " " +  lastName;
    }
}
