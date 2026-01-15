package com.example.demo.app.projects;

import com.example.demo.api.dto.project.ProjectRequestDto;
import com.example.demo.api.dto.project.ProjectResponseDto;
import com.example.demo.api.exception.OwnershipException;
import com.example.demo.api.exception.ProjectNotFoundException;
import com.example.demo.api.exception.UserNotFoundException;
import com.example.demo.app.projects.util.ProjectConverter;
import com.example.demo.app.users.UserEntity;
import com.example.demo.app.users.UserRepository;
import com.example.demo.app.users.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto requestDto){
        ProjectEntity project = new ProjectEntity();
        updateProjectData(requestDto, project);

        project.setUser(userService.getCurrentUserEntity());
        ProjectEntity createdProject = projectRepository.saveAndFlush(project);
        return ProjectConverter.convertToResponseDto(createdProject);
    }

    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectConverter::convertToResponseDto)
                .toList();
    }

    public ProjectResponseDto getProjectById(Long projectId){
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(
                        ()-> new ProjectNotFoundException(String.format("Project with id: %d doesn't exist", projectId)));

        return ProjectConverter.convertToResponseDto(project);
    }

    public List<ProjectResponseDto> getProjectByUserId(Long userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User with id: %d doesn't exist", userId));
        List<ProjectEntity> projects = projectRepository.getProjectEntitiesByUserId(userId);

        return projects.stream().map(ProjectConverter::convertToResponseDto).toList();
    }

    public List<ProjectResponseDto> getCurrentUserProjects() {
        return getProjectByUserId(userService.getCurrentUserEntity().getId());
    }


    @Transactional
    public ProjectResponseDto updateProject(ProjectRequestDto requestDto, Long projectId){

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new ProjectNotFoundException(String.format("Project with id: %d doesn't exist", projectId)));

        validateProjectOwnership(project);

        updateProjectData(requestDto, project);

        ProjectEntity updatedProject = projectRepository.saveAndFlush(project);
        return ProjectConverter.convertToResponseDto(updatedProject);
    }

    public void deleteProjectById(Long projectId){
        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(
                        ()-> new ProjectNotFoundException(String.format("Project with id: %d doesn't exist", projectId)));
        validateProjectOwnership(project);

        projectRepository.deleteById(project.getId());
    }

    public void validateProjectOwnership(ProjectEntity project){
        UserEntity currentUser = userService.getCurrentUserEntity();
        if(!(currentUser.getId()).equals(project.getUser().getId()))
            throw new OwnershipException();
    }

    private void updateProjectData(ProjectRequestDto requestDto, ProjectEntity project) {
        project.setName(requestDto.getName());
        project.setDescription(requestDto.getDescription());
    }
}
