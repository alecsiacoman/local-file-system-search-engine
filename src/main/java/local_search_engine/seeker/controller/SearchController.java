package local_search_engine.seeker.controller;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.service.SearchService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
    public String searchPage(@RequestParam("query") String query,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IndexedFile> results = searchService.searchFiles(query, pageable);

        model.addAttribute("files", results.getContent());
        model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("query", query);

        return "search"; // Return the search page populated with results
    }

    @GetMapping("/search/ajax")
    @ResponseBody
    public Map<String, Object> searchAjax(@RequestParam("query") String query,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IndexedFile> results = searchService.searchFiles(query, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("files", results.getContent());
        response.put("totalPages", results.getTotalPages());
        response.put("currentPage", page);

        return response;
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

