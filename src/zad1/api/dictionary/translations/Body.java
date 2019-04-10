package zad1.api.dictionary.translations;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Body {
    @XmlElement
    public List<Entry> entry;
}
