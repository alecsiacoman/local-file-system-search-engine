package local_search_engine.seeker.controller;

import local_search_engine.seeker.service.FileCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/crawl")
public class FileCrawlerController {
    private final FileCrawlerService fileCrawlerService;

    public FileCrawlerController(FileCrawlerService fileCrawlerService) {
        this.fileCrawlerService = fileCrawlerService;
    }


    @GetMapping
    public String startCrawling() {
        String directoryPath = "C:/Users/coman/Desktop/search-engine-test";
        try {
            fileCrawlerService.crawlDirectory(directoryPath);
            return "Crawling started...";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}

