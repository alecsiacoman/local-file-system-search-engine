package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.model.QueryCriteria;
import local_search_engine.seeker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {
    @Autowired
    private FileRepository fileRepository;

    public Page<IndexedFile> searchFiles(String query, Pageable pageable) {
        QueryCriteria queryCriteria = parseQuery(query);
        Page<IndexedFile> dbResults;

        if (queryCriteria.content != null) {
            dbResults = fileRepository.searchByContent(queryCriteria.content, Pageable.unpaged());
        } else if (queryCriteria.path != null) {
            dbResults = fileRepository.searchByPath(queryCriteria.path, Pageable.unpaged());
        } else {
            dbResults = fileRepository.searchEverywhere(query, Pageable.unpaged());
        }

        List<IndexedFile> sortedFiles = dbResults.getContent().stream()
                .sorted(Comparator.comparingDouble(this::computeRank).reversed())
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedFiles.size());
        List<IndexedFile> pageContent = (start > end) ? Collections.emptyList() : sortedFiles.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedFiles.size());
    }



//    public Page<IndexedFile> searchFiles(String query, Pageable pageable) {
//        QueryCriteria queryCriteria = parseQuery(query);
//
//        String initialQuery = query.trim().toLowerCase();
//        Page<IndexedFile> dbResults = fileRepository.searchFiles(initialQuery, Pageable.unpaged());
//
//        /*
//            Retrieved files from database are either matched with
//            path:A/B, or with content:C or with the initial query
//         */
//        List<IndexedFile> filteredFiles = dbResults.stream()
//                .filter(file -> {
//                    boolean matches = true;
//                    if (queryCriteria.content != null && !queryCriteria.content.isBlank()) {
//                        matches &= file.getContent() != null &&
//                                file.getContent().toLowerCase().contains(queryCriteria.content.toLowerCase());
//                    } else {
//                        // fallback: match content with query
//                        matches &= file.getContent() != null &&
//                                file.getContent().toLowerCase().contains(initialQuery);
//                    }
//
//                    if (queryCriteria.path != null && !queryCriteria.path.isBlank()) {
//                        matches &= file.getFilePath() != null &&
//                                file.getFilePath().toLowerCase().contains(queryCriteria.path.toLowerCase());
//                    } else {
//                        // fallback: match path and name with query
//                        matches &= (file.getFilePath() != null && file.getFilePath().toLowerCase().contains(initialQuery)) ||
//                                (file.getFileName() != null && file.getFileName().toLowerCase().contains(initialQuery));
//                    }
//
//                    return matches;
//                })
//                .sorted(Comparator.comparingDouble(this::computeRank).reversed())
//                .toList();
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min(start + pageable.getPageSize(), filteredFiles.size());
//        List<IndexedFile> pageContent = (start > end) ? Collections.emptyList() : filteredFiles.subList(start, end);
//
//        return new PageImpl<>(pageContent, pageable, filteredFiles.size());
//    }


    private QueryCriteria parseQuery(String query) {
        QueryCriteria queryCriteria = new QueryCriteria();
        String[] parts = query.split("\\s+");
        for(String part: parts) {
            if(part.startsWith("content:")){
                queryCriteria.content = part.substring(8);
            } else if(part.startsWith("path:")){
                queryCriteria.path = part.substring(5);
            }
        }
        return queryCriteria;
    }

    private double computeRank(IndexedFile file) {
        double score = 0.0;

        //keyword presence in path
        if (file.getFilePath() != null && file.getFilePath().toLowerCase().contains("important")) {
            score += 2.0;
        }

        //path length -> shorter path means higher score
        score += 1.0 / (file.getFilePath().length() + 1);

        //file extension prioritization
        if(file.getExtension() != null) {
            switch (file.getExtension()) {
                case "java":
                    score += 1.5;
                    break;
                case "txt":
                    score += 1.0;
                    break;
                case "md":
                    score += 0.5;
                    break;
                default:
                    break;
            }
        }

        //recent file access
        long modifiedTime = java.time.Instant.parse(file.getLastModified()).toEpochMilli();
        long now = System.currentTimeMillis();
        long age = now - modifiedTime;
        score += Math.max(0.0, 1_000_000.0 / (age + 1));

        //file size
        if(file.getSize() < 1000) {
            score += 0.5;
        }

        return score;
    }

}
