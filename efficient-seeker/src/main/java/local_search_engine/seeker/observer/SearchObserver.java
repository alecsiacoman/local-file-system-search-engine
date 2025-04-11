package local_search_engine.seeker.observer;

import local_search_engine.seeker.model.IndexedFile;

import java.util.List;

public interface SearchObserver {
    void onSearch(String query, List<IndexedFile> searchResults);
}
