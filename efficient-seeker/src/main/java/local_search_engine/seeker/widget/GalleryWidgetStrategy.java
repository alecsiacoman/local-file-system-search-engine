package local_search_engine.seeker.widget;

import java.util.List;
import java.util.Optional;
import local_search_engine.seeker.model.IndexedFile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class GalleryWidgetStrategy implements WidgetStrategy {
  private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

  @Override
  public Optional<Widget> resolve(String query, List<IndexedFile> results) {
    long imageCount =
        results.stream()
            .filter(file -> IMAGE_EXTENSIONS.contains(file.getExtension().toLowerCase()))
            .count();

    if (imageCount >= 5 || (query.contains("image") && imageCount > 0)) {
      return Optional.of(new Widget("View as Gallery 🖼"));
    }

    return Optional.empty();
  }
}
