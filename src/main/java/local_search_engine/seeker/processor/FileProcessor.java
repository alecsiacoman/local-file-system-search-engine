package local_search_engine.seeker.processor;

import local_search_engine.seeker.model.IndexedFile;
import local_search_engine.seeker.repository.FileRepository;
import local_search_engine.seeker.service.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Service
@Slf4j
public class FileProcessor {
    @Autowired
    private ParserService parserService;

    @Autowired
    private FileRepository fileRepository;

    public void processFiles(List<Path> files) {
        for (Path file : files) {
            if (!Files.isReadable(file)) continue;

            try {
                IndexedFile indexedFile = extractFileInfo(file);
                if (!fileAlreadyExists(indexedFile)) {
                    fileRepository.save(indexedFile);
                }
            } catch (Exception e) {
                log.error("Error processing file {}: {}", file, e.getMessage());
            }
        }
    }

    private boolean fileAlreadyExists(IndexedFile newFile) {
        return fileRepository.existsByFilePathAndLastModified(
                newFile.getFilePath(), newFile.getLastModified());
    }

    public IndexedFile extractFileInfo(Path file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
        String content = parserService.extractContent(file);

        IndexedFile indexedFile = new IndexedFile();
        indexedFile.setFileName(file.getFileName().toString());
        indexedFile.setFilePath(file.toString());
        indexedFile.setExtension(getFileExtension(file));
        indexedFile.setSize(attr.size());
        indexedFile.setLastModified(attr.lastModifiedTime().toString());
        indexedFile.setContent(content);
        indexedFile.setMetadata("{\"owner\": \"" + Files.getOwner(file).toString() + "\"}");
        return indexedFile;
    }

    public String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        return (lastDot == -1) ? "" : fileName.substring(lastDot + 1).toLowerCase();
    }
}

