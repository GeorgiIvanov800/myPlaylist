// Inform the User that the upload picture size should be max of 1mb
//and trigger the upload
document.addEventListener('DOMContentLoaded', function () {
    // Select all file inputs within forms having the class 'image-upload-form'
    const fileInputs = document.querySelectorAll('.image-upload-form input[type="file"]');

    fileInputs.forEach(fileInput => {
        fileInput.addEventListener('change', function () {
            const file = this.files[0];
            if (file) {
                // Validate file size
                if (file.size > 1048576) { // 1MB size limit
                    alert('Sorry, but the file size should be less than 1MB');
                    this.value = ''; // Reset the file input
                    return;
                }
                // Validate file type
                if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
                    alert('Sorry, only JPG and PNG files are allowed');
                    this.value = ''; // Reset the file input
                    return;
                }

                // If the file is valid, submit the form and show the spinner
                this.closest('.image-upload-form').submit();
                document.getElementById('loadingOverlay').style.display = 'flex';
            }
        });
    });
});


document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.show-more').forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            let songList = this.previousElementSibling;

            // Remove hidden class to reveal songs
            let hiddenItems = songList.querySelectorAll('.hidden');
            hiddenItems.forEach(function (item) {
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
    document.querySelectorAll('.song-list').forEach(function (songList) {
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

function ratePlaylist(playlistId, ratingType) {

    let csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    let csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    let csrfToken = csrfTokenMeta.getAttribute('content');
    let csrfHeader = csrfHeaderMeta.getAttribute('content');

    console.log(document.getElementById(`likeCount_${playlistId}`)); // For debugging
    console.log(document.getElementById(`dislikeCount_${playlistId}`)); // For debugging
    console.log("Iam here");

    fetch(`/api/playlist/${playlistId}/${ratingType.toLowerCase()}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            [csrfHeader]: csrfToken
        },

    })
        .then(response => response.json())
        .then(data => {
            if (data) {
                updateRatingCount(playlistId, data.likeCount, data.dislikeCount);
            } else {
                console.error('Error retrieving updated counts');
            }
        })
        .catch(error => console.error('Error:', error));
}

function updateRatingCount(playlistId, likeCount, dislikeCount) {
    const likeCountElement = document.getElementById(`likeCount_${playlistId}`);
    const dislikeCountElement = document.getElementById(`dislikeCount_${playlistId}`);

    likeCountElement.textContent = likeCount;
    dislikeCountElement.textContent = dislikeCount;
}

function showSpinner() {
    document.getElementById('loadingOverlay').style.display = 'flex';
}


