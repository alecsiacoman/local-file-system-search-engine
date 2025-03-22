package local_search_engine.seeker.processor;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.repository.FileRepository;
import local_search_engine.seeker.service.FileFilterService;
import local_search_engine.seeker.service.FileParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class FileProcessor {
    @Autowired
    private FileFilterService fileFilterService;

    @Autowired
    private FileParserService fileParserService;

    @Autowired
    private FileRepository fileRepository;

    public void processFiles(List<Path> files) {
        for (Path file : files) {
            if (!fileFilterService.isValidFile(file)) {
                continue;
            }

            try {
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                String content = fileParserService.extractContent(file);
                log.info("Content of file {}: {}", file.getFileName(), content);

                IndexedFile indexedFile = new IndexedFile();
                indexedFile.setFileName(file.getFileName().toString());
                indexedFile.setFilePath(file.toString());
                indexedFile.setExtension(getFileExtension(file));
                indexedFile.setSize(attr.size());
                indexedFile.setLastModified(LocalDateTime.now().toString());
                indexedFile.setContent(content);
                indexedFile.setMetadata("{\"owner\": \"" + Files.getOwner(file).toString() + "\"}");

                fileRepository.save(indexedFile);
            } catch (Exception e) {
                System.err.println("Error processing file: " + file + " - " + e.getMessage());
            }
        }
    }

    private String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        return (lastDot == -1) ? "" : fileName.substring(lastDot + 1).toLowerCase();
    }
}

