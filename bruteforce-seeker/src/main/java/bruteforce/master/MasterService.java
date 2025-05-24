package bruteforce.master;

import bruteforce.model.FileResult;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MasterService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final List<String> workerUrls =
      List.of(
          "http://localhost:3901/api/search",
          "http://localhost:3902/api/search",
          "http://localhost:3903/api/search");
  private final Map<String, List<FileResult>> cache = new ConcurrentHashMap<>();

  public List<FileResult> search(String query) {
    if (cache.containsKey(query)) {
      return cache.get(query);
    }

    List<FileResult> allResults = new ArrayList<>();
    for (String url : workerUrls) {
      try {
        ResponseEntity<List<FileResult>> response =
            restTemplate.exchange(
                url + "?query=" + query,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        allResults.addAll(response.getBody());
      } catch (Exception e) {
        System.err.println("Worker error at: " + url + " -> " + e.getMessage());
      }
    }

    allResults.sort(Comparator.comparingDouble(FileResult::getScore).reversed());
    cache.put(query, allResults);
    return allResults;
  }
}
