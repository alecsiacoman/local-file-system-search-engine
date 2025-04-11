package local_search_engine.seeker.service;

import local_search_engine.seeker.model.IndexedFile;
import org.springframework.stereotype.Service;

@Service
public class RankingService {
    public static double computeRank(IndexedFile file) {
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
