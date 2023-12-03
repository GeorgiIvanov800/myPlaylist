//Event Listener on the Playlist Form
document.addEventListener('DOMContentLoaded', function () {
    let form = document.getElementById('playlistForm');

    let pathSegments = window.location.pathname.split('/');
    let playlistId = pathSegments[pathSegments.length - 1];
    console.log(playlistId);
    if (playlistId && !isNaN(playlistId)) {
        editPlaylist(playlistId);
    }
    // Clear any previous error messages and remove the error class before submitting
    document.querySelectorAll('.playlist-form-error').forEach(el => {
        el.textContent = '';
        el.classList.remove('playlist-form-error');
    });


    //Submit button
    form.addEventListener('submit', function (e) {

        e.preventDefault();
        let playlistId = document.getElementById('playlistId').value;
        let url = playlistId ? '/api/playlist/update/' + playlistId : '/api/playlist';
        // Use PUT for update and POST for create
        let method = playlistId ? 'PUT' : 'POST';


        let name = document.getElementById('playlistName').value;
        let description = document.getElementById('playlistDescription').value;
        let songIds = [];
        let genre = document.getElementById('playlistGenre').value;

        songIds = Array.from(document.querySelectorAll('#playlistSongs li'))
            .map(li => li.dataset.songId);


        let csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
        let csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfTokenMeta || !csrfHeaderMeta) {
            // console.error('CSRF token or header not found in the HTML meta tags');
            return; // Exit the function if the meta-tags are not found
        }

        let csrfToken = csrfTokenMeta.getAttribute('content');
        let csrfHeader = csrfHeaderMeta.getAttribute('content');


        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({name, description, songIds, genre})
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        // Clear any previous error messages
                        document.querySelectorAll('.playlist-form-error').forEach(el => {
                            el.textContent = '';
                            el.classList.remove('playlist-form-error'); // Clear specific class
                        });
                        // Display new error messages next to the respective fields
                        Object.entries(err).forEach(([field, message]) => {
                            const errorElement = document.getElementById(field + 'Error');
                            if (errorElement) {
                                errorElement.textContent = message;
                                errorElement.classList.add('playlist-form-error'); // Add specific class
                            }
                        });
                        console.log(method);
                        throw new Error("Validation errors occurred");
                    });
                }
                return response.json();
            })
            .then(data => {
                // Simple alert for success
                if (method === 'POST') {
                    alert("Playlist created successfully!");
                } else {
                    alert("Playlist updated successfully!");
                }
                window.location.href = "/users/dashboard";
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});
//Edit Playlist
function editPlaylist(playlistId) {
    fetch('/api/playlist/' + playlistId)
        .then(response => response.json())
        .then(data => {
            document.getElementById('playlistId').value = playlistId;
            document.getElementById('playlistName').value = data.name;
            document.getElementById('playlistDescription').value = data.description;
            document.getElementById('playlistGenre').value = data.genre;

            // Change the heading and button text for edit mode
            document.querySelector('h2.text-center').textContent = 'Edit Your Playlist';
            document.getElementById('playlistSubmitButton').textContent = 'Update Playlist';
            document.querySelectorAll('.delete-song').forEach(button => button.style.display = 'none');
            document.getElementById('uploadForm').remove();
            // Clear existing songs and populate with new ones
            let playlistSongsElement = document.getElementById('playlistSongs');
            playlistSongsElement.innerHTML = ''; // Clear existing songs
            if (data.songs && Array.isArray(data.songs)) {
                data.songs.forEach(song => {
                    addSongToPlaylist(song.id, song.title, song.artist);
                });
            }
        })
        .catch(error => console.error('Error:', error));
}




//Add Button
document.addEventListener('DOMContentLoaded', function () {
    const searchResults = document.getElementById('addSongs');

    if (searchResults) {
        searchResults.addEventListener('click', function (event) {
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

    if (songTitle === null) {
        songTitle = '';
    }

    const playlistSongs = document.getElementById('playlistSongs');
    const li = document.createElement('li');
    console.log(`Adding song with ID: ${songId}`);

    li.setAttribute('data-song-id', songId);
    li.className = 'list-group-item d-flex justify-content-between align-items-center';
    li.innerHTML = `
        <div>
            <h6 class="song-title mb-1">${songTitle}</h6>
            <small class="text-muted song-artist">${songArtist}</small>
        </div>
        <button type="button" class="btn btn-outline-danger btn-sm remove-song">
            <i class="fas fa-minus"></i>
        </button>
    `;
    playlistSongs.appendChild(li);
    //Disable the added button when songs are added to the playlist
    const addButton = document.querySelector(`li[data-song-id="${songId}"] .add-song`);
    console.log("Targeted add button:", addButton);
    //Add song id to the set to keep track which songs are added
    addedSongIds.add(songId);
    if (addButton) {
        addButton.disabled = true;
    }
}

document.getElementById('playlistSongs').addEventListener('click', function (event) {
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
    if (!searchInput) {
        console.error('Search input not found');
        return;
    }

    //Event listener to trigger when the user types in the search box
    searchInput.addEventListener('keyup', function () {

        // Get the current value of the input box, convert it to lowercase for case-insensitive search
        console.log('Key up event triggered'); // For debugging

        let searchQuery = searchInput.value.toLowerCase();
        console.log('Search Query:', searchQuery); // For debugging

        // Select all song items by their class
        let songItems = document.querySelectorAll('.song-item');

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
                // If the item matches the query, display it, Used custom CSS with .d-flex from bootstrap was not working
                item.classList.remove('hidden-item'); // Show the item
            } else {
                // If the item does not match the query, hide it
                item.classList.add('hidden-item'); // Hide the item
            }
        });
    });
});
//Check how many files the user want to upload and that they are all in mp3 format
document.getElementById('uploadForm').addEventListener('submit', function (event) {
    let files = document.getElementById('fileInput').files;
    let errorElement = document.getElementById('fileError');
    let isValid = true;
    errorElement.textContent = '';
    errorElement.classList.remove('error-message');

    // Check the number of files
    if (files.length > 5) {
        errorElement.textContent = 'You can only upload up to 5 songs.';
        errorElement.classList.add('upload-form-error');
        isValid = false
    } else if (files.length === 0) {
        errorElement.textContent = 'No files were selected for upload.';
        errorElement.classList.add('upload-form-error');
        isValid = false;
    } else {
        // Check each file's type
        for (let i = 0; i < files.length; i++) {
            if (files[i].type !== 'audio/mpeg') {
                errorElement.textContent = 'Only MP3 files are allowed.';
                errorElement.classList.add('upload-form-error');
                isValid = false;
                break;
            }
        }

    }
    // Show the spinner only if all validation checks passed
    if (isValid) {
        document.getElementById('loadingOverlay').style.display = 'flex';
    } else {
        event.preventDefault(); // Prevent form submission if validation fails
    }
});



document.getElementById('fileInput').addEventListener('change', function(event) {
    let files = event.target.files;
    let fileList = document.getElementById('fileList');
    // Clear the list first
    fileList.innerHTML = '';

    // Create a list of selected file names
    let list = document.createElement('ul');
    for (let i = 0; i < files.length; i++) {
        let li = document.createElement('li');
        li.textContent = files[i].name;
        list.appendChild(li);
    }

    fileList.appendChild(list);
});
//Delete confirmation
function confirmDeletion() {
    return confirm('Are you sure you want to delete this song?');
}

function showSpinner() {
    document.getElementById('loadingOverlay').style.display = 'flex';
}
