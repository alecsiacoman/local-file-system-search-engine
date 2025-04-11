package local_search_engine.seeker.service;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.model.QueryCriteria;
import local_search_engine.seeker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<IndexedFile> searchFiles(String query, Pageable pageable) {
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
                .peek(file -> file.setScore(rankingService.computeRankByReport(file)))  // Call the instance method
                .sorted(Comparator.comparingDouble(rankingService::computeRankByReport).reversed())  // Call the instance method
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedFiles.size());
        List<IndexedFile> pageContent = (start > end) ? Collections.emptyList() : sortedFiles.subList(start, end);


        return new PageImpl<>(pageContent, pageable, sortedFiles.size());
    }
}
