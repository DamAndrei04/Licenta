package com.example.demo.app.pages;

import com.example.demo.api.PageApi;
import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controler REST care implementează {@link PageApi}. Primește cererile HTTP legate de
 * pagini, deleagă logica de afaceri către {@link PageService} și împachetează
 * rezultatul în răspunsuri HTTP cu codul de stare corespunzător.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class PageController implements PageApi {

    private final PageService pageService;

    /**
     * {@inheritDoc}
     * Deleagă crearea către serviciu și returnează pagina creată cu stare 201.
     */
    @Override
    public ResponseEntity<PageResponseDto> createPage(PageRequestDto pageRequestDto, Long projectId){
        log.info("Received request for createPage with {}", pageRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pageService.createPage(pageRequestDto, projectId));
    }

    /**
     * {@inheritDoc}
     * Returnează lista tuturor paginilor cu stare 200.
     */
    @Override
    public ResponseEntity<List<PageResponseDto>> getAllPages(){
        log.info("Received request for getAllPages");

        return ResponseEntity.status(HttpStatus.OK)
                .body(pageService.getAllPages());
    }

    /**
     * {@inheritDoc}
     * Returnează pagina identificată prin {@code pageId} cu stare 200.
     */
    @Override
    public ResponseEntity<PageResponseDto> getPageById(Long pageId){
        log.info("Received request for getPageById with: {}", pageId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pageService.getPageById(pageId));
    }

    /**
     * {@inheritDoc}
     * Returnează paginile proiectului identificat prin {@code projectId} cu stare 200.
     */
    @Override
    public ResponseEntity<List<PageResponseDto>> getPagesByProjectId(@PathVariable Long projectId){
        log.info("Received request for getPagesByProjectId with id: {}", projectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pageService.getPagesByProjectId(projectId));
    }

    /**
     * {@inheritDoc}
     * Deleagă actualizarea către serviciu și returnează pagina actualizată cu stare 200.
     */
    @Override
    public ResponseEntity<PageResponseDto> updatePage(PageRequestDto pageRequestDto, Long pageId){
        log.info("Received request for updatePage with id: {} and {}", pageRequestDto, pageId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pageService.updatePage(pageRequestDto, pageId));
    }

    /**
     * {@inheritDoc}
     * Deleagă ștergerea către serviciu și returnează stare 204 fără conținut.
     */
    @Override
    public ResponseEntity<Void> deletePage(Long pageId){
        log.info("Received request for deletePage with id: {}", pageId);

        pageService.deletePageById(pageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }
}
