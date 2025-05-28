package es.upm.miw.foro.api.controller;

import es.upm.miw.foro.api.dto.TagDto;
import es.upm.miw.foro.service.TagService;
import es.upm.miw.foro.util.ApiPath;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ToString
@Slf4j
@RestController
@RequestMapping(ApiPath.TAGS)
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagDto>> searchTags(@RequestParam String query) {
        return ResponseEntity.ok(tagService.searchTags(query));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
