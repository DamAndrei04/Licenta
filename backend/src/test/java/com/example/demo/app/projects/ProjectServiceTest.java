package com.example.demo.app.projects;

import com.example.demo.api.dto.project.ProjectRequestDto;
import com.example.demo.api.dto.project.ProjectResponseDto;
import com.example.demo.api.exception.OwnershipException;
import com.example.demo.api.exception.ProjectNotFoundException;
import com.example.demo.app.users.UserEntity;
import com.example.demo.app.users.UserRepository;
import com.example.demo.app.users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private UserEntity user(Long id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }

    private ProjectEntity project(Long id, UserEntity owner) {
        ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setName("Sample");
        project.setDescription("desc");
        project.setUser(owner);
        return project;
    }

    @Test
    void createProject_savesAndReturnsResponseDto() {
        UserEntity currentUser = user(1L);
        when(userService.getCurrentUserEntity()).thenReturn(currentUser);
        when(projectRepository.saveAndFlush(any(ProjectEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectRequestDto request = ProjectRequestDto.builder()
                .name("My Project")
                .description("A description")
                .build();

        ProjectResponseDto result = projectService.createProject(request);

        assertEquals("My Project", result.getName());
        assertEquals("A description", result.getDescription());
        assertEquals(1L, result.getUserId());
        verify(projectRepository).saveAndFlush(any(ProjectEntity.class));
    }

    @Test
    void getProjectById_whenExists_returnsDto() {
        ProjectEntity project = project(5L, user(1L));
        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));

        ProjectResponseDto result = projectService.getProjectById(5L);

        assertEquals(5L, result.getId());
        assertEquals("Sample", result.getName());
    }

    @Test
    void getProjectById_whenMissing_throwsNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(99L));
    }

    @Test
    void deleteProjectById_whenOwner_deletes() {
        ProjectEntity project = project(5L, user(1L));
        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));
        when(userService.getCurrentUserEntity()).thenReturn(user(1L));

        projectService.deleteProjectById(5L);

        verify(projectRepository).deleteById(5L);
    }

    @Test
    void deleteProjectById_whenNotOwner_throwsOwnership() {
        ProjectEntity project = project(5L, user(1L));
        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));
        when(userService.getCurrentUserEntity()).thenReturn(user(2L));

        assertThrows(OwnershipException.class, () -> projectService.deleteProjectById(5L));
        verify(projectRepository, never()).deleteById(any());
    }
}
