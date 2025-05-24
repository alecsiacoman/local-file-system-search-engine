package local_search_engine.seeker.service;

import java.util.HashMap;
import java.util.Map;
import local_search_engine.seeker.model.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("proxySearchService")
@Slf4j
public class ProxySearchService implements SearchService {

  private final SearchService searchService;
  private final Map<String, SearchResponse> cache = new HashMap<>();

  public ProxySearchService(StandardSearchService standardSearchService) {
    this.searchService = standardSearchService;
  }

  @Override
  public SearchResponse searchFiles(String query, String rankingFormat, Pageable pageable) {
    if (cache.containsKey(query)) {
      log.info("returning cache result for " + query);
      return cache.get(query);
    }

    log.info("cache miss -> go to standard searching");
    SearchResponse results = searchService.searchFiles(query, rankingFormat, pageable);
    cache.put(query, results);

    return results;
  }
}
