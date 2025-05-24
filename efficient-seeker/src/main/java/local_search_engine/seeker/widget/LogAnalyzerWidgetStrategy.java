package local_search_engine.seeker.widget;

import java.util.List;
import java.util.Optional;
import local_search_engine.seeker.model.IndexedFile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class LogAnalyzerWidgetStrategy implements WidgetStrategy {
  @Override
  public Optional<Widget> resolve(String query, List<IndexedFile> results) {
    long logCount =
        results.stream().filter(file -> "log".equalsIgnoreCase(file.getExtension())).count();

    return logCount >= 5 ? Optional.of(new Widget("Analyze Logs 🧾")) : Optional.empty();
  }
}
