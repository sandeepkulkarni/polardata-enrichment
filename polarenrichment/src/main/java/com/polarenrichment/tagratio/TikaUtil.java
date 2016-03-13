package com.polarenrichment.tagratio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaUtil {

	private static String filePath = "D:\\599test\\input\\test.html";
	
	public String parseToHTML() throws IOException, SAXException, TikaException {
	    ContentHandler handler = new ToXMLContentHandler();
	 
	    InputStream inputStream = new FileInputStream(new File(filePath));
	    File file = new File(filePath);
	    
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    
	    try (InputStream stream = inputStream) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	    //parse method parameters
	    /*Parser parser = new AutoDetectParser();
	    BodyContentHandler handler = new BodyContentHandler();
	    Metadata metadata = new Metadata();
	    FileInputStream inputstream = new FileInputStream(file);
	    ParseContext context = new ParseContext();
	    
	    //parsing the file
	    parser.parse(inputstream, handler, metadata, context);
	    System.out.println("File content : " + handler.toString());
	    return handler.toString();*/
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
