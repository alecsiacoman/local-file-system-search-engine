package local_search_engine.seeker.widget;

import java.util.List;
import java.util.Optional;
import local_search_engine.seeker.model.IndexedFile;
import org.springframework.stereotype.Component;

@Component
public class WidgetFactory {
  private final List<WidgetStrategy> strategies;

  public WidgetFactory(List<WidgetStrategy> strategies) {
    this.strategies = strategies;
  }

  public List<Widget> resolveAll(String query, List<IndexedFile> results) {
    return strategies.stream()
        .map(strategy -> strategy.resolve(query, results))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .distinct()
        .toList();
  }
}
