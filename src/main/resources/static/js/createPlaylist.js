//Event Listener on the Playlist Form
document.addEventListener('DOMContentLoaded', function () {
    let form = document.getElementById('playlistForm');
    console.log("Form element: ", form);
    //Submit button
    form.addEventListener('submit', function(e) {

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
                // Simple alert for success
                alert("Playlist created successfully!");
                window.location.href = "/users/dashboard";
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const searchResults = document.getElementById('searchResults');

    if (searchResults) {
        searchResults.addEventListener('click', function(event) {
            let target = event.target;
            let addButton = target.classList.contains('add-song') ? target : target.closest('.add-song');

            if (addButton) {
                const songItem = addButton.closest('li[data-song-id]');

                if (songItem) {
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
    } else {
        console.error('searchResults element not found');
    }
});

//Adding songs into the playlist
const addedSongIds = new Set();
function addSongToPlaylist(songId, songTitle, songArtist) {
    const playlistSongs = document.getElementById('playlistSongs');
    const li = document.createElement('li');
    li.setAttribute('data-song-id', songId);
    li.className = 'list-group-item d-flex justify-content-between align-items-center';
    li.innerHTML = `
        <div>
            <h6 class="song-title mb-1">${songTitle}</h6>
            <small class="text-muted song-artist">${songArtist}</small>
        </div>
        <button type="button" class="btn btn-outline-danger btn-sm remove-song">
            <i class="fas fa-minus"></i> Remove
        </button>
    `;
    playlistSongs.appendChild(li);
    //Disable the add button when songs is added to the playlist
    const addButton = document.querySelector(`li[data-song-id="${songId}"] .add-song`);
    //Add song id to the set to keep track which songs are added
    addedSongIds.add(songId);
    if (addButton) {
        addButton.disabled = true;
    }
}

document.getElementById('playlistSongs').addEventListener('click', function(event) {
    if (event.target.classList.contains('remove-song') || event.target.closest('.remove-song')) {
        const songItem = event.target.closest('li[data-song-id]');
        if (songItem) {
            removeSongFromPlaylist(songItem);
        }
    }
});

function removeSongFromPlaylist(songItem) {
    //Get the song ID
    const songId = songItem.getAttribute('data-song-id');

    // Removes the song item from the list
    if (songItem) {
        songItem.remove();
    }
    // Remove the song ID from the Set
    addedSongIds.delete(songId);

    // Find the Add button for this song and re-enable it
    const addButton = document.querySelector(`li[data-song-id="${songId}"] .add-song`);
    if (addButton) {
        addButton.disabled = false;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // Get the search input box by its ID
    let searchInput = document.getElementById('songSearch');
    console.log("Hello form Search")
    if (!searchInput) {
        console.error('Search input not found');
        return;
    }

    //event listener to trigger when the user types in the search box
    searchInput.addEventListener('keyup', function () {

        // Get the current value of the input box, convert it to lowercase for case-insensitive search
        console.log('Key up event triggered'); // For debugging

        let searchQuery = searchInput.value.toLowerCase();
        console.log('Search Query:', searchQuery); // For debugging

        // Select all song items by their class
        let songItems = document.querySelectorAll('.song-item');

        console.log('Number of song items found:', songItems.length); // For debugging

        if (!songItems.length) {
            console.error('No song items found');
        }
        // Iterate over each song item
        songItems.forEach(function (item) {
            // Get the text content of the song title and artist, convert to lowercase
            let title = item.querySelector('.song-title').textContent.toLowerCase();
            let artist = item.querySelector('.song-artist').textContent.toLowerCase();

            // Check if the title or artist includes the search query
            if (title.includes(searchQuery) || artist.includes(searchQuery)) {
                // If the item matches the query, display it
                item.style.display = '';
            } else {
                // If the item does not match the query, hide it
                item.style.display = 'none';
            }
        });
    });
});

