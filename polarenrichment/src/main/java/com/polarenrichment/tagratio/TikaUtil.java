package com.polarenrichment.tagratio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaUtil {

	public String parseToHTML(String filePath) throws IOException, SAXException, TikaException {
		ContentHandler handler = new ToXMLContentHandler();

		InputStream inputStream = new FileInputStream(new File(filePath));

		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();

		try (InputStream stream = inputStream) {
			parser.parse(stream, handler, metadata);
			return handler.toString();
		}	    
	}

}
