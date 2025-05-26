package local_search_engine.seeker.controller;

import java.util.HashMap;
import java.util.Map;
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

@Controller
public class SearchController {
  private final SearchService searchService;
  private final SearchHistory searchHistory;
  private final SpellingCorrectorService spellingCorrectorService;

  public SearchController(
      @Qualifier("proxySearchService") ProxySearchService proxySearchService,
      StandardSearchService standardSearchService,
      SearchHistory searchHistory,
      SpellingCorrectorService spellingCorrectorService) {
    this.searchService = proxySearchService;
    this.searchHistory = searchHistory;
    this.spellingCorrectorService = spellingCorrectorService;

    standardSearchService.addObserver(searchHistory);
  }

  @GetMapping("/")
  public String homePage(Model model) {
    return "search";
  }

  @GetMapping("/search/ajax")
  @ResponseBody
  public Map<String, Object> searchAjax(
      @RequestParam("query") String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "average") String rankingFormat) {
    Pageable pageable = PageRequest.of(page, size);

    String correctedQuery = spellingCorrectorService.correctQuery(query);
    boolean isCorrected = !correctedQuery.equals(query);

    SearchResponse searchResponse =
        searchService.searchFiles(correctedQuery, rankingFormat, pageable);

    Map<String, Object> response = new HashMap<>();
    response.put("files", searchResponse.results());
    response.put("totalPages", searchResponse.totalPages());
    response.put("currentPage", page);
    if (searchResponse.widget().isPresent()) {
      response.put("widget", searchResponse.widget().get());
    }

    if (isCorrected) {
      response.put("correctedQuery", correctedQuery);
    }
    response.put("suggestions", searchHistory.suggestQueries());
    response.put("fileTypeCount", searchResponse.fileTypeCount());
    response.put("modifiedYearCount", searchResponse.modifiedYearCount());
    response.put("languageCount", searchResponse.languageCount());

    return response;
  }
}
