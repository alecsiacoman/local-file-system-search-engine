package local_search_engine.seeker.service;

import local_search_engine.seeker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.file.*;
import java.io.IOException;

@Service
@Slf4j
public class FileWatcherService {

    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private FileRepository fileRepository;

    @Async
    @Transactional
    public void startWatching(Path directory) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        log.info("Started file watcher...");

        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path changedFile = directory.resolve((Path) event.context());

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isRegularFile(changedFile)) {
                        log.info("File created: {}", changedFile);
                        crawlerService.indexFile(changedFile);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    if (Files.isRegularFile(changedFile)) {
                        log.info("File modified: {}", changedFile);
                        deleteFileRecord(changedFile);
                        crawlerService.indexFile(changedFile);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    deleteFileRecord(changedFile);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    public void deleteFileRecord(Path filePath) {
        fileRepository.deleteByFilePath(filePath.toString());
        log.info("File with path {} deleted from the database.", filePath);
    }
}

