package local_search_engine.seeker.corrector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Loader {

  public static Map<String, Integer> load(Path filePath) throws IOException {
    String text = Files.readString(filePath).toLowerCase();

    Pattern wordPattern = Pattern.compile("[a-z]+");
    Matcher matcher = wordPattern.matcher(text);

    Map<String, Integer> frequencyMap = new HashMap<>();
    while (matcher.find()) {
      String word = matcher.group();
      frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
    }

    return frequencyMap;
  }
}
