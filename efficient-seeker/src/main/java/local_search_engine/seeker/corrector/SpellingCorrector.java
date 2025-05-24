package local_search_engine.seeker.corrector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpellingCorrector implements SpellingCorrectorStrategy{

    private final Map<String, Integer> dictionary;

    public SpellingCorrector(Map<String, Integer> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String correct(String input) {
        if (dictionary.containsKey(input)) return input;

        List<String> words = edit(input);
        String best = input;
        int mostFrequent = 0;

        for (String w : words) {
            if (dictionary.containsKey(w) && dictionary.get(w) > mostFrequent) {
                best = w;
                mostFrequent = dictionary.get(w);
            }
        }
        return best;
    }

    private List<String> edit(String word) {
        List<String> edits = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            edits.add(word.substring(0, i) + word.substring(i + 1));
            if (i < word.length() - 1) {
                edits.add(word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2));
            }
            for (char c = 'a'; c <= 'z'; c++) {
                edits.add(word.substring(0, i) + c + word.substring(i + 1));
            }
        }
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                edits.add(word.substring(0, i) + c + word.substring(i));
            }
        }
        return edits;
    }
}
