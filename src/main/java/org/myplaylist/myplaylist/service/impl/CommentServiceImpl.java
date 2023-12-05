package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.mapper.CommentMapper;
import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.repository.CommentRepository;
import org.myplaylist.myplaylist.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public void create(CommentBindingModel commentBindingModel) {
            CommentEntity comment = commentMapper.commentBindingModelToEntity(commentBindingModel);

            commentRepository.save(comment);
    }

    @Override
    public void delete(Long id) {

    }
}
