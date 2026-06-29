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

/**
 * Serviciu care implementează logica de afaceri pentru gestionarea proiectelor.
 * Se ocupă de operațiile CRUD asupra proiectelor și de verificarea dreptului de
 * proprietate al utilizatorului autenticat asupra acestora.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Creează un proiect nou pentru utilizatorul curent autenticat.
     *
     * @param requestDto datele proiectului care trebuie creat (nume și descriere)
     * @return reprezentarea (DTO) a proiectului nou creat și salvat în baza de date
     */
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto requestDto){
        ProjectEntity project = new ProjectEntity();
        updateProjectData(requestDto, project);

        project.setUser(userService.getCurrentUserEntity());
        ProjectEntity createdProject = projectRepository.saveAndFlush(project);
        return ProjectConverter.convertToResponseDto(createdProject);
    }

    /**
     * Returnează toate proiectele existente în baza de date.
     *
     * @return lista tuturor proiectelor sub formă de DTO-uri de răspuns
     */
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectConverter::convertToResponseDto)
                .toList();
    }

    /**
     * Caută un proiect după identificatorul său.
     *
     * @param projectId identificatorul unic al proiectului căutat
     * @return DTO-ul de răspuns corespunzător proiectului găsit
     * @throws ProjectNotFoundException dacă nu există niciun proiect cu acest id
     */
    public ProjectResponseDto getProjectById(Long projectId){
        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(
                        ()-> new ProjectNotFoundException(String.format("Project with id: %d doesn't exist", projectId)));

        return ProjectConverter.convertToResponseDto(project);
    }

    /**
     * Returnează toate proiectele care aparțin unui anumit utilizator.
     *
     * @param userId identificatorul utilizatorului ale cărui proiecte se caută
     * @return lista proiectelor utilizatorului sub formă de DTO-uri de răspuns
     * @throws UserNotFoundException dacă nu există niciun utilizator cu acest id
     */
    public List<ProjectResponseDto> getProjectsByUserId(Long userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User with id: %d doesn't exist", userId));
        List<ProjectEntity> projects = projectRepository.findByUser_Id(userId);

        return projects.stream().map(ProjectConverter::convertToResponseDto).toList();
    }

    /**
     * Returnează proiectele utilizatorului curent autenticat.
     *
     * @return lista proiectelor utilizatorului autenticat sub formă de DTO-uri
     */
    public List<ProjectResponseDto> getCurrentUserProjects() {
        return getProjectsByUserId(userService.getCurrentUserEntity().getId());
    }


    /**
     * Actualizează datele unui proiect existent, după verificarea dreptului de
     * proprietate al utilizatorului curent asupra acestuia.
     *
     * @param requestDto noile date ale proiectului (nume și descriere)
     * @param projectId identificatorul proiectului care trebuie actualizat
     * @return DTO-ul de răspuns corespunzător proiectului actualizat
     * @throws ProjectNotFoundException dacă nu există niciun proiect cu acest id
     * @throws OwnershipException dacă proiectul nu aparține utilizatorului curent
     */
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

    /**
     * Șterge un proiect după identificatorul său, după verificarea dreptului de
     * proprietate al utilizatorului curent asupra acestuia.
     *
     * @param projectId identificatorul proiectului care trebuie șters
     * @throws ProjectNotFoundException dacă nu există niciun proiect cu acest id
     * @throws OwnershipException dacă proiectul nu aparține utilizatorului curent
     */
    public void deleteProjectById(Long projectId){
        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(
                        ()-> new ProjectNotFoundException(String.format("Project with id: %d doesn't exist", projectId)));
        validateProjectOwnership(project);

        projectRepository.deleteById(project.getId());
    }

    /**
     * Verifică dacă proiectul dat aparține utilizatorului curent autenticat.
     *
     * @param project entitatea proiect a cărei proprietate se verifică
     * @throws OwnershipException dacă proiectul nu aparține utilizatorului curent
     */
    public void validateProjectOwnership(ProjectEntity project){
        UserEntity currentUser = userService.getCurrentUserEntity();
        if(!(currentUser.getId()).equals(project.getUser().getId()))
            throw new OwnershipException();
    }

    /**
     * Copiază datele din DTO-ul de cerere în entitatea proiect (nume și descriere).
     *
     * @param requestDto sursa datelor (DTO-ul de cerere)
     * @param project entitatea proiect care va fi actualizată
     */
    private void updateProjectData(ProjectRequestDto requestDto, ProjectEntity project) {
        project.setName(requestDto.getName());
        project.setDescription(requestDto.getDescription());
    }
}