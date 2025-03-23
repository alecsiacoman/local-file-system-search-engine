package local_search_engine.seeker.service;

import local_search_engine.seeker.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

                if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    if (Files.isRegularFile(changedFile)) {
                        log.info("File modified: {}", changedFile);
                        crawlerService.indexFile(changedFile);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    log.info("File deleted: {}", changedFile);
                    fileRepository.deleteByFilePath(changedFile.toString());
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}

