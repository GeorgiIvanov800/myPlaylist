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
            passwordField.setCustomValidity("Invalid password!");
            return;
        } else {
            passwordField.setCustomValidity("");
        }

        if (passwordField.value !== confirmPasswordField.value) {
            passwordError.textContent = "Passwords do not match!";
            confirmPasswordField.setCustomValidity("Passwords do not match!"); //prevent form submission
        } else {
            passwordError.textContent = "";
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


// $.ajaxSetup({ cache: false });
// $(document).ready(function(){
//     $('#email').blur(function(){
//         let email = $(this).val();
//         $.get('/users/checkEmail', { email: email }, function(response){
//             console.log(response);
//             if(response) {
//                 $('#emailError').text('Email already in use').show();
//             } else {
//                 $('#emailError').hide();
//             }
//         });
//     });
//
//     $('#username').blur(function(){
//         let username = $(this).val();
//         $.get('/users/checkUsername', { username: username }, function(response){
//             if(response) {
//                 $('#usernameError').text('Username already in use').show();
//             } else {
//                 $('#usernameError').hide();
//             }
//         });
//     });
// });


