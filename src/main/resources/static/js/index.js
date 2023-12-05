function ratePlaylist(playlistId, ratingType) {

    let csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    let csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    let csrfToken = csrfTokenMeta.getAttribute('content');
    let csrfHeader = csrfHeaderMeta.getAttribute('content');

    console.log(document.getElementById(`likeCount_${playlistId}`)); // For debugging
    console.log(document.getElementById(`dislikeCount_${playlistId}`)); // For debugging

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

// document.addEventListener('DOMContentLoaded', function () {
//     document.querySelectorAll('.show-more').forEach(function (link) {
//         link.addEventListener('click', function (event) {
//             event.preventDefault();
//
//             // Get the ID of the song list associated with this link
//             let songListId = this.getAttribute('data-song-list');
//             let songList = document.querySelector(songListId);
//             let hiddenItems = songList.querySelectorAll('.hidden');
//             let separators = songList.querySelectorAll('.song-separator');
//
//             if (this.textContent.includes('more')) {
//                 // Show all hidden songs in this playlist
//                 hiddenItems.forEach(item => item.classList.remove('hidden'));
//
//                 // Change link text to "Show Less"
//                 this.textContent = '... and less';
//
//                 // Show separators, except the last one
//                 separators.forEach(separator => separator.style.display = 'block');
//                 if (separators.length > 0) {
//                     separators[separators.length - 1].style.display = 'none';
//                 }
//             } else {
//                 // Hide songs again in this playlist
//                 hiddenItems.forEach(item => item.classList.add('hidden'));
//
//                 // Change link text to "Show More"
//                 this.textContent = '... and more';
//
//                 // Hide all separators
//                 separators.forEach(separator => separator.style.display = 'none');
//             }
//         });
//     });
//
//     // Initially hide the last separator in each song list
//     document.querySelectorAll('.song-list').forEach(function (songList) {
//         let separators = songList.querySelectorAll('.song-separator');
//         if (separators.length > 0) {
//             separators[separators.length - 1].style.display = 'none';
//         }
//     });
// });
