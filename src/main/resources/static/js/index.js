function ratePlaylist(playlistId, ratingType) {

    console.log("hello from index");

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