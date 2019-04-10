package zad1.api.dictionary;

import zad1.api.dictionary.translations.files.LanguagesEnum;

import javax.xml.bind.JAXBException;
import java.net.InetSocketAddress;

public final class ServerConfiguration {

    private final Integer port;
    private final LanguagesEnum languageCode;
    private final Integer threadsNumbers;
    private DictionaryInterface dictionary;

    public ServerConfiguration(Integer port, LanguagesEnum languageCode, Integer threadsNumbers) {
        this.port = port;
        this.languageCode = languageCode;
        this.threadsNumbers = threadsNumbers;

        try {
            this.dictionary = DictionaryFactory.createDictionary(languageCode);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public DictionaryInterface getDictionary() {
        return dictionary;
    }

    public LanguagesEnum getLanguageCode() {
        return languageCode;
    }

    public Integer getThreadsNumbers() {
        return threadsNumbers;
    }

    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(this.port);
    }
}
