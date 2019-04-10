package zad1.api.dictionary.translations;

import javax.xml.bind.annotation.XmlElement;

public class Entry {
    @XmlElement
    public Form form;

    @XmlElement
    public Sense sense;
}
