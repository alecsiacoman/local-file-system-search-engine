package local_search_engine.seeker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

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

  public String detectLanguage(Path filePath) {
    String fileName = filePath.getFileName().toString().toLowerCase();

    if (fileName.endsWith(".java")) return "Java";
    if (fileName.endsWith(".c")) return "C";
    if (fileName.endsWith(".cpp")) return "C++";
    if (fileName.endsWith(".py")) return "Python";
    if (fileName.endsWith(".js")) return "JavaScript";
    if (fileName.endsWith(".html") || fileName.endsWith(".htm")) return "HTML";
    if (fileName.endsWith(".css")) return "CSS";
    if (fileName.endsWith(".sh")) return "Shell";
    if (fileName.endsWith(".rb")) return "Ruby";
    if (fileName.endsWith(".ts")) return "TypeScript";

    return "Unknown";
  }

  public int extractYear(BasicFileAttributes attr) {
    Instant instant = attr.lastModifiedTime().toInstant();
    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
    return date.getYear();
  }
}
