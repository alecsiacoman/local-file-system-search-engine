package local_search_engine.seeker.widget;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WidgetResolver {
    public Optional<Widget> resolveWidget(String query) {
        query = query.toLowerCase();
        if (query.contains("calculator")) {
            return Optional.of(new Widget("Calculator 🧮"));
        } else if (query.contains("calendar")) {
            return Optional.of(new Widget("Calendar \uD83D\uDCC5"));
        }
        return Optional.empty();
    }
}
