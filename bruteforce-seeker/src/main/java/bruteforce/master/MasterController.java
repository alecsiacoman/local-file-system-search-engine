package bruteforce.master;

import bruteforce.model.FileResult;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class MasterController {

  private final MasterService masterService;

  public MasterController(MasterService masterService) {
    this.masterService = masterService;
  }

  @GetMapping
  public List<FileResult> search(@RequestParam String query) {
    return masterService.search(query);
  }
}
