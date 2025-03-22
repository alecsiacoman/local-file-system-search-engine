package local_search_engine.seeker.controller;

import local_search_engine.seeker.service.CrawlerService;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/crawl")
public class CrawlerController {
    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }


    @GetMapping
    public String startCrawling() {
        String directoryPath = "C:/Users/coman/Desktop/search-engine-test";
        try {
            crawlerService.crawlDirectory(directoryPath);
            return "Crawling started...";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}

