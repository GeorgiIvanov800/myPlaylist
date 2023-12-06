package org.myplaylist.myplaylist.model.view;

import java.time.LocalDateTime;

public record CommentViewModel(
        Long id,
        String textContent,
        LocalDateTime createdOn,
        UserViewModel user

) {
}
