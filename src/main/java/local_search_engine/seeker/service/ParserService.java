package local_search_engine.seeker.service;

import local_search_engine.seeker.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {

    private final FileRepository fileRepository;
    private final FilterService filterService;

    public String extractContent(Path filePath) throws IOException {
        String extension = getFileExtension(filePath);
        switch (extension.toLowerCase()) {
            case "txt":
                return Files.readString(filePath);
            default:
                return "";
        }
    }

    private String getFileExtension(Path path) {
        String name = path.getFileName().toString();
        int lastDot = name.lastIndexOf(".");
        return (lastDot == -1) ? "" : name.substring(lastDot + 1);
    }

}
