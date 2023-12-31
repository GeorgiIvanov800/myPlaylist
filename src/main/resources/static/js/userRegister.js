document.addEventListener("DOMContentLoaded", function () { // Ensure the DOM is fully loaded before attaching event listeners
    const passwordField = document.getElementById("password");
    const confirmPasswordField = document.getElementById("confirmPassword");
    const passwordError = document.getElementById("passwordError");

    function isValidPassword(password) {
        // Regular expression to check if password contains at least one letter and one number
        const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{5,}$/;
        return regex.test(password);
    }


    function checkPasswordMatch() {

        if (!isValidPassword(passwordField.value)) {
            passwordError.textContent = "Password should contain both characters and numbers!";
            passwordError.style.display = 'block'; // Show the error message
            passwordField.setCustomValidity("Invalid password!");
            return;
        } else {
            passwordError.style.display = 'none'; // Hide the error message
            passwordField.setCustomValidity("");
        }

        if (passwordField.value !== confirmPasswordField.value) {
            passwordError.textContent = "Passwords do not match!";
            passwordError.style.display = 'block'; // Show the error message
            confirmPasswordField.setCustomValidity("Passwords do not match!"); //prevent form submission
        } else {
            passwordError.textContent = "";
            passwordError.style.display = 'none'; // Hide the error message
            confirmPasswordField.setCustomValidity(""); //allow form submission
        }
    }

    passwordField.addEventListener("input", checkPasswordMatch);
    confirmPasswordField.addEventListener("input", checkPasswordMatch);
});

document.addEventListener("DOMContentLoaded", function() {
    const togglePasswordButtons = document.querySelectorAll('.toggle-password');

    togglePasswordButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const passwordInput = document.getElementById(btn.getAttribute('data-input'));

            // Toggle the type attribute
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            // Toggle the text of the button
            btn.textContent = type === 'password' ? 'Show' : 'Hide';
        });
    });
});



