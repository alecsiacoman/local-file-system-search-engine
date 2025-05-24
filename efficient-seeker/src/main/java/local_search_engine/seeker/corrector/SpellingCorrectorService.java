package local_search_engine.seeker.corrector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpellingCorrectorService {
  private SpellingCorrectorStrategy correctionStrategy;

  @Autowired
  public void SpellingCorrectorService(SpellingCorrectorStrategy spellingCorrectorStrategy) {
    this.correctionStrategy = spellingCorrectorStrategy;
  }

  public String correctQuery(String query) {
    return correctionStrategy.correct(query);
  }
}
