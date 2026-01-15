package com.example.demo.api;

import com.example.demo.api.dto.page.PageRequestDto;
import com.example.demo.api.dto.page.PageResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/page")
@Tag(name = "page")
public interface PageApi {

    @PostMapping("/{projectId}")
    ResponseEntity<PageResponseDto> createPage(
            @RequestBody @Valid PageRequestDto pageRequestDto,
            @PathVariable Long projectId);

    @GetMapping
    ResponseEntity<List<PageResponseDto>> getAllPages();

    @GetMapping("/{pageId}")
    ResponseEntity<PageResponseDto> getPageById(@PathVariable Long pageId);

    @GetMapping("/project/{projectId}")
    ResponseEntity<List<PageResponseDto>> getPagesByProjectId(@PathVariable Long projectId);

    @PutMapping("/{pageId}")
    ResponseEntity<PageResponseDto> updatePage(
            @RequestBody @Valid PageRequestDto pageRequestDto,
            @PathVariable Long pageId);

    @DeleteMapping("/{pageId}")
    ResponseEntity<Void> deletePage(@PathVariable Long pageId);
}
