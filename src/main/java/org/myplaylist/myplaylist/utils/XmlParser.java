package org.myplaylist.myplaylist.utils;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;

public interface XmlParser {

    <T> T from(String xmlString, Class<T> tClass) throws JAXBException, FileNotFoundException;
}
