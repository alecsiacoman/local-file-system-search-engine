package local_search_engine.seeker.observer;

import java.util.List;
import local_search_engine.seeker.model.IndexedFile;

public interface SearchObserver {
  void onSearch(String query, List<IndexedFile> searchResults);
}
