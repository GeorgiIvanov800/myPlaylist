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
//Add a spinner when the User creates the playlist and to be sure that the image is indeed saved in the resources
document.addEventListener('DOMContentLoaded', function() {
    const uploadForms = document.querySelectorAll('.imageUploadForm');

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
                        }, 1000); // Spinner time
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

            // Remove hidden class to reveal songs
            let hiddenItems = songList.querySelectorAll('.hidden');
            hiddenItems.forEach(function(item) {
                item.classList.remove('hidden');
            });

            // Hide the "Show More" link
            this.style.display = 'none';

            // all separators are visible
            let separators = songList.querySelectorAll('.song-separator');
            separators.forEach(separator => separator.style.display = 'block');

            // Then hide the last one
            if (separators.length) {
                separators[separators.length - 1].style.display = 'none';
            }
        });
    });

    //Hide the last visible separator
    document.querySelectorAll('.song-list').forEach(function(songList) {
        let separators = songList.querySelectorAll('.song-separator');
        if (separators.length) {
            separators[separators.length - 1].style.display = 'none';
        }
    });
});

function toggleHiddenSongs() {
    document.querySelectorAll('.song-item.hidden').forEach(el => {
        el.classList.toggle('hidden');
    });
}

