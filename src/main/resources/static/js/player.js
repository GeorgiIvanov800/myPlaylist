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
    sound.play();
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

// Call this function on page load or when the playlist is rendered
populatePlaylistFromDOM();
