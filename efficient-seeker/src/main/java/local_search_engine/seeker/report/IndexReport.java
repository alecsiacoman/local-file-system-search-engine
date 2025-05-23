package local_search_engine.seeker.report;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class IndexReport {
    private int totalFiles;
    private int indexedFiles;
    private int failedFiles;
    private List<String> errorMessages = new ArrayList<>();
    private Instant startTime;
    private Instant endTime;

    public IndexReport() {
        this.startTime = Instant.now();
    }

    public void fileIndexed() {
        indexedFiles++;
        totalFiles++;
    }

    public void fileFailed(String filePath, Exception e) {
        failedFiles++;
        totalFiles++;
        errorMessages.add("Failed: " + filePath + " - " + e.getMessage());
    }

    public void end() {
        this.endTime = Instant.now();
    }

    public String getDuration() {
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}