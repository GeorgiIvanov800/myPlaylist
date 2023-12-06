package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.view.CommentViewModel;

import java.util.List;

public interface CommentService {
    void create(CommentBindingModel commentBindingModel,  String userEmail);

    List<CommentViewModel> findAllByPlaylistId(Long id);
    void delete(Long id);
}
