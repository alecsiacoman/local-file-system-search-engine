package local_search_engine.seeker.service;

import local_search_engine.seeker.model.SearchResponse;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    SearchResponse searchFiles(String query, String rankingFormat, Pageable pageable);
}
