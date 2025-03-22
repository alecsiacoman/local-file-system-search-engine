package local_search_engine.seeker.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import local_search_engine.seeker.processor.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileCrawlerService {
    private static final int BATCH_SIZE = 500;
    private static final int THREAD_POOL_SIZE = 4;

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final FileProcessor fileProcessor;

    public FileCrawlerService(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    public void crawlDirectory(String directoryPath) throws IOException {
        List<Path> fileBatch = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            for (Iterator<Path> it = paths.iterator(); it.hasNext(); ) {
                Path file = it.next();

                if (Files.isRegularFile(file)) {
                    log.info("Adding file {} to batch...", file.getFileName());
                    fileBatch.add(file);
                    if (fileBatch.size() >= BATCH_SIZE) {
                        processBatch(new ArrayList<>(fileBatch));
                        fileBatch.clear();
                    }
                }
            }
        }

        if (!fileBatch.isEmpty()) {
            processBatch(fileBatch);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch(List<Path> batch) {
        executor.submit(() -> fileProcessor.processFiles(batch));
    }
}
