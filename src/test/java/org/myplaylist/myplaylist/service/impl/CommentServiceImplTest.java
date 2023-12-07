package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.config.mapper.CommentMapper;
import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.view.CommentViewModel;
import org.myplaylist.myplaylist.repository.CommentRepository;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.ReportRepository;
import org.myplaylist.myplaylist.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    private CommentServiceImpl serviceToTest;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private PlaylistRepository mockPlaylistRepository;
    @Mock
    private ReportRepository mockReportRepository;
    @Mock
    private CommentMapper mockCommentMapper;

    @BeforeEach
    void setUp() {
        serviceToTest = new CommentServiceImpl(
                mockCommentRepository,
                mockUserRepository,
                mockPlaylistRepository,
                mockReportRepository,
                mockCommentMapper
        );
    }

    @Test
    void shouldCreateComment() {
        // Arrange
        String userEmail = "test@example.com";
        Long playlistId = 1L;
        CommentBindingModel commentBindingModel = new CommentBindingModel();
        commentBindingModel.setPlaylistId(playlistId);
        commentBindingModel.setTextContent("Test Comment");

        UserEntity user = new UserEntity();
        user.setEmail(userEmail);
        PlaylistEntity playlist = new PlaylistEntity();

        when(mockUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));

        //Act
        serviceToTest.create(commentBindingModel, userEmail);

        //Assert
        verify(mockUserRepository).findByEmail(userEmail);
        verify(mockPlaylistRepository).findById(playlistId);
        verify(mockCommentRepository).save(any(CommentEntity.class));

    }



}
