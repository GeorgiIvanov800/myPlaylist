package org.myplaylist.myplaylist.model.view;

import java.time.LocalDateTime;

public record CommentViewModel(
        String textContent,
        LocalDateTime createdOn,
        UserViewModel user

) {
}
