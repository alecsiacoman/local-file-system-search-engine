package local_search_engine.seeker.helper;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component("thymeleafHelper")
public class ThymeleafHelper {

    public String mapToSummaryString(Map<?, Long> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "));
    }
}

