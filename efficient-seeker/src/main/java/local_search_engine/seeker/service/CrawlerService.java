package local_search_engine.seeker.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import local_search_engine.seeker.processor.FileProcessor;
import local_search_engine.seeker.report.IndexReport;
import local_search_engine.seeker.report.ReportWriterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CrawlerService {
    private static final int BATCH_SIZE = 500;
    private static final int THREAD_POOL_SIZE = 4;

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final FileProcessor fileProcessor;

    @Autowired
    private ReportWriterFactory reportWriterFactory;

    public CrawlerService(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    public void initialCrawl(String directoryPath) {
        log.info("Starting initial crawl for directory: {}", directoryPath);
        List<Path> fileBatch = new ArrayList<>();
        IndexReport report = new IndexReport();
        List<Future<?>> futures = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.forEach(file -> {
                if (Files.isRegularFile(file)) {
                    fileBatch.add(file);
                    if (fileBatch.size() >= BATCH_SIZE) {
                        futures.add(processBatch(fileBatch, report));
                        fileBatch.clear();
                    }
                }
            });

            if (!fileBatch.isEmpty()) {
                futures.add(processBatch(fileBatch, report));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Error waiting for task: {}", e.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Error during crawling: {}", e.getMessage());
        }

        log.info("Initial crawl finished.");
        report.end();
        reportWriterFactory.getReportWriter().write(report, "report");
    }

    private Future<?> processBatch(List<Path> batch, IndexReport report) {
        return executor.submit(() -> fileProcessor.processFiles(batch, report));
    }

    public void indexFile(Path file) {
        executor.submit(() -> {
            List<Path> singleFileList = Collections.singletonList(file);
            fileProcessor.processFiles(singleFileList, null);
        });
    }
}
