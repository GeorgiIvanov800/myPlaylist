package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.mapper.CommentMapper;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.view.CommentViewModel;
import org.myplaylist.myplaylist.repository.CommentRepository;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    private final CommentMapper commentMapper;


    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PlaylistRepository playlistRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public void create(CommentBindingModel commentBindingModel, String userEmail) {
        UserEntity userId = userRepository.findByEmail(userEmail)
                .orElseThrow( () -> new ObjectNotFoundException("User not found"));

        PlaylistEntity playlistId = playlistRepository.findById(commentBindingModel.getPlaylistId())
                .orElseThrow( () -> new ObjectNotFoundException("Playlist not found"));

        CommentEntity comment = new CommentEntity();
        comment.setCreatedOn(LocalDateTime.now());
        comment.setUser(userId);
        comment.setPlaylist(playlistId);
        comment.setTextContent(commentBindingModel.getTextContent());

        commentRepository.save(comment);
    }

    @Override
    public List<CommentViewModel> findAllByPlaylistId(Long id) {
    return commentRepository.findAllByPlaylistId(id)
            .stream()
            .map(commentMapper::commentEntityToViewModel)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {

    }
}
