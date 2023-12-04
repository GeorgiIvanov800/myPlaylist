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

