package bruteforce.worker;

import bruteforce.model.FileResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WorkerService {

    @Value("${worker.directory}")
    private String searchDirectory;

    public List<FileResult> search(String query) {
        List<FileResult> results = new ArrayList<>();
        File root = new File(searchDirectory);
        searchRecursive(root, query.toLowerCase(), results);
        return results;
    }

    private void searchRecursive(File file, String query, List<FileResult> results) {
        if (file == null || !file.exists()) return;

        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                searchRecursive(child, query, results);
            }
        } else {
            if (file.getName().toLowerCase().contains(query)) {
                results.add(new FileResult(file.getAbsolutePath(), 1.0));
            }
        }
    }
}