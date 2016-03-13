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

	private static String filePath = "D:\\599test\\input\\2";
	
	public String parseToHTML() throws IOException, SAXException, TikaException {
	    ContentHandler handler = new ToXMLContentHandler();
		//BodyContentHandler handler = new BodyContentHandler();
	 
	    InputStream inputStream = new FileInputStream(new File(filePath));
	    //File file = new File(filePath);
	    
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    
	    try (InputStream stream = inputStream) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }	    
	}
	
	public static void main(String[] args) {
		
		TikaUtil tikaUtil = new TikaUtil();
		try {
			String output = tikaUtil.parseToHTML();
			System.out.println(output);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

	}

}
