package local_search_engine.seeker.controller;

import local_search_engine.seeker.corrector.SpellingCorrectorService;
import local_search_engine.seeker.model.SearchResponse;
import local_search_engine.seeker.service.ProxySearchService;
import local_search_engine.seeker.service.SearchHistory;
import local_search_engine.seeker.service.SearchService;
import local_search_engine.seeker.service.StandardSearchService;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final SpellingCorrectorService spellingCorrectorService;

    public SearchController(@Qualifier("proxySearchService") ProxySearchService proxySearchService, StandardSearchService standardSearchService, SearchHistory searchHistory, SpellingCorrectorService spellingCorrectorService) {
        this.searchService = proxySearchService;
        this.searchHistory = searchHistory;
        this.spellingCorrectorService = spellingCorrectorService;

        standardSearchService.addObserver(searchHistory);
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

        String correctedQuery = spellingCorrectorService.correctQuery(query);
        boolean isCorrected = !correctedQuery.equals(query);

        SearchResponse response = searchService.searchFiles(correctedQuery, rankingFormat, pageable);

        model.addAttribute("files", response.results());
        model.addAttribute("totalPages", response.totalPages());
        model.addAttribute("currentPage", response.currentPage());
        model.addAttribute("query", query);
        model.addAttribute("suggestions", searchHistory.suggestQueries());
        model.addAttribute("widget", response.widget());
        if (isCorrected) {
            model.addAttribute("correctedQuery", correctedQuery);
        }
        model.addAttribute("fileTypeCount", response.fileTypeCount());
        model.addAttribute("modifiedYearCount", response.modifiedYearCount());
        model.addAttribute("languageCount", response.languageCount());

        return "search";
    }

    @GetMapping("/search/ajax")
    @ResponseBody
    public Map<String, Object> searchAjax(@RequestParam("query") String query,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "average") String rankingFormat) {
        Pageable pageable = PageRequest.of(page, size);

        String correctedQuery = spellingCorrectorService.correctQuery(query);
        boolean isCorrected = !correctedQuery.equals(query);

        SearchResponse searchResponse = searchService.searchFiles(correctedQuery, rankingFormat, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("files", searchResponse.results());
        response.put("totalPages", searchResponse.totalPages());
        response.put("currentPage", page);
        response.put("widget", searchResponse.widget());
        if (isCorrected) {
            response.put("correctedQuery", correctedQuery);
        }

        return response;
    }
}

