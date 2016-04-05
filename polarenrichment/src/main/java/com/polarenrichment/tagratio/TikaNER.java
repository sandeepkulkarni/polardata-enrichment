package com.polarenrichment.tagratio;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ner.NamedEntityParser;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TikaNER {

    public static final String CONFIG_FILE = "tika-config.xml";

    @Test
    public void extractTikaNER() throws Exception {
        System.setProperty(NamedEntityParser.SYS_PROP_NER_IMPL,CoreNLPNERecogniser.class.getName());

        TikaConfig config = new TikaConfig(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE));
        Tika tika = new Tika(config);

        JSONParser parser = new JSONParser();
        String text="";
        
        HashMap<Integer,String> hmap = new HashMap<Integer,String>();
        HashMap<String,HashMap<Integer,String>> outerhmap = new HashMap<String,HashMap<Integer,String>>();
        
        int index=0;
        //Input Directory Path
        String inputDirPath = "/Users/AravindMac/Desktop/samplejson";
        
        try {
 
        	File root = new File(inputDirPath);
        	File[] listDir = root.listFiles();
            for(File filename : listDir){
	        		
	        	if(!filename.getName().equals(".DS_Store")){
	        			
	        		String absoluteFilename = filename.getAbsolutePath().toString();
	        		
	        		System.out.println(absoluteFilename);
	        		 //Read the json file, parse and retrieve the text present in the content field.
		        	   
		            Object obj = parser.parse(new FileReader(absoluteFilename));
		            
		            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(absoluteFilename)));
		                
		            JSONObject jsonObject = (JSONObject) obj;
		            text = (String) jsonObject.get("content");
		            
		            Metadata md = new Metadata();
		            tika.parse(new ByteArrayInputStream(text.getBytes()), md);
		            
		            //Parse the content and retrieve the values tagged as the NER entities
		            HashSet<String> set = new HashSet<String>();
		            set.addAll(Arrays.asList(md.getValues("X-Parsed-By")));
		            
		            // Store values tagged as NER_PERSON
		            set.clear();
		            set.addAll(Arrays.asList(md.getValues("NER_PERSON")));
		            
		            hmap = new HashMap<Integer,String>();
		            index=0;
		            
		            for(Iterator<String> i = set.iterator();i.hasNext();){
		            	String f = i.next();
		            	hmap.put(index,f);
		            	index++;
		            }
		            
		            if(!hmap.isEmpty()){
		            	outerhmap.put("PERSON", hmap);
		            }
		            
		            // Store values tagged as NER_LOCATION
		            set.clear();
		            set.addAll(Arrays.asList(md.getValues("NER_LOCATION")));
		            hmap = new HashMap<Integer,String>();
		            index=0;
		            
		            for(Iterator<String> i = set.iterator();i.hasNext();){
		            	String f = i.next();
		            	hmap.put(index,f);
		            	index++;
		            }
		            
		            if(!hmap.isEmpty()){
		            	outerhmap.put("LOCATION", hmap);	
		            }
		            
		            //Store values tagged as NER_ORGANIZATION
		            set.clear();
		            set.addAll(Arrays.asList(md.getValues("NER_ORGANIZATION")));
		            
		            hmap = new HashMap<Integer,String>();
		            index=0;
		            
		            for(Iterator<String> i = set.iterator();i.hasNext();){
		            	String f = i.next();
		            	hmap.put(index,f);
		            	index++;
		            }
		            
		            if(!hmap.isEmpty()){
		            	outerhmap.put("ORGANIZATION", hmap);
		            }
		            
		            // Store values tagged as NER_DATE
		            set.clear();
		            set.addAll(Arrays.asList(md.getValues("NER_DATE")));
		            
		            hmap = new HashMap<Integer,String>();
		            index=0;
		            
		            for(Iterator<String> i = set.iterator();i.hasNext();){
		            	String f = i.next();
		            	hmap.put(index,f);
		            	index++;
		            }

		            if(!hmap.isEmpty()){
		            	outerhmap.put("DATE", hmap);
		            }
		            
		            JSONArray array = new JSONArray();
					array.put(outerhmap);
					jsonObject.put("NER", array);		//Add the NER entities to the json under NER key as a JSON array.
					
		            System.out.println(jsonObject);
		            
		            bw.write(jsonObject.toJSONString());	//Stringify thr JSON and write it back to the file 
		            bw.close();
		            
	        		}
	        	}
        	} catch (Exception e) {
        			e.printStackTrace();
        		}
      	}
}