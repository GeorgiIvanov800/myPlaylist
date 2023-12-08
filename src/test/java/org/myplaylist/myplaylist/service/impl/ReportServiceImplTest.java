package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.ReportBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.entity.ReportEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.repository.CommentRepository;
import org.myplaylist.myplaylist.repository.ReportRepository;
import org.myplaylist.myplaylist.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {
    private ReportServiceImpl serviceToTest;
    @Mock
    private ReportRepository mockReportRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private UserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        serviceToTest = new ReportServiceImpl(
                mockReportRepository,
                mockCommentRepository,
                mockUserRepository
        );
    }

    @Test
    void createReport_SuccessfulCreation() {
        // Arrange
        ReportBindingModel reportBindingModel = new ReportBindingModel();
        reportBindingModel.setCommentId(1L);
        reportBindingModel.setDescription("Test Description");
        reportBindingModel.setReason("Test Reason");

        String userEmail = "user@example.com";
        CommentEntity comment = new CommentEntity();
        UserEntity user = new UserEntity();
        user.setEmail(userEmail);

        when(mockCommentRepository.findById(reportBindingModel.getCommentId())).thenReturn(Optional.of(comment));
        when(mockUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Act
        serviceToTest.createReport(reportBindingModel, userEmail);

        // Assert
        ArgumentCaptor<ReportEntity> reportCaptor = ArgumentCaptor.forClass(ReportEntity.class);
        verify(mockReportRepository).save(reportCaptor.capture());
        ReportEntity savedReport = reportCaptor.getValue();

        assertNotNull(savedReport);
        assertEquals(reportBindingModel.getDescription(), savedReport.getDescription());
        assertEquals(reportBindingModel.getReason(), savedReport.getReason());
        assertEquals(comment, savedReport.getCommentEntity());
        assertEquals(user, savedReport.getReportedBy());

    }


    @Test
    void createReport_CommentNotFound_ThrowsException() {
        // Arrange
        ReportBindingModel reportBindingModel = new ReportBindingModel();
        reportBindingModel.setCommentId(1L); // Non-existent comment ID
        String userEmail = "user@example.com";

        when(mockCommentRepository.findById(reportBindingModel.getCommentId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ObjectNotFoundException.class, () -> {
            // Act
            serviceToTest.createReport(reportBindingModel, userEmail);
        });
    }

    @Test
    void createReport_UserNotFound_ThrowsException() {
        // Arrange
        ReportBindingModel reportBindingModel = new ReportBindingModel();
        reportBindingModel.setCommentId(1L);
        String userEmail = "nonexistent@example.com"; // Non-existent user email

        when(mockCommentRepository.findById(reportBindingModel.getCommentId())).thenReturn(Optional.of(new CommentEntity()));
        when(mockUserRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ObjectNotFoundException.class, () -> {
            // Act
            serviceToTest.createReport(reportBindingModel, userEmail);
        });
    }

    @Test
    void hasUserAlreadyReportedComment_Exists_ReturnsTrue() {
        // Arrange
        Long commentId = 1L;
        String userEmail = "user@example.com";

        when(mockReportRepository.existsByCommentEntity_IdAndReportedBy_Email(commentId, userEmail)).thenReturn(true);

        // Act
        boolean result = serviceToTest.hasUserAlreadyReportedComment(commentId, userEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasUserAlreadyReportedComment_NotExists_ReturnsFalse() {
        // Arrange
        Long commentId = 1L;
        String userEmail = "user@example.com";

        when(mockReportRepository.existsByCommentEntity_IdAndReportedBy_Email(commentId, userEmail)).thenReturn(false);

        // Act
        boolean result = serviceToTest.hasUserAlreadyReportedComment(commentId, userEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    void allReports_ReturnsAllReports() {
        // Arrange
        List<ReportEntity> expectedReports = Arrays.asList(
                createTestReport(),
                createTestReport()
        );
        when(mockReportRepository.findAll()).thenReturn(expectedReports);

        // Act
        List<ReportEntity> result = serviceToTest.allReports();

        // Assert
        assertNotNull(result);
        assertEquals(expectedReports.size(), result.size());
        assertEquals(expectedReports, result);


        verify(mockReportRepository).findAll();
    }

    @Test
    void deleteReport_DeletesReportWithGivenId() {
        // Arrange
        Long reportId = 1L;

        // Act
        serviceToTest.deleteReport(reportId);

        // Assert
        verify(mockReportRepository).deleteById(reportId);
    }

    @Test
    void testIsAdmin_UserIsAdmin() {
        // Arrange
        String email = "testuser@example.com";
        UserEntity user = createTestUser();


        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean result = serviceToTest.isAdmin(email);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAdmin_UserIsNotAdmin() {
        // Arrange
        String email = "testuser@example.com";
        UserEntity user = createTestNoAdminUser();

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean result = serviceToTest.isAdmin(email);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsAdmin_UserNotFound() {
        // Arrange
        String email = "test@example.com";

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> serviceToTest.isAdmin(email));
    }

    private CommentEntity createTestComment() {
        CommentEntity comment = new CommentEntity();
        comment.setTextContent("test");
        comment.setCreatedOn(LocalDateTime.now());
        comment.setUser(createTestUser());
        return comment;
    }
    private ReportEntity createTestReport() {
        ReportEntity report = new ReportEntity();
        report.setDescription("Test Description");
        report.setReason("Test Reason");
        report.setReportedOn(LocalDateTime.now());
        report.setCommentEntity(createTestComment());
        report.setReportedBy(createTestUser());
        return report;
    }

    private static UserEntity createTestUser() {
        UserEntity testUser = new UserEntity();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("StrongPassword123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setUsername("testUser");
        testUser.setRegisterDate(LocalDateTime.now());
        testUser.setActive(false);
        List<UserRoleEntity> roles = new ArrayList<>();
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.USER));
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.ADMIN));
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.MODERATOR));
        testUser.setRoles(roles);
        return testUser;
    }

    private static UserEntity createTestNoAdminUser() {
        UserEntity testUser = new UserEntity();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("StrongPassword123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setUsername("testUser");
        testUser.setRegisterDate(LocalDateTime.now());
        testUser.setActive(false);
        List<UserRoleEntity> roles = new ArrayList<>();
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.USER));
        testUser.setRoles(roles);
        return testUser;
    }
}
