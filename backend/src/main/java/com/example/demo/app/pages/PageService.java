package com.example.demo.app.pages;

import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
import com.example.demo.api.exception.OwnershipException;
import com.example.demo.api.exception.PageNotFoundException;
import com.example.demo.api.exception.ProjectNotFoundException;
import com.example.demo.app.components.ComponentEntity;
import com.example.demo.app.pages.util.PageConverter;
import com.example.demo.app.projects.ProjectEntity;
import com.example.demo.app.projects.ProjectRepository;
import com.example.demo.app.projects.ProjectService;
import com.example.demo.app.users.UserEntity;
import com.example.demo.app.users.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu care implementează logica de afaceri pentru gestionarea paginilor unui
 * proiect. Acoperă operațiile CRUD asupra paginilor și verificarea dreptului de
 * proprietate, fiecare pagină aparținând unui proiect.
 */
@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    /**
     * Creează o pagină nouă în cadrul unui proiect, după verificarea dreptului de
     * proprietate al utilizatorului curent asupra proiectului.
     *
     * @param requestDto datele paginii care trebuie creată (nume și rută)
     * @param projectId identificatorul proiectului în care se adaugă pagina
     * @return DTO-ul de răspuns corespunzător paginii nou create
     * @throws ProjectNotFoundException dacă proiectul nu există
     * @throws OwnershipException dacă proiectul nu aparține utilizatorului curent
     */
    @Transactional
    public PageResponseDto createPage(PageRequestDto requestDto, Long projectId){

        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(
                        () -> new ProjectNotFoundException(String.format("Project with id: %d not found", projectId)));

        projectService.validateProjectOwnership(project);

        PageEntity page = new PageEntity();
        updatePageData(requestDto, page);

        page.setProject(project);

        PageEntity createdPage = pageRepository.saveAndFlush(page);
        return PageConverter.convertToResponseDto(createdPage);
    }

    /**
     * Returnează toate paginile existente în baza de date.
     *
     * @return lista tuturor paginilor sub formă de DTO-uri de răspuns
     */
    public List<PageResponseDto> getAllPages() {
        return pageRepository.findAll().stream()
                .map(PageConverter::convertToResponseDto)
                .toList();
    }

    /**
     * Caută o pagină după identificatorul său.
     *
     * @param pageId identificatorul unic al paginii căutate
     * @return DTO-ul de răspuns corespunzător paginii găsite
     * @throws PageNotFoundException dacă nu există nicio pagină cu acest id
     */
    public PageResponseDto getPageById(Long pageId){
        PageEntity page = pageRepository
                .findById(pageId)
                .orElseThrow(
                        () -> new PageNotFoundException(String.format("Page with id: %d not found", pageId)));

        return PageConverter.convertToResponseDto(page);
    }

    /**
     * Returnează toate paginile care aparțin unui anumit proiect.
     *
     * @param projectId identificatorul proiectului ale cărui pagini se caută
     * @return lista paginilor proiectului sub formă de DTO-uri de răspuns
     * @throws ProjectNotFoundException dacă proiectul nu există
     */
    public List<PageResponseDto> getPagesByProjectId(Long projectId) {
        if(!projectRepository.existsById(projectId))
            throw new ProjectNotFoundException(String.format("Project with id: %d not found", projectId));

        List<PageEntity> pages = pageRepository.getPageEntitiesByProjectId(projectId);
        return pages.stream().map(PageConverter::convertToResponseDto).toList();
    }

    /**
     * Actualizează datele unei pagini existente, după verificarea dreptului de
     * proprietate al utilizatorului curent asupra proiectului care o conține.
     *
     * @param requestDto noile date ale paginii (nume și rută)
     * @param pageId identificatorul paginii care trebuie actualizată
     * @return DTO-ul de răspuns corespunzător paginii actualizate
     * @throws PageNotFoundException dacă pagina nu există
     * @throws OwnershipException dacă proiectul nu aparține utilizatorului curent
     */
    public PageResponseDto updatePage(PageRequestDto requestDto, Long pageId) {
        PageEntity page = pageRepository
                .findById(pageId)
                .orElseThrow(
                        () -> new PageNotFoundException(String.format("Page with id: %d not found", pageId)));

        projectService.validateProjectOwnership(page.getProject());

        updatePageData(requestDto, page);

        PageEntity updatedPage = pageRepository.saveAndFlush(page);
        return PageConverter.convertToResponseDto(updatedPage);
    }

    /**
     * Șterge o pagină după identificatorul său.
     *
     * @param pageId identificatorul paginii care trebuie ștearsă
     * @throws PageNotFoundException dacă pagina nu există
     */
    public void deletePageById(Long pageId) {
        PageEntity page = pageRepository
                .findById(pageId)
                .orElseThrow(
                        () -> new PageNotFoundException(String.format("Page with id: %d not found", pageId)));

        pageRepository.deleteById(page.getId());
    }

    /**
     * Șterge toate paginile unui proiect. Înainte de ștergere, anulează referințele
     * părinte ale componentelor pentru a evita încălcarea constrângerilor de cheie
     * externă în baza de date.
     *
     * @param projectId identificatorul proiectului ale cărui pagini se șterg
     */
    @Transactional
    public void deletePagesByProjectId(Long projectId) {
        List<PageEntity> pages = pageRepository.findByProjectId(projectId);

        for (PageEntity page : pages) {
            // Null out parent references so JPA can delete without FK violations
            for (ComponentEntity component : page.getComponents()) {
                component.setParent(null);
            }
            pageRepository.save(page);
        }

        pageRepository.flush(); // flush nulled parents before delete
        pageRepository.deleteAll(pages);
        pageRepository.flush();
    }

    /**
     * Verifică dacă pagina dată aparține (prin proiectul său) utilizatorului curent.
     *
     * @param page entitatea pagină a cărei proprietate se verifică
     * @throws OwnershipException dacă pagina nu aparține utilizatorului curent
     */
    public void validatePageOwnership(PageEntity page){
        UserEntity currentUser = userService.getCurrentUserEntity();
        if(!(currentUser.getId()).equals(page.getProject().getUser().getId()))
            throw new OwnershipException();
    }

    /**
     * Copiază datele din DTO-ul de cerere în entitatea pagină (nume și rută).
     *
     * @param requestDto sursa datelor (DTO-ul de cerere)
     * @param page entitatea pagină care va fi actualizată
     */
    private void updatePageData(PageRequestDto requestDto, PageEntity page) {
        page.setName(requestDto.getName());
        page.setRoute(requestDto.getRoute());
    }
}
