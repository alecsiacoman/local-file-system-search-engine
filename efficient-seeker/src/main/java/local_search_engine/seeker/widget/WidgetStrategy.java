package local_search_engine.seeker.widget;

import java.util.List;
import java.util.Optional;
import local_search_engine.seeker.model.IndexedFile;

public interface WidgetStrategy {
  Optional<Widget> resolve(String query, List<IndexedFile> results);
}
