package com.polarenrichment.tagratio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

class TTR {
	
	private double[] linkTagList;
	public Object outputDirPath;
	private static ArrayList<String> arrList = new ArrayList<String>(); 
	
	public double[] parseFile(String html,String filepath) {
		
		html = html.replaceAll("(?s)<!--.*?-->", "");
		html = html.replaceAll("(?s)<script.*?>.*?</script>", "");
		html = html.replaceAll("(?s)<SCRIPT.*?>.*?</SCRIPT>", "");
		html = html.replaceAll("(?s)<style.*?>.*?</style>", "");

		try{
			// To get the filename from absolute path
			Path p1 = Paths.get(filepath);
			String filename = p1.getFileName().toString();
			
			System.out.println("Output File :"+outputDirPath+"/"+filename);
			
			//Create file in output directory
			FileWriter fw = new FileWriter(new File(outputDirPath+"/"+filename));
			BufferedWriter bw = new BufferedWriter(fw);
			
			Parser p = new Parser(html);
			NodeList nl = p.parse(null);
			//NodeList list = nl.extractAllNodesThatMatch(new TagNameFilter("TITLE"), true);
		
			BufferedReader br = new BufferedReader(new StringReader(nl.toHtml()));
			int numLines = 0;
			while (br.readLine() != null) {
				numLines++;
			}
			br.close();

			if (numLines == 0) {
				return linkTagList;				
			}


			linkTagList = new double[numLines];

			String line;
			br = new BufferedReader(new StringReader(nl.toHtml()));
			for (int i = 0; i < linkTagList.length; i++) {
				line = br.readLine();
				line = line.trim();
				if (line.equals("")) {
					continue;
				}
				linkTagList[i] = computeTextToTagRatio(line);
				if(linkTagList[i]!=0){
				System.out.println("Tag Ratio : "+linkTagList[i]);
					//System.out.println(line);
				bw.write(line);
				bw.newLine();
				}
			}
			
			br.close();
			bw.close();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
			return linkTagList;
		}
		return linkTagList;
	}

	/**
	 * Calculate Text to Tag Ratio for line
	 * @param line
	 * @return
	 */
	private double computeTextToTagRatio(String line) {
		int tag = 0;
		int text = 0;

		for (int i = 0; i >= 0 && i < line.length(); i++) {
			if (line.charAt(i) == '<') {	//start tag
				tag++;
				i = line.indexOf('>', i);
				if (i == -1) {
					break;
				}
			} else if (tag == 0 && line.charAt(i) == '>') {	//close tag
				text = 0;
				tag++;
			} else {		//just text
				text++;
			}

		}
		if (tag == 0) {
			tag = 1;
		}
		if(text != 0){
			System.out.println("\nLine : "+line+"\nTag : "+tag+"\n"+"Text : "+text);
		}
		return (double) text / (double) tag;
	}
	
	/**
	 * Take input XHTML file path and add Absolute File Path to a List
	 * @param path
	 * @return list
	 */
	public static ArrayList<String> walk(String path) {

		File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return arrList;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else {
            	if(f.toString().toLowerCase().endsWith(".xhtml")){
            		
                //System.out.println( "File:" + f.getAbsoluteFile());
                arrList.add(f.getAbsoluteFile().toString());
                }
            }
        }
        return arrList;
    }

}

public class TextToTagRatio {
	
	private static String inputDirPath = "D:\\599test\\input";
	private static String outputDirPath = "D:\\599test\\output";
	
	public static void main(String[] args) {
		TTR ttr = new TTR();
		
		ArrayList<String> arrList = null;
		arrList = TTR.walk(inputDirPath);	//Input Directory Path
		ttr.outputDirPath = outputDirPath;	// Output Directory Path
		
		if(arrList != null && !arrList.isEmpty()) {
			for(String filePath : arrList) {
					
					BufferedReader br = null;
					try {
						br = new BufferedReader(new FileReader(filePath));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					
					String line = null;
					String lines = "";
					try {
						while ((line = br.readLine()) != null) {
							lines+=line;
							lines+="\n";
						 }
						System.out.println(lines);
						ttr.parseFile(lines,filePath);
					} 
					catch (IOException e) {
						e.printStackTrace();
					}			
			}
		}
	}
	
	
}

