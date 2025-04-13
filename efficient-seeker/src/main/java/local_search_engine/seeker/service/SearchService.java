package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.model.QueryCriteria;
import local_search_engine.seeker.observer.SearchObserver;
import local_search_engine.seeker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class SearchService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RankingService rankingService;

    private final List<SearchObserver> observers = new ArrayList<>();

    public void addObserver(SearchObserver observer) {
        observers.add(observer);
    }


    private void notifyObservers(String query, List<IndexedFile> searchResults) {
        for (SearchObserver observer : observers) {
            observer.onSearch(query, searchResults);
        }
    }


    public Page<IndexedFile> searchFiles(String query, String rankingFormat, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        QueryCriteria queryCriteria = new QueryCriteria(query);
        Page<IndexedFile> dbResults;

        if (queryCriteria.content != null) {
            dbResults = fileRepository.searchByContent(queryCriteria.content, Pageable.unpaged());
        } else if (queryCriteria.path != null) {
            dbResults = fileRepository.searchByPath(queryCriteria.path, Pageable.unpaged());
        } else {
            dbResults = fileRepository.searchEverywhere(query, Pageable.unpaged());
        }

        List<IndexedFile> sortedFiles = dbResults.getContent().stream()
                .peek(file -> file.setScore(rankingService.computeRankByReport(file, rankingFormat)))
                .sorted(Comparator.comparingDouble(file -> rankingService.computeRankByReport((IndexedFile) file, rankingFormat)).reversed())
                .toList();

        notifyObservers(query, sortedFiles);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedFiles.size());
        List<IndexedFile> pageContent = (start > end) ? Collections.emptyList() : sortedFiles.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedFiles.size());
    }
}
