package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.CommentBindingModel;

public interface CommentService {
    void create(CommentBindingModel commentBindingModel);

    void delete(Long id);
}
