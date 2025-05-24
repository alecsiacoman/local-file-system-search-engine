package local_search_engine.seeker.widget;

import local_search_engine.seeker.model.IndexedFile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Order(1)
public class WidgetResolver implements WidgetStrategy {
    @Override
    public Optional<Widget> resolve(String query, List<IndexedFile> results) {
        query = query.toLowerCase();
        if (query.contains("calculator")) {
            return Optional.of(new Widget("Calculator \uD83D\uDCBB"));
        } else if (query.contains("calendar")) {
            return Optional.of(new Widget("Calendar \uD83D\uDCC5"));
        }
        return Optional.empty();
    }
}