// Global variables
let sound = null;
let currentSongIndex = 0;
let playlist = []; // Initialize playlist at the top
let fastForwardInterval;
let rewindInterval;
//Load the playlist in the player on a page load
document.addEventListener('DOMContentLoaded', () => {
    let playlistIdElement = document.getElementById('playlistId');
    let playlistId = playlistIdElement.getAttribute('data-id');
    loadPlaylist(playlistId);
});
// Function to load and play a song
function loadSong(index) {
    let song = playlist[index];
    if (sound) {
        sound.unload();
    }
    sound = new Howl({
        src: [song.url],
        html5: true,
        onplay: function() {
            updateProgress(); // Start updating the progress when the song plays
        },
        onend: function() {
            playNextSong();
        }
    });
    updateNowPlaying(song.title, song.artist, song.formattedDuration);

    document.getElementById('musicPlayer').classList.add('visible');
    sound.play();
}

function playNextSong() {
    currentSongIndex = (currentSongIndex + 1) % playlist.length;
    loadSong(currentSongIndex);
}

// Function to update the Now Playing Display
function updateNowPlaying(title, artist, formattedDuration) {
    document.querySelector('.music-player .info label').textContent = title;
    document.querySelector('.music-player .info small').textContent = artist;
    document.querySelector('.music-player .info span[name="duration"]').textContent = formattedDuration;
}

// Update the progress of the song
function updateProgress() {
    if (sound.playing()) {
        let currentTime = sound.seek() || 0;
        let duration = sound.duration();
        let progress = currentTime / duration;

        // Update the current time display
        document.querySelector('.music-player .info span[name="current"]').textContent = formatTime(currentTime);

        requestAnimationFrame(updateProgress);
    }
}

function formatTime(seconds) {
    let minutes = Math.floor(seconds / 60);
    seconds = Math.floor(seconds % 60);
    return `${minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
}

// Play/Pause Toggle
document.querySelector('.music-player .controls a[href="#play"]').addEventListener('click', function() {
    if (sound) {
        if (!sound.playing()) {
            sound.play();
            this.classList.remove('fa-play');
            this.classList.add('fa-pause');

        } else {
            sound.pause();
            this.classList.remove('fa-pause');
            this.classList.add('fa-play');
        }
    }
});

// Next Song
document.querySelector('.music-player .controls a[href="#forward"]').addEventListener('click', function() {
    currentSongIndex = (currentSongIndex + 1) % playlist.length;
    loadSong(currentSongIndex);
});

// Previous Song
document.querySelector('.music-player .controls a[href="#back"]').addEventListener('click', function() {
    currentSongIndex = (currentSongIndex - 1 + playlist.length) % playlist.length;
    loadSong(currentSongIndex);
});

// Volume control
// Flag to keep track of dragging
let isDraggingVolume = false;

// Function to update volume based on event
function updateVolume(e) {
    const volumeBar = document.querySelector('.music-player .volume-level');
    const rect = volumeBar.getBoundingClientRect();
    const clickPositionX = e.clientX - rect.left; // Click X within the volume bar
    const newVolumeLevel = Math.max(0, Math.min(clickPositionX / volumeBar.offsetWidth, 1)); // Ensure volume is between 0 and 1

    // Set the new volume on the Howler sound object
    if (sound) {
        sound.volume(newVolumeLevel);
    }

    // Update the UI to reflect the new volume
    volumeBar.querySelector('em').style.width = (newVolumeLevel * 100) + '%';
}

// Add event listener for mousedown on the volume bar
document.querySelector('.music-player .volume-level').addEventListener('mousedown', function(e) {
    isDraggingVolume = true;
    updateVolume(e);
});

// Add event listener for mouseup anywhere in the document
document.addEventListener('mouseup', function() {
    isDraggingVolume = false;
});

// Add event listener for mousemove on the volume bar
document.querySelector('.music-player .volume-level').addEventListener('mousemove', function(e) {
    if (isDraggingVolume) {
        updateVolume(e);
    }
});


// while the user is dragging the volume slider
document.addEventListener('mousemove', function(e) {
    if (isDraggingVolume) {
        updateVolume(e);
    }
});

// Mute toggle
document.querySelector('.music-player a[href="#mute"]').addEventListener('click', function(e) {
    e.preventDefault(); // Prevent default behavior
    if (sound) {
        // Toggle the mute state
        sound.mute(!sound.mute());
        // Update the icon based on whether the sound is now muted
        this.classList.toggle('fa-volume-up', !sound.mute());
        this.classList.toggle('fa-volume-off', sound.mute());
    }
});

// Load playlist
function loadPlaylist(playlistId) {

    fetch('/api/playlist/' + playlistId + '/songs', {
    })
        .then(response => {
            console.log('Fetch response:', response);
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
                    url: song.filePath,// Construct the song URL,
                    formattedDuration: song.formattedDuration
                };
            });
            attachClickListeners();
            if (playlist.length > 0) {
                updatePlaylistDisplay(songs); // Update the playlist display
            }
        })
        .catch(error => {
            console.error('Error loading playlist:', error);
            // Handle errors (like showing an alert or a message to the user)
        });
}

// Play button event listener
function playFirstSong() {
    if (playlist.length > 0 && (!sound || !sound.playing())) {
        loadSong(0); // Load and play the first song
    }
}


// Populate playlist display (unchanged)
function populatePlaylistFromDOM() {
    const songElements = document.querySelectorAll('.song-list .song-item');

    playlist = Array.from(songElements).map(el => {
        const title = el.querySelector('span:first-of-type').textContent;
        const artist = el.querySelector('span:nth-of-type(3)').textContent;
        const formattedDuration = el.getAttribute('data-formatted-duration');
        let url = el.getAttribute('data-file-path');

        url = "/" + url;
        return { title, artist, url, formattedDuration };
    });

}

populatePlaylistFromDOM();

// Update playlist display as before (unchanged)
function updatePlaylistDisplay(songs) {

    const playlistElement = document.getElementById('playlistSongs');

    playlistElement.innerHTML = ''; // Clear the existing list

    songs.forEach((song, index) => {
        const songItem = document.createElement('li');
        songItem.textContent = `${song.title} by ${song.artist}`;
        songItem.onclick = () => {
            loadSong(index); // Load and play the song when clicked
        };
        playlistElement.appendChild(songItem);
    });
}

function attachClickListeners() {
    const songItems = document.querySelectorAll('.list-group.song-list-music-room .list-group-item');

    songItems.forEach((item, index) => {
        item.addEventListener('click', () => {
            loadSong(index);
        });
    });
}

// Call this function on a page load or when the playlist is rendered
populatePlaylistFromDOM();

function startFastForward() {
    if (sound && sound.playing()) {
        fastForwardInterval = setInterval(() => {
            let newPosition = sound.seek() + 5; // Move 5 seconds forward
            if (newPosition < sound.duration()) {
                sound.seek(newPosition);
            } else {
                clearInterval(fastForwardInterval); // Stop if at end of song
            }
            updateProgress();
        }, 200);
    }
}

function stopFastForward() {
    clearInterval(fastForwardInterval);
}

function startRewind() {
    if (sound && sound.playing()) {
        rewindInterval = setInterval(() => {
            let newPosition = sound.seek() - 5; // Move 5 seconds backward
            if (newPosition > 0) {
                sound.seek(newPosition);
            } else {
                clearInterval(rewindInterval); // Stop if at start of song
            }
            updateProgress();
        }, 200);
    }
}

function stopRewind() {
    clearInterval(rewindInterval);
}