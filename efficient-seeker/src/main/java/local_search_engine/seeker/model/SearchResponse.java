package local_search_engine.seeker.model;

import local_search_engine.seeker.widget.Widget;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SearchResponse(
        List<IndexedFile> results,
        int totalPages,
        int currentPage,
        Optional<Widget> widgetOptional,
        Map<String, Long> fileTypeCount,
        Map<String, Long> modifiedYearCount,
        Map<String, Long> languageCount
) {}
