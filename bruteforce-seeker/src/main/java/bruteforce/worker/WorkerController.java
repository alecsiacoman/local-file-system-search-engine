package bruteforce.worker;

import bruteforce.model.FileResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class WorkerController {

    private final WorkerService fileSearchService;

    public WorkerController(WorkerService fileSearchService) {
        this.fileSearchService = fileSearchService;
    }

    @GetMapping
    public List<FileResult> search(@RequestParam String query) {
        return fileSearchService.search(query);
    }
}

