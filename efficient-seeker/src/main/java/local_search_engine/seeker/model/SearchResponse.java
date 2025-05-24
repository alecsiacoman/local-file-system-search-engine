package local_search_engine.seeker.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import local_search_engine.seeker.widget.Widget;

public record SearchResponse(
    List<IndexedFile> results,
    int totalPages,
    int currentPage,
    Optional<Widget> widget,
    Map<String, Long> fileTypeCount,
    Map<Integer, Long> modifiedYearCount,
    Map<String, Long> languageCount) {}
