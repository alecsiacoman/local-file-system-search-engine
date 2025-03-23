package local_search_engine.seeker.controller;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.service.SearchService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        return "search";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<IndexedFile> results = searchService.searchFiles(query);

        model.addAttribute("files", results);
        return "search";
    }

    @GetMapping("/open-file")
    public ResponseEntity<Resource> openFile(@RequestParam("filePath") String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Resource resource = new FileSystemResource(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

