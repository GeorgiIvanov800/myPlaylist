// Inform the User that the upload picture size should be max of 1mb
document.addEventListener('DOMContentLoaded', function () {
    const fileInputs = document.querySelectorAll('.file-input');

    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            const file = this.files[0];
            if (file && file.size > 1048576) { // 1 MB
                alert('Sorry but the file size should be less than 1MB');
                this.value = ''; // Reset the input
            }
        });
    });
});

//Disable or Enable the button depends on how big is the file
document.addEventListener('DOMContentLoaded', function () {
    // Select all file inputs and upload buttons
    const fileInputs = document.querySelectorAll('.file-input');
    const uploadButtons = document.querySelectorAll('.upload-button');

    fileInputs.forEach((input, index) => {
        input.addEventListener('change', function () {
            // Enable or disable the upload button
            uploadButtons[index].disabled = this.files.length <= 0;
        });
    });
});
//Add a spiner when the User creates the playlist and to be sure that the image is indeed saved in the resources
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM fully loaded and parsed");
    const uploadForms = document.querySelectorAll('.imageUploadForm');

    console.log("Form: ", uploadForms);

    uploadForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const spinner = form.querySelector('.spinner');
            spinner.style.display = 'block'; // Show spinner

            let formData = new FormData(this);
            fetch(this.action, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        setTimeout(() => {
                            spinner.style.display = 'none'; // Hide spinner
                            // Reloads the page or navigates to the dashboard
                            window.location.href = '/users/dashboard';
                        }, 2000); // Adjust the delay as needed
                    } else {
                        alert('File upload failed.');
                        spinner.style.display = 'none'; // Hide spinner
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('File upload failed.');
                    spinner.style.display = 'none'; // Hide spinner
                });
        });
    });
});

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.show-more').forEach(function(link) {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            let songList = this.previousElementSibling;
            songList.querySelectorAll('.hidden').forEach(function(item) {
                item.classList.remove('hidden');
            });
            this.style.display = 'none'; // Hide the "Show More" link
        });
    });
});
