package local_search_engine.seeker.model;

import local_search_engine.seeker.widget.Widget;

import java.util.List;
import java.util.Optional;

public record SearchResponse(
        List<IndexedFile> results,
        int totalPages,
        int currentPage,
        Optional<Widget> widgetOptional
) {}
