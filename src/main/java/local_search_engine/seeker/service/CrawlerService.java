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
public class CrawlerService {
    private static final int BATCH_SIZE = 500;
    private static final int THREAD_POOL_SIZE = 4;

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final FileProcessor fileProcessor;

    public CrawlerService(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    public void initialCrawl(String directoryPath) {
        log.info("Starting initial crawl for directory: {}", directoryPath);
        List<Path> fileBatch = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.forEach(file -> {
                if (Files.isRegularFile(file)) {
                    fileBatch.add(file);
                    if (fileBatch.size() >= BATCH_SIZE) {
                        processBatch(fileBatch);
                        fileBatch.clear();
                    }
                }
            });

            if (!fileBatch.isEmpty()) {
                processBatch(fileBatch);
            }

        } catch (IOException e) {
            log.error("Error during crawling: {}", e.getMessage());
        }
        log.info("Initial crawl finished.");
       // shutdownExecutor();
    }

    private void processBatch(List<Path> batch) {
        executor.submit(() -> fileProcessor.processFiles(batch));
    }

    private void shutdownExecutor() {
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void indexFile(Path file) {
        executor.submit(() -> {
            List<Path> singleFileList = Collections.singletonList(file);
            fileProcessor.processFiles(singleFileList);
        });
    }
}
