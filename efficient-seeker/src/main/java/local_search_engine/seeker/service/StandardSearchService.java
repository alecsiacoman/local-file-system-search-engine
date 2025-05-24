package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.model.QueryCriteria;
import local_search_engine.seeker.model.SearchResponse;
import local_search_engine.seeker.observer.SearchObserver;
import local_search_engine.seeker.repository.FileRepository;
import local_search_engine.seeker.widget.Widget;
import local_search_engine.seeker.widget.WidgetResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("standardSearchService")
@Slf4j
public class StandardSearchService implements SearchService{
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private WidgetResolver widgetResolver;

    private final List<SearchObserver> observers = new ArrayList<>();

    public void addObserver(SearchObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String query, List<IndexedFile> searchResults) {
        for (SearchObserver observer : observers) {
            observer.onSearch(query, searchResults);
        }
    }

    @Override
    public SearchResponse searchFiles(String query, String rankingFormat, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return new SearchResponse(null, 0, 0, null, null, null, null);
        }

        QueryCriteria queryCriteria = new QueryCriteria(query);
        List<IndexedFile> allResults = new ArrayList<>();

        if (queryCriteria.content != null) {
            allResults.addAll(fileRepository.searchByContent(queryCriteria.content, Pageable.unpaged()).getContent());
        }
        if (queryCriteria.path != null) {
            allResults.addAll(fileRepository.searchByPath(queryCriteria.path, Pageable.unpaged()).getContent());
        }
        if (queryCriteria.content == null && queryCriteria.path == null) {
            allResults.addAll(fileRepository.searchEverywhere(query, Pageable.unpaged()).getContent());
        }

        List<IndexedFile> sortedFiles = allResults.stream()
                .peek(file -> file.setScore(rankingService.computeRankByReport(file, rankingFormat)))
                .sorted(Comparator.comparingDouble(file -> rankingService.computeRankByReport((IndexedFile) file, rankingFormat)).reversed())
                .toList();

        notifyObservers(query, sortedFiles);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedFiles.size());
        int totalPages = (int) Math.ceil((double) sortedFiles.size() / pageable.getPageSize());
        List<IndexedFile> pageContent = (start > end) ? Collections.emptyList() : sortedFiles.subList(start, end);

        Optional<Widget> widgetOptional = widgetResolver.resolveWidget(query);

        Map<String, Long> fileTypeCount = sortedFiles.stream()
                .collect(Collectors.groupingBy(IndexedFile::getExtension, Collectors.counting()));

        Map<String, Long> modifiedYearCount = sortedFiles.stream()
                .collect(Collectors.groupingBy(IndexedFile::getLastModified, Collectors.counting()));

        Map<String, Long> languageCount = sortedFiles.stream()
                .collect(Collectors.groupingBy(IndexedFile::getLanguage, Collectors.counting()));

        return new SearchResponse(pageContent, totalPages, pageable.getPageNumber(), widgetOptional, fileTypeCount, modifiedYearCount, languageCount);
    }
}