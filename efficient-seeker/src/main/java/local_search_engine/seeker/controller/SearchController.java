package local_search_engine.seeker.controller;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.service.SearchHistory;
import local_search_engine.seeker.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SearchController {
    private final SearchService searchService;
    private final SearchHistory searchHistory;

    public SearchController(SearchService searchService, SearchHistory searchHistory) {
        this.searchService = searchService;
        this.searchHistory = searchHistory;
        this.searchService.addObserver(searchHistory);
    }

    @GetMapping("/")
    public String homePage(Model model) {
        return "search";
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam("query") String query,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "average") String rankingFormat,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IndexedFile> results = searchService.searchFiles(query, rankingFormat, pageable);

        model.addAttribute("files", results.getContent());
        model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("query", query);

        model.addAttribute("suggestions", searchHistory.suggestQueries());

        return "search";
    }

    @GetMapping("/search/ajax")
    @ResponseBody
    public Map<String, Object> searchAjax(@RequestParam("query") String query,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "average") String rankingFormat) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IndexedFile> results = searchService.searchFiles(query, rankingFormat, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("files", results.getContent());
        response.put("totalPages", results.getTotalPages());
        response.put("currentPage", page);

        return response;
    }
}

