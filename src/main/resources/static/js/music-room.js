function ratePlaylist(playlistId, ratingType) {

    let csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    let csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    let csrfToken = csrfTokenMeta.getAttribute('content');
    let csrfHeader = csrfHeaderMeta.getAttribute('content');

    console.log(document.getElementById(`likeButton_${playlistId}`)); // For debugging
    console.log(document.getElementById(`dislikeButton_${playlistId}`)); // For debugging

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
    console.log(`Updating counts for Playlist ID ${playlistId}`); // Debugging
    const likeCountElement = document.getElementById(`likeCount_${playlistId}`);
    const dislikeCountElement = document.getElementById(`dislikeCount_${playlistId}`);

    if (likeCountElement && dislikeCountElement) {
        likeCountElement.textContent = likeCount;
        dislikeCountElement.textContent = dislikeCount;
    } else {
        console.error('Could not find elements to update');
    }
}