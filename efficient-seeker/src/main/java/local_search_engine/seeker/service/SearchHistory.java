package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.observer.SearchObserver;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchHistory implements SearchObserver {

    private final Map<String, Integer> searchQueryHistory = new HashMap<>();

    @Override
    public void onSearch(String query, List<IndexedFile> searchResults) {
        searchQueryHistory.put(query, searchQueryHistory.getOrDefault(query, 0) + 1);
    }

    public List<String> suggestQueries() {
        return searchQueryHistory.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}