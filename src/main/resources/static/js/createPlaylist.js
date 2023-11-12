//Event Listener on the Playlist Form
document.addEventListener('DOMContentLoaded', function () {
    let form = document.getElementById('playlistForm');
    console.log("Form element: ", form);
    //Submit button
    form.addEventListener('submit', function(e) {
        // console.log("Submit button clicked.");
        e.preventDefault();

        let name = document.getElementById('playlistName').value;
        let description = document.getElementById('playlistDescription').value;
        let songIds = [];
        let genre = document.getElementById('playlistGenre').value;

        songIds = Array.from(document.querySelectorAll('#playlistSongs li'))
            .map(li => li.dataset.songId);


        let csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
        let csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfTokenMeta || !csrfHeaderMeta) {
            console.error('CSRF token or header not found in the HTML meta tags');
            return; // Exit the function if the meta tags are not found
        }

        let csrfToken = csrfTokenMeta.getAttribute('content');
        let csrfHeader = csrfHeaderMeta.getAttribute('content');

        fetch('/api/playlist', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ name, description, songIds, genre })
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        // Clear any previous error messages
                        document.querySelectorAll('.error-message').forEach(el => el.textContent = '');

                        // Display new error messages next to the respective fields
                        Object.entries(err).forEach(([field, message]) => {
                            const errorElement = document.getElementById(field + 'Error');
                            if (errorElement) {
                                errorElement.textContent = message;
                            }
                            console.log(err);
                        });

                        throw new Error("Validation errors occurred");
                    });
                }
                return response.json();
            })
            .then(data => {
                // Handle success
                alert("Playlist created successfully!"); // Simple alert for success
                window.location.href = "/users/home";
            })
            .catch(error => {
                console.error('Error:', error);
                // Handle other types of errors (network issues, etc.)
            });
    });
});

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchResults').addEventListener('click', function(event) {
        let target = event.target;
        let addButton = target.classList.contains('add-song') ? target : target.closest('.add-song');

        if (addButton) {
            const songItem = addButton.closest('div[data-song-id]');

            if (songItem) {
                console.log('Song Title Element:', songItem.querySelector('.song-title')); // Check if the element is found

                const songId = songItem.getAttribute('data-song-id');
                const songTitleElement = songItem.querySelector('.song-title');
                const songArtistElement = songItem.querySelector('.song-artist');

                if (!songTitleElement || !songArtistElement) {
                    console.error('Song title or artist elements not found');
                    return;
                }

                const songTitle = songTitleElement.textContent;
                const songArtist = songArtistElement.textContent;

                addSongToPlaylist(songId, songTitle, songArtist);
                updateSongIdsInput(songId);

            } else {
                console.error('Could not find the song item element');
            }
        }
    });
});

function addSongToPlaylist(songId, songTitle, songArtist) {
    const playlistSongs = document.getElementById('playlistSongs');

    // Create the list item for the song
    const songElement = document.createElement('li');
    songElement.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
    songElement.setAttribute('data-song-id', songId);

    // Create a span for the song text
    const songText = document.createElement('span');
    songText.textContent = `${songTitle} - ${songArtist}`;
    songElement.appendChild(songText);

    // Create the remove button
    const removeButton = document.createElement('button');
    removeButton.setAttribute('type', 'button');
    removeButton.classList.add('btn', 'btn-outline-danger', 'btn-sm', 'remove-song');
    removeButton.innerHTML = '<i class="fas fa-minus" aria-hidden="true"></i>';
    removeButton.onclick = function() {
        removeSongFromPlaylist(songId, songElement);
    };
    songElement.appendChild(removeButton);

    // Append the new song element to the playlist
    playlistSongs.appendChild(songElement);
}

function removeSongFromPlaylist(songId, songElement) {
    // Remove the song element from the playlist
    songElement.remove();

    // Update the hidden input to remove the song ID
    const songIdsInput = document.getElementById('songIds');
    let songIds = songIdsInput.value.split(',');
    songIds = songIds.filter(id => id !== songId);
    songIdsInput.value = songIds.join(',');
}


function updateSongIdsInput(songId) {
    const songIdsInput = document.getElementById('songIds');
    let songIds = songIdsInput.value ? songIdsInput.value.split(',') : [];
    if (!songIds.includes(songId)) {
        songIds.push(songId);
        songIdsInput.value = songIds.join(',');
    }
    console.log(songIds)
}

