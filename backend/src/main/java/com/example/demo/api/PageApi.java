package com.example.demo.api;

import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contractul REST pentru gestionarea paginilor (rădăcina {@code /page}).
 * Implementarea este asigurată de {@link com.example.demo.app.pages.PageController}.
 */
@RestController
@RequestMapping("/page")
@Tag(name = "page")
public interface PageApi {

    /**
     * Creează o pagină într-un proiect (HTTP POST {@code /page/{projectId}}).
     *
     * @param pageRequestDto datele paginii de creat, validate
     * @param projectId identificatorul proiectului în care se adaugă pagina
     * @return răspuns HTTP 201 cu pagina creată
     */
    @PostMapping("/{projectId}")
    ResponseEntity<PageResponseDto> createPage(
            @RequestBody @Valid PageRequestDto pageRequestDto,
            @PathVariable Long projectId);

    /**
     * Returnează toate paginile (HTTP GET {@code /page}).
     *
     * @return răspuns HTTP 200 cu lista tuturor paginilor
     */
    @GetMapping
    ResponseEntity<List<PageResponseDto>> getAllPages();

    /**
     * Returnează o pagină după id (HTTP GET {@code /page/{pageId}}).
     *
     * @param pageId identificatorul paginii din calea cererii
     * @return răspuns HTTP 200 cu pagina găsită
     */
    @GetMapping("/{pageId}")
    ResponseEntity<PageResponseDto> getPageById(@PathVariable Long pageId);

    /**
     * Returnează paginile unui proiect (HTTP GET {@code /page/project/{projectId}}).
     *
     * @param projectId identificatorul proiectului din calea cererii
     * @return răspuns HTTP 200 cu lista paginilor proiectului
     */
    @GetMapping("/project/{projectId}")
    ResponseEntity<List<PageResponseDto>> getPagesByProjectId(@PathVariable Long projectId);

    /**
     * Actualizează o pagină existentă (HTTP PUT {@code /page/{pageId}}).
     *
     * @param pageRequestDto noile date ale paginii, validate
     * @param pageId identificatorul paginii de actualizat
     * @return răspuns HTTP 200 cu pagina actualizată
     */
    @PutMapping("/{pageId}")
    ResponseEntity<PageResponseDto> updatePage(
            @RequestBody @Valid PageRequestDto pageRequestDto,
            @PathVariable Long pageId);

    /**
     * Șterge o pagină (HTTP DELETE {@code /page/{pageId}}).
     *
     * @param pageId identificatorul paginii de șters
     * @return răspuns HTTP 204 fără conținut
     */
    @DeleteMapping("/{pageId}")
    ResponseEntity<Void> deletePage(@PathVariable Long pageId);
}
