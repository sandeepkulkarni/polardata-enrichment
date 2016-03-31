package com.polarenrichment.tagratio;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


public class StanfordRegexNER {

  //private static final Logger LOG = LoggerFactory.getLogger(StanfordRegexNER.class);

  @Test
  public void extractNerAndSweet() {
    //LOG.info("Starting Stanford NLP");

    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and
    Properties props = new Properties();
    boolean useRegexner = true;
    if (useRegexner) {
      props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
      props.put("regexner.mapping", "resource/regexner.txt");
    } 
    else {
      props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
    }
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    
    String inputDirPath = "/Users/AravindMac/Desktop/polardata_json/audio_mpeg_test";
    

    	File root = new File(inputDirPath);
    	//System.out.println(root);
        
    	File[] listDir = root.listFiles();
    	int count = 0;
        	for(File filename : listDir){
        	    try {
        		count+=1;
        		System.out.println(count);
        		if(!filename.getName().equals(".DS_Store")){
        			
        		String absoluteFilename = filename.getAbsolutePath().toString();
        		
        		JSONParser parser = new JSONParser();
        	    String text="";
        	    
        	    TreeSet<String> measurementSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> unitSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> tset1 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> tset2 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> tset3 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> tset4 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> tset5 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    TreeSet<String> tset6 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        	    
        	    HashMap<String,TreeSet<String>> outerhmap1 = new HashMap<String,TreeSet<String>>();
        	    HashMap<String,TreeSet<String>> outerhmap2 = new HashMap<String,TreeSet<String>>();
        	    
        	    JSONArray array = new JSONArray();
        		
        		//System.out.println(absoluteFilename);
	            Object obj = parser.parse(new FileReader(absoluteFilename));
	            
	            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(absoluteFilename)));
	                
	            JSONObject jsonObject = (JSONObject) obj;
	           // System.out.println(jsonObject.toJSONString());
	            text = (String) jsonObject.get("content");
	            //text = text.replaceAll("\\r\\n&lt;&gt;", "");
	            String[] test = text.split("\n");
	         //   for(String s : test)
	           // 	System.out.println(s);
	            
        		
    //String degree = "\u00B0";
    /*String[] tests =
        {
            "7 "+degree+"C is a temperature"
        };*/
    List tokens = new ArrayList<>();

    for (String s : test) {

      Annotation document = new Annotation(s);
      pipeline.annotate(document);

      List<CoreMap> sentences = document.get(SentencesAnnotation.class);
      StringBuilder sb = new StringBuilder();
      
      for (CoreMap sentence : sentences) {
        // traversing the words in the current sentence, "O" is a sensible default to initialize tokens
        String prevNeToken = "O";
        String currNeToken = "O";
        boolean newToken = true;
        for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
          currNeToken = token.get(NamedEntityTagAnnotation.class);
          String word = token.get(TextAnnotation.class);
          //if(!(currNeToken.equals("O")||currNeToken.equals("ORGANIZATION")||currNeToken.equals("LOCATION")||currNeToken.equals("DATE")||currNeToken.equals("NUMBER")||currNeToken.equals("MISC")||currNeToken.equals("PERSON")||currNeToken.equals("TIME")||currNeToken.equals("ORDINAL")||currNeToken.equals("DURATION")||currNeToken.equals("PERCENT"))){
        	//  System.out.println(word+" : "+currNeToken);
          //}
          if((currNeToken.equals("MEASUREMENT")||currNeToken.equals("UNIT")||currNeToken.equals("REPRESENTATION")||currNeToken.equals("REALM")||currNeToken.equals("PROCESS")||currNeToken.equals("PHENOMENA")||currNeToken.equals("HUMAN ACTIVITIES")||currNeToken.equals("MATTER"))){
        	 // System.out.println(word+" : "+currNeToken);
        	  
        	  if(currNeToken.equals("MEASUREMENT")){
        		  measurementSet.add(word);
        	  }
        	  else if(currNeToken.equals("UNIT")){
        		  unitSet.add(word);
        	  }
        	  else if(currNeToken.equals("REPRESENTATION")){
        		  tset1.add(word);
        	  }
        	  else if(currNeToken.equals("REALM")){
        		  tset2.add(word);
        	  }
        	  else if(currNeToken.equals("PROCESS")){
        		  tset3.add(word);
        	  }
        	  else if(currNeToken.equals("PHENOMENA")){
        		  tset4.add(word);
        	  }
        	  else if(currNeToken.equals("HUMAN ACTIVITIES")){
        		  tset5.add(word);
        	  }
        	  else if(currNeToken.equals("MATTER")){
        		  tset6.add(word);
        	  }
        	  
        	  
          }
          //if(!currNeToken.equals("O")){
        	//  System.out.println(word+" : "+currNeToken);
          //}
          
          if (currNeToken.equals("O")) {
            // LOG.debug("Skipping '{}' classified as {}", word, currNeToken);
            if (!prevNeToken.equals("O") && (sb.length() > 0)) {
              handleEntity(prevNeToken, sb, tokens);
              //System.out.println(prevNeToken);
              newToken = true;
            }
            continue;
          }

          if (newToken) {
            prevNeToken = currNeToken;
            newToken = false;
            sb.append(word);
            //System.out.println(prevNeToken);
            continue;
          }

          if (currNeToken.equals(prevNeToken)) {
            sb.append(" " + word);
          } else {
            handleEntity(prevNeToken, sb, tokens);
            newToken = true;
          }
          prevNeToken = currNeToken;
          //System.out.println(prevNeToken);
        }
      }
     // LOG.info("We extracted {} tokens of interest from the input text", tokens.size());
    }
    if(!measurementSet.isEmpty()){
    	outerhmap1.put("MEASUREMENT", measurementSet);
    }
    if(!unitSet.isEmpty()){
    	outerhmap1.put("UNIT", unitSet);
    }
    
    if(!tset1.isEmpty()){
    	outerhmap2.put("REPRESENTATION", tset1);
    }
    if(!tset2.isEmpty()){
    	outerhmap2.put("REALM", tset2);
    }
    if(!tset3.isEmpty()){
    	outerhmap2.put("PROCESS", tset3);
    }
    if(!tset4.isEmpty()){
    	outerhmap2.put("PHENOMENA", tset4);
    }
    if(!tset5.isEmpty()){
    	outerhmap2.put("HUMAN ACTIVITIES", tset5);
    }
    if(!tset6.isEmpty()){
    	outerhmap2.put("MATTER", tset6);
    }
    
    array.put(outerhmap1);
    if(!outerhmap1.isEmpty()){
    	jsonObject.put("STANFORD_NER", array);
    }
    
    array =  new JSONArray();
    array.put(outerhmap2);
    
    if(!outerhmap2.isEmpty()){
    	jsonObject.put("SWEET", array);
    }
    System.out.println(jsonObject.toJSONString());
    bw.write(jsonObject.toJSONString());
    bw.close();
    
   }
  
 } catch (Exception e) {
    			e.printStackTrace();
    			continue;
    		}
    }
  }
  private void handleEntity(String inKey, StringBuilder inSb, List inTokens) {
    //LOG.info("'{}' is a {}", inSb, inKey);
    inTokens.add(new EmbeddedToken(inKey, inSb.toString()));
    inSb.setLength(0);
  }


}
class EmbeddedToken {

  private String name;
  private String value;

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public EmbeddedToken(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }
}