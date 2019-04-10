package zad1.api.dictionary;

import zad1.api.dictionary.translations.TEI;

import java.util.HashMap;
import java.util.Map;

public final class Dictionary implements DictionaryInterface {

    private final Map<String, String> dictionary = new HashMap<>();

    Dictionary(TEI teiDocument) {
        teiDocument.text.body.entry.forEach(entry -> {
            this.dictionary.putIfAbsent(entry.form.orth, entry.sense.cit.quote);
        });
    }

    public String translate(String word) {
        return this.dictionary.getOrDefault(word.trim(), "No translation");
    }
}
