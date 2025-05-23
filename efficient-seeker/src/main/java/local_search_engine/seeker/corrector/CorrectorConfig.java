package local_search_engine.seeker.corrector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrectorConfig {
  @Bean
  public SpellingCorrectorStrategy spellingCorrectorStrategy() throws IOException {
    Path filePath = Path.of("efficient-seeker/src/main/resources/big.txt");
    Map<String, Integer> dictionary = Loader.load(filePath);
    return new SpellingCorrector(dictionary);
  }
}
