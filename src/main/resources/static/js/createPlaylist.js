console.log("Hello from My Playlist")
document.addEventListener('DOMContentLoaded', function () {
    let form = document.getElementById('playlistForm');
    console.log("Forn element: ", form);

    form.addEventListener('submit', function(e) {
        console.log("Submit button clicked.");
        e.preventDefault();

        let name = document.getElementById('playlistName').value;
        let description = document.getElementById('playlistDescription').value;
        let songIds = []; // This should be populated with actual song IDs

        // Example of how you might populate songIds with selected songs
        // This is just a placeholder and will depend on your actual implementation
        // document.querySelectorAll('.selected-song').forEach(function(song) {
        //     songIds.push(song.dataset.songId);
        // });

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
            body: JSON.stringify({ name, description, songIds })
        })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Server responded with status ' + response.status);
                }
                return response.json();
            })
            .then(function(data) {
                // Handle successful playlist creation
                console.log('Success:', data);
                // Redirect to the playlist page or display a success message
                // window.location.href = '/path-to-playlist/' + data.id;
            })
            .catch(function(error) {
                // Handle errors
                console.error('Error:', error);
                // Display error message to the user
            });
    });
});
