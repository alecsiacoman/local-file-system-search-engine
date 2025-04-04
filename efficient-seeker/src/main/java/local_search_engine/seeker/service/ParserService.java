package local_search_engine.seeker.service;

import local_search_engine.seeker.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {
    private final Tika tika = new Tika();

    public String extractContent(Path filePath) {
        try {
            String mimeType = Files.probeContentType(filePath);
            if (mimeType != null && mimeType.startsWith("text")) {
                return Files.readString(filePath);
            }

            return tika.parseToString(filePath.toFile());
        } catch (IOException | TikaException e) {
            log.warn("Cannot extract content from file: {}", filePath.getFileName());
            return "";
        }
    }
}
