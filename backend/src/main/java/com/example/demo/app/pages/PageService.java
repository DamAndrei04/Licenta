package com.example.demo.app.pages;

import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
import com.example.demo.api.exception.OwnershipException;
import com.example.demo.api.exception.PageNotFoundException;
import com.example.demo.api.exception.ProjectNotFoundException;
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

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserService userService;

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

    public List<PageResponseDto> getAllPages() {
        return pageRepository.findAll().stream()
                .map(PageConverter::convertToResponseDto)
                .toList();
    }

    public PageResponseDto getPageById(Long pageId){
        PageEntity page = pageRepository
                .findById(pageId)
                .orElseThrow(
                        () -> new PageNotFoundException(String.format("Page with id: %d not found", pageId)));

        return PageConverter.convertToResponseDto(page);
    }

    public List<PageResponseDto> getPagesByProjectId(Long projectId) {
        if(!projectRepository.existsById(projectId))
            throw new ProjectNotFoundException(String.format("Project with id: %d not found", projectId));

        List<PageEntity> pages = pageRepository.getPageEntitiesByProjectId(projectId);
        return pages.stream().map(PageConverter::convertToResponseDto).toList();
    }

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

    public void deletePageById(Long pageId) {
        PageEntity page = pageRepository
                .findById(pageId)
                .orElseThrow(
                        () -> new PageNotFoundException(String.format("Page with id: %d not found", pageId)));

        pageRepository.deleteById(page.getId());
    }

    public void validatePageOwnership(PageEntity page){
        UserEntity currentUser = userService.getCurrentUserEntity();
        if(!(currentUser.getId()).equals(page.getProject().getUser().getId()))
            throw new OwnershipException();
    }

    private void updatePageData(PageRequestDto requestDto, PageEntity page) {
        page.setName(requestDto.getName());
        page.setRoute(requestDto.getRoute());
    }
}
