package org.myplaylist.myplaylist.service.impl;

import jakarta.transaction.Transactional;
import org.myplaylist.myplaylist.config.mapper.CommentMapper;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.entity.*;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.view.CommentViewModel;
import org.myplaylist.myplaylist.repository.CommentRepository;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.ReportRepository;
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
    private final ReportRepository reportRepository;
    private final CommentMapper commentMapper;


    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              PlaylistRepository playlistRepository,
                              ReportRepository reportRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.reportRepository = reportRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public void create(CommentBindingModel commentBindingModel, String userEmail) {
        UserEntity userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        PlaylistEntity playlistId = playlistRepository.findById(commentBindingModel.getPlaylistId())
                .orElseThrow(() -> new ObjectNotFoundException("Playlist not found"));

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
    @Transactional
    public void deleteCommentAndReport(Long id) {

        //get all reports for this comment
        List<ReportEntity> reportsToDelete = reportRepository.findAllByCommentEntity_Id(id);
        //delete the reports
        reportRepository.deleteAll(reportsToDelete);
        //delete the comment
        commentRepository.deleteById(id);
    }

    @Override
    public boolean isAdmin(String email) {
        return isAdmin(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new ObjectNotFoundException("User with email: " + email + " cannot be found"))
        );
    }

    private boolean isAdmin(UserEntity userEntity) {

        return userEntity
                .getRoles()
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(r -> UserRoleEnum.ADMIN == r);

    }
}
