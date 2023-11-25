// Global variables
let sound = null;
let currentSongIndex = 0;
let playlist = []; // Initialize playlist at the top

// Function to load a song
function loadSong(index) {
    let song = playlist[index];
    if (sound) {
        sound.unload();
    }
    sound = new Howl({
        src: [song.url],
        html5: true
    });
    updateNowPlaying(song.title, song.artist);
}

// Function to update the Now Playing Display
function updateNowPlaying(title, artist) {
    document.getElementById("nowPlaying").textContent = title + " by " + artist;
}

// Control Buttons event listeners
document.getElementById("playBtn").addEventListener("click", function() {
    if (sound) {
        if (sound.playing()) {
            sound.pause();
        } else {
            sound.play();
        }
    }
});

document.getElementById("pauseBtn").addEventListener("click", function() {
    sound.pause();
});

document.getElementById("nextBtn").addEventListener("click", function() {
    currentSongIndex = (currentSongIndex + 1) % playlist.length;
    loadSong(currentSongIndex);
    sound.play();
});

document.getElementById("prevBtn").addEventListener("click", function() {
    currentSongIndex = (currentSongIndex - 1 + playlist.length) % playlist.length;
    loadSong(currentSongIndex);
    sound.play();
});

// Function to populate playlist from the DOM
function populatePlaylistFromDOM() {
    const songElements = document.querySelectorAll('.song-list .song-item');
    console.log("Found song elements:", songElements.length);

    playlist = Array.from(songElements).map(el => {
        const title = el.querySelector('span:first-of-type').textContent;
        const artist = el.querySelector('span:nth-of-type(3)').textContent;
        let url = el.getAttribute('data-file-path');

        console.log("Song data:", { title, artist, url });
        url = "/" + url;
        return { title, artist, url };
    });

    if (playlist.length > 0) {
        console.log("Playlist populated:", playlist);
        loadSong(0); // Load the first song after populating the playlist
    } else {
        console.log("No songs found in the playlist.");
    }
}

// Call this function on a page load or when the playlist is rendered
populatePlaylistFromDOM();

function loadPlaylist(playlistId) {

    fetch('/api/playlist/' + playlistId + '/songs', {
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(songs => {
            playlist = songs.map(song => {
                return {
                    title: song.title,
                    artist: song.artist,
                    url: '/' + song.filePath // Construct the song URL
                };
            });

            if (playlist.length > 0) {
                loadSong(0); // Load the first song from the new playlist
                updatePlaylistDisplay(songs); // Update the playlist display
            }
        })
        .catch(error => {
            console.error('Error loading playlist:', error);
            // Handle errors (like showing an alert or a message to the user)
        });
}

function updatePlaylistDisplay(songs) {
    const playlistElement = document.getElementById('playlistSongs');
    playlistElement.innerHTML = ''; // Clear the existing list

    songs.forEach((song, index) => {
        const songItem = document.createElement('li');
        songItem.textContent = `${song.title} by ${song.artist}`;
        songItem.onclick = () => {
            loadSong(index); // Load and play the song when clicked
            highlightCurrentSong(index); // Optional, to highlight the playing song
        };
        playlistElement.appendChild(songItem);
    });
}

function highlightCurrentSong(index) {
    const songs = document.querySelectorAll('#playlistSongs li');
    songs.forEach((li, liIndex) => {
        if (index === liIndex) {
            li.classList.add('current-song');
        } else {
            li.classList.remove('current-song');
        }
    });
}

