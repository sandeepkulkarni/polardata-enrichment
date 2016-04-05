package com.polarenrichment.tagratio;

import java.io.BufferedWriter;
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

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


public class StanfordRegexNER {


  @Test
  public void extractNerAndSweet() {

    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and
    Properties props = new Properties();	// Configure the parser by setting the required properties
    boolean useRegexner = true;				// Use Stanford RegexNER parser
    if (useRegexner) {
      props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
      props.put("regexner.mapping", "resource/regexner.txt");
    } 
    else {
      props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
    }
    
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    
    //Input Directory Path
    String inputDirPath = "/Users/AravindMac/Desktop/polardata_json/application_xml_train";
    File root = new File(inputDirPath);
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
        	    
        			//Use TreeSet to insert unique and case insensitive elements for the measurements and SWEET tags
        			TreeSet<String> measurementSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        			TreeSet<String> unitSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    TreeSet<String> tset1 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    TreeSet<String> tset2 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    TreeSet<String> tset3 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    TreeSet<String> tset4 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    TreeSet<String> tset5 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    TreeSet<String> tset6 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	        	    
	        	    //Store the measurements and SWEET tags in separate hashmaps
	        	    HashMap<String,TreeSet<String>> outerhmap1 = new HashMap<String,TreeSet<String>>();
	        	    HashMap<String,TreeSet<String>> outerhmap2 = new HashMap<String,TreeSet<String>>();
        	    
	        	    JSONArray array = new JSONArray();
        		
	        	    Object obj = parser.parse(new FileReader(absoluteFilename));
	            
	        	    //Read the json file, parse and retrieve the text present in the content field.
	        	    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(absoluteFilename)));
	                
	        	    JSONObject jsonObject = (JSONObject) obj;
	        	    text = (String) jsonObject.get("content");
	        	    String[] test = text.split("\n");
        		
	        	    List tokens = new ArrayList<>();
	        	    for (String s : test) {

	        	    	Annotation document = new Annotation(s);
	        	    	pipeline.annotate(document);	//Annotate the document to get the entities for each word in the content.
	        	    	
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
          
	        	    			//Add the respective words to corresponding entity TreeSets for measurements and SWEEt tags
	        	    			if((currNeToken.equals("MEASUREMENT")||currNeToken.equals("UNIT")||currNeToken.equals("REPRESENTATION")||currNeToken.equals("REALM")||currNeToken.equals("PROCESS")||currNeToken.equals("PHENOMENA")||currNeToken.equals("HUMAN ACTIVITIES")||currNeToken.equals("MATTER"))){
        	  
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
          
	        	    			if (currNeToken.equals("O")) {
	        	    				if (!prevNeToken.equals("O") && (sb.length() > 0)) {
	        	    					handleEntity(prevNeToken, sb, tokens);
	        	    					newToken = true;
	        	    				}
	        	    				continue;
	        	    			}

	        	    			if (newToken) {
	        	    				prevNeToken = currNeToken;
	        	    				newToken = false;
	        	    				sb.append(word);
	        	    				continue;
	        	    			}

	        	    			if (currNeToken.equals(prevNeToken)) {
	        	    				sb.append(" " + word);
	        	    			} else {
	        	    				handleEntity(prevNeToken, sb, tokens);
	        	    				newToken = true;
	        	    			}
	        	    			prevNeToken = currNeToken;
	        	    		}
	        	    	}
     
	        	    }
	        	    
	        	    //Add the measurments and units TreeSets to a hashmap 
	        	    if(!measurementSet.isEmpty()){
	        	    	outerhmap1.put("MEASUREMENT", measurementSet);
	        	    }
	        	    if(!unitSet.isEmpty()){
	        	    	outerhmap1.put("UNIT", unitSet);
	        	    }
    
	        	    //Add all SWEET tag entities to a hashmap 
	        	    
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
	        	    bw.write(jsonObject.toJSONString());	//Stringify thr JSON and write it back to the file
	        	    bw.close();
    
        		}
  
        } catch (Exception e) {
    			e.printStackTrace();
    			continue;
    		}
    	}
  	}
  
  	private void handleEntity(String inKey, StringBuilder inSb, List inTokens) {
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