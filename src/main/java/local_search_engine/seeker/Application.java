package local_search_engine.seeker;

import local_search_engine.seeker.service.CrawlerService;
import local_search_engine.seeker.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@Async
public class Application {
	@Autowired
	private CrawlerService crawlerService;
	@Autowired
	private FileWatcherService fileWatcherService;

	private static final Path DIRECTORY_TO_WATCH = Paths.get("C:/Users/coman/Desktop/search-engine-test");

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() throws IOException {
		crawlerService.initialCrawl(DIRECTORY_TO_WATCH.toString());

		fileWatcherService.startWatching(DIRECTORY_TO_WATCH);
	}

}
