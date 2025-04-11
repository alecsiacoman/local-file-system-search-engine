package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RankingService {

    @Value("${index.report.format}")
    private String rankingFormat;

    public double computeRankByReport(IndexedFile file, String rankingFormat) {
        return switch (rankingFormat) {
            case "average" -> computeRankByAverage(file);
            case "last_access" -> computeRankByLastAccess(file);
            case "shorter_path" -> computeRankByShorterPath(file);
            default -> 0;
        };
    }

    /*
    Average ranking takes into consideration:
        - keyword "important" present in the path
        - path length -> shorter path means higher score
        - file extension prioritization
        - recent file access
        - file size
     */
    private double computeRankByAverage(IndexedFile file) {
        double score = 0.0;

        if (file.getFilePath() != null && file.getFilePath().toLowerCase().contains("important")) {
            score += 2.0;
        }

        score += getPathLengthFactor(file) + getExtensionFactor(file) + getAccessTimeFactor(file) + getSizeFactor(file);

        return score;
    }

    private double computeRankByLastAccess(IndexedFile file) {
        long modifiedTime = java.time.Instant.parse(file.getLastModified()).toEpochMilli();
        long now = System.currentTimeMillis();
        long age = now - modifiedTime;

        return Math.max(0.0, 1_000_000.0 / (age + 1));
    }

    private double computeRankByShorterPath(IndexedFile file) {
        return getPathLengthFactor(file);
    }

    private double getPathLengthFactor(IndexedFile file) {
        return 1.0 / (file.getFilePath().length() + 1);
    }

    private double getExtensionFactor(IndexedFile file) {
        if (file.getExtension() != null) {
            switch (file.getExtension()) {
                case "java":
                    return 1.5;
                case "txt":
                    return 1.0;
                case "md":
                    return 0.5;
                default:
                    break;
            }
        }
        return 0.0;
    }

    private double getSizeFactor(IndexedFile file) {
        if (file.getSize() < 1000) {
            return 0.5;
        }
        return 1.0;
    }

    private double getAccessTimeFactor(IndexedFile file) {
        long modifiedTime = java.time.Instant.parse(file.getLastModified()).toEpochMilli();
        long now = System.currentTimeMillis();
        long age = now - modifiedTime;
        return Math.max(0.0, 1_000_000.0 / (age + 1));
    }
}
