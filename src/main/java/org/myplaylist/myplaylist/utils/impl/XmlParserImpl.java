package org.myplaylist.myplaylist.utils.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.myplaylist.myplaylist.utils.XmlParser;
import org.springframework.stereotype.Component;

import java.io.StringReader;


@Component
public class XmlParserImpl implements XmlParser {

    @Override
    public <T> T from(String xmlString, Class<T> tClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(tClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xmlString);
        return tClass.cast(unmarshaller.unmarshal(reader));
    }
}
