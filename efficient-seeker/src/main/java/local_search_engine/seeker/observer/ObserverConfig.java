package local_search_engine.seeker.observer;

import jakarta.annotation.PostConstruct;
import local_search_engine.seeker.service.SearchHistory;
import local_search_engine.seeker.service.StandardSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {

  @Autowired private StandardSearchService standardSearchService;

  @Autowired private SearchHistory searchHistory;

  @PostConstruct
  public void registerObservers() {
    standardSearchService.addObserver(searchHistory);
  }
}
