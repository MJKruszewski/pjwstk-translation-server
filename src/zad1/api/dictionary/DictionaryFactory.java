package zad1.api.dictionary;

import zad1.api.dictionary.translations.TEI;
import zad1.api.dictionary.translations.files.LanguagesEnum;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DictionaryFactory {

    private static Unmarshaller unmarshaller;
    private static Map<LanguagesEnum, DictionaryInterface> dictionaries = new LinkedHashMap<>();

    static {
        try {
            unmarshaller = JAXBContext.newInstance(TEI.class).createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static DictionaryInterface createDictionary(LanguagesEnum languageCode) throws JAXBException {
        if (dictionaries.containsKey(languageCode)) {
            return dictionaries.get(languageCode);
        }

        try (
                InputStream inputStream = DictionaryFactory.class.getResource("translations/files/PL-" + languageCode.name() + ".xml").openStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            dictionaries.putIfAbsent(languageCode, new Dictionary((TEI) unmarshaller.unmarshal(bufferedInputStream)));
        } catch (IOException ignored) {
        }

        return dictionaries.get(languageCode);
    }
}
