package local_search_engine.seeker.widget;

import local_search_engine.seeker.model.IndexedFile;

import java.util.List;
import java.util.Optional;

public interface WidgetStrategy {
    Optional<Widget> resolve(String query, List<IndexedFile> results);
}
