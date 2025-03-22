package local_search_engine.seeker.service;

import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;

@Service
public class FilterService {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("txt", "pdf", "docx");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public boolean isValidFile(Path file) {
        try {
            String extension = getFileExtension(file);
            long size = Files.size(file);
            return ALLOWED_EXTENSIONS.contains(extension) && size <= MAX_SIZE;
        } catch (IOException e) {
            return false;
        }
    }

    private String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        return (lastDot == -1) ? "" : fileName.substring(lastDot + 1).toLowerCase();
    }
}