package local_search_engine.seeker.helper;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component("thymeleafHelper")
public class ThymeleafHelper {

  public String mapToSummaryString(Map<?, Long> map) {
    return map.entrySet().stream()
        .map(e -> e.getKey() + " (" + e.getValue() + ")")
        .collect(Collectors.joining(", "));
  }
}
