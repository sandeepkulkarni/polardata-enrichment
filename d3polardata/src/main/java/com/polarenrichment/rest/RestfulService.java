package com.polarenrichment.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.polarenrichment.dto.BarChartDTO;
import com.polarenrichment.dto.ChildDTO;
import com.polarenrichment.dto.LineChartDTO;
import com.polarenrichment.dto.ParentDTO;
import com.polarenrichment.dto.RootParentDTO;
import com.polarenrichment.dto.WordCloudDTO;
import com.polarenrichment.solr.QuerySolr;	

@Path("/api/service")
public class RestfulService {

	private static final String hostname = "localhost";
	private static final int solrPortNo = 8983;
	private static final String coreName = "polardata";

	@GET
	@Path("/getBarChart")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BarChartDTO> getBarChart(
			@QueryParam("q") String query,
			@QueryParam("rows") int rows) {
		System.out.println("------ Get bar chart solr --------- ");
		if(rows == 0){
			rows = 100;	//default rows
		}

		//	"\"*.*\"&fl=score%2CContentType"
		if(query == null || query.equals("")){
			query = "\"*.*\"" + "&fl=score,ContentType";
		}else{
			query = query + "&fl=score,ContentType";
		}
		System.out.println(query);

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr(query, rows);

		List<BarChartDTO> list = new ArrayList<BarChartDTO>();
		for(SolrDocument doc: response){
			String score = doc.getFieldValue("score").toString();
			String contentType = doc.getFieldValue("ContentType").toString();	

			BarChartDTO dto = new BarChartDTO();
			dto.setScore(Double.parseDouble(score));
			dto.setCount(1);
			if(contentType.contains(";")){
				dto.setMimeType(contentType.substring(0, contentType.indexOf(";")));				
			}else{
				dto.setMimeType(contentType);
			}
			list.add(dto);
		}
		System.out.println("returning records: "+list.size());	
		return list;
	}


	@GET
	@Path("/getWordCloud")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WordCloudDTO> getWordCloud(
			@QueryParam("rows") int rows) {
		System.out.println("------ Get Word Cloud solr ---------");
		if(rows == 0){
			rows = 100;	//default rows
		}

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr("-((Phenomena:N/A) AND (Matter:N/A) AND (Process:N/A) AND (Realm:N/A) AND (Representation:N/A) AND (HumanActivities:N/A))&fl=Phenomena,Matter,Process,Realm,Representation,HumanActivities",rows);
		Map<String,Integer> map = new HashMap<String,Integer>();
		System.out.println("Solr list size : "+response.size());
		for(SolrDocument doc: response) {
			String phenomena = doc.getFieldValue("Phenomena").toString();
			String matter = doc.getFieldValue("Matter").toString();	
			String process = doc.getFieldValue("Process").toString();
			String representation = doc.getFieldValue("Representation").toString();
			String humanActivities = doc.getFieldValue("HumanActivities").toString();
			String realm = doc.getFieldValue("Realm").toString();

			populateDataMap(phenomena, map);
			populateDataMap(matter, map);
			populateDataMap(process, map);
			populateDataMap(representation, map);
			populateDataMap(humanActivities, map);
			populateDataMap(realm, map);
		}

		List<WordCloudDTO> list = new ArrayList<WordCloudDTO>();
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			String word = entry.getKey();
			int freq = entry.getValue();
			//For visualization purpose: scale all values
			freq = freq + 10;			
			WordCloudDTO dto = new WordCloudDTO(word, freq);
			list.add(dto);
		}

		System.out.println("returning records: "+list.size());	
		return list;
	}


	@GET
	@Path("/getLineChart")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LineChartDTO> getLineChart(
			@QueryParam("q") String query,
			@QueryParam("rows") int rows) {
		System.out.println("------ Get line chart solr ---------");
		if(rows == 0){
			rows = 100;	//default rows
		}		
		//	"NOT(Units:N/A)"
		if(query == null || query.equals("")){
			query = "NOT(Units:\"N/A\")&fl=Units";
		}else{
			query = query + "&fl=Units";
		}
		System.out.println(query);

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr(query,rows);

		Map<String, Integer> map = new HashMap<String, Integer>();
		for(SolrDocument doc: response){
			String units = doc.getFieldValue("Units").toString();

			populateDataMap(units, map);			
		}
		List<LineChartDTO> list = new ArrayList<LineChartDTO>();
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			String unit = entry.getKey();
			int count = entry.getValue();
			LineChartDTO dto = new LineChartDTO(unit, count);
			list.add(dto);
		}
		return list;
	}


	@GET
	@Path("/getChordDiagram")
	@Produces(MediaType.APPLICATION_JSON)
	public int[][] getChordDiagram(@QueryParam("rows") int rows) {
		System.out.println("------ Get getChordDiagram ---------");
		if(rows == 0){
			rows = 100;	//default rows
		}

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr("-((Phenomena:N/A) AND (Matter:N/A) AND (Process:N/A) AND (Realm:N/A) AND (Representation:N/A) AND (HumanActivities:N/A))&fl=Phenomena,Matter,Process,Realm,Representation,HumanActivities,ContentType",rows);

		Map<String, Integer> map0 = new TreeMap<String, Integer>();
		Map<String, Integer> map1 = new TreeMap<String, Integer>();
		Map<String, Integer> map2 = new TreeMap<String, Integer>();
		Map<String, Integer> map3 = new TreeMap<String, Integer>();
		Map<String, Integer> map4 = new TreeMap<String, Integer>();
		Map<String, Integer> map5 = new TreeMap<String, Integer>();

		//Pre-populate map with measures
		map0.put("application/pdf", 0);map0.put("application/xml", 0);map0.put("text/html", 0);
		map1.put("application/pdf", 0);map1.put("application/xml", 0);map1.put("text/html", 0);
		map2.put("application/pdf", 0);map2.put("application/xml", 0);map2.put("text/html", 0);
		map3.put("application/pdf", 0);map3.put("application/xml", 0);map3.put("text/html", 0);
		map4.put("application/pdf", 0);map4.put("application/xml", 0);map4.put("text/html", 0);
		map5.put("application/pdf", 0);map5.put("application/xml", 0);map5.put("text/html", 0);

		for(SolrDocument doc: response){
			String humanActivities = doc.getFieldValue("HumanActivities").toString();
			String matter = doc.getFieldValue("Matter").toString();
			String phenomena = doc.getFieldValue("Phenomena").toString();
			String process = doc.getFieldValue("Process").toString();
			String realm = doc.getFieldValue("Realm").toString();	
			String representation = doc.getFieldValue("Representation").toString();

			String contentType = doc.getFieldValue("ContentType").toString();
			if(contentType.contains(";")){
				contentType = contentType.substring(0,contentType.indexOf(";"));
			}

			if(contentType.equals("application/pdf")) {		//PDF
				if(!humanActivities.equals("N/A")) {
					populateMap(contentType, map0);
				}
				if(!matter.equals("N/A")) {
					populateMap(contentType, map1);
				}
				if(!phenomena.equals("N/A")) {
					populateMap(contentType, map2);
				}
				if(!process.equals("N/A")) {
					populateMap(contentType, map3);
				}
				if(!realm.equals("N/A")) {
					populateMap(contentType, map4);
				}
				if(!representation.equals("N/A")) {
					populateMap(contentType, map5);
				}
			}
			if(contentType.equals("application/xml")) {		//XML
				if(!humanActivities.equals("N/A")) {
					populateMap(contentType, map0);
				}
				if(!matter.equals("N/A")) {
					populateMap(contentType, map1);
				}
				if(!phenomena.equals("N/A")) {
					populateMap(contentType, map2);
				}
				if(!process.equals("N/A")) {
					populateMap(contentType, map3);
				}
				if(!realm.equals("N/A")) {
					populateMap(contentType, map4);
				}
				if(!representation.equals("N/A")) {
					populateMap(contentType, map5);
				}
			}
			if(contentType.equals("text/html")) {		//HTML
				if(!humanActivities.equals("N/A")) {
					populateMap(contentType, map0);
				}
				if(!matter.equals("N/A")) {
					populateMap(contentType, map1);
				}
				if(!phenomena.equals("N/A")) {
					populateMap(contentType, map2);
				}
				if(!process.equals("N/A")) {
					populateMap(contentType, map3);
				}
				if(!realm.equals("N/A")) {
					populateMap(contentType, map4);
				}
				if(!representation.equals("N/A")) {
					populateMap(contentType, map5);
				}
			}


		}

		System.out.println("Map: "+map0 + " size: "+map0.size());
		System.out.println("Map: "+map1 + " size: "+map1.size());
		System.out.println("Map: "+map2 + " size: "+map2.size());
		System.out.println("Map: "+map3 + " size: "+map3.size());
		System.out.println("Map: "+map4 + " size: "+map4.size());
		System.out.println("Map: "+map5 + " size: "+map5.size());

		//{HumanActivities=0, Matter=0, Phenomena=0, Process=0, Realm=0, Representation=0, application/pdf=727, application/xml=11, text/html=2035}
		String[] keys = {"application/pdf", "application/xml", "text/html"};
		int[][] arr = new int[9][9];
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(i < 6 || j < 6){
					arr[i][j] = 0;
				}
				if(i == 0 && j == 6)arr[i][j] = map0.get(keys[0]);
				if(i == 0 && j == 7)arr[i][j] = map0.get(keys[1]);
				if(i == 0 && j == 8)arr[i][j] = map0.get(keys[2]);

				if(i == 1 && j == 6)arr[i][j] = map1.get(keys[0]);
				if(i == 1 && j == 7)arr[i][j] = map1.get(keys[1]);
				if(i == 1 && j == 8)arr[i][j] = map1.get(keys[2]);

				if(i == 2 && j == 6)arr[i][j] = map2.get(keys[0]);
				if(i == 2 && j == 7)arr[i][j] = map2.get(keys[1]);
				if(i == 2 && j == 8)arr[i][j] = map2.get(keys[2]);

				if(i == 3 && j == 6)arr[i][j] = map3.get(keys[0]);
				if(i == 3 && j == 7)arr[i][j] = map3.get(keys[1]);
				if(i == 3 && j == 8)arr[i][j] = map3.get(keys[2]);

				if(i == 4 && j == 6)arr[i][j] = map4.get(keys[0]);
				if(i == 4 && j == 7)arr[i][j] = map4.get(keys[1]);
				if(i == 4 && j == 8)arr[i][j] = map4.get(keys[2]);

				if(i == 5 && j == 6)arr[i][j] = map5.get(keys[0]);
				if(i == 5 && j == 7)arr[i][j] = map5.get(keys[1]);
				if(i == 5 && j == 8)arr[i][j] = map5.get(keys[2]);

				if(i == 6 && j == 0)arr[i][j] = map0.get(keys[0]);
				if(i == 6 && j == 1)arr[i][j] = map1.get(keys[0]);
				if(i == 6 && j == 2)arr[i][j] = map2.get(keys[0]);
				if(i == 6 && j == 3)arr[i][j] = map3.get(keys[0]);
				if(i == 6 && j == 4)arr[i][j] = map4.get(keys[0]);
				if(i == 6 && j == 5)arr[i][j] = map5.get(keys[0]);

				if(i == 7 && j == 0)arr[i][j] = map0.get(keys[1]);
				if(i == 7 && j == 1)arr[i][j] = map1.get(keys[1]);
				if(i == 7 && j == 2)arr[i][j] = map2.get(keys[1]);
				if(i == 7 && j == 3)arr[i][j] = map3.get(keys[1]);
				if(i == 7 && j == 4)arr[i][j] = map4.get(keys[1]);
				if(i == 7 && j == 5)arr[i][j] = map5.get(keys[1]);

				if(i == 8 && j == 0)arr[i][j] = map0.get(keys[2]);
				if(i == 8 && j == 1)arr[i][j] = map1.get(keys[2]);
				if(i == 8 && j == 2)arr[i][j] = map2.get(keys[2]);
				if(i == 8 && j == 3)arr[i][j] = map3.get(keys[2]);
				if(i == 8 && j == 4)arr[i][j] = map4.get(keys[2]);
				if(i == 8 && j == 5)arr[i][j] = map5.get(keys[2]);				

			}
		}


		for(int i=0; i<arr.length; i++){
			for(int j=0; j<arr.length; j++){
				System.out.print(arr[i][j] + "  ");
			}
			System.out.println();
		}

		return arr;
	}

	private void populateDataMap(String input, Map<String, Integer> map) {
		input = input.toLowerCase();
		if(!input.equalsIgnoreCase("N/A")) {
			if(input.contains(",")) {
				String[] array = input.split(",");
				for(String key : array){
					if(map.containsKey(key)) {
						int count = map.get(key);
						map.put(key, count + 1);
					} else {
						map.put(key, 1);
					}
				}					
			} else {
				if(map.containsKey(input)) {
					int count = map.get(input);
					map.put(input, count + 1);
				} else {
					map.put(input, 1);
				}
			}
		}
	}


	@GET
	@Path("/getDendrogram")
	@Produces(MediaType.APPLICATION_JSON)
	public RootParentDTO getDendrogram(
			@QueryParam("q") String query,
			@QueryParam("rows") int rows) {
		System.out.println("------- getDendrogram -------");
		if(rows == 0){
			rows = 100;	//default rows
		}		
		//	"Scholar_tags:*.*"
		if(query == null || query.equals("")){
			query = "Scholar_tags:*.*";
		}else{
			query = "Grobid_authors:" + query;
		}
		System.out.println(query);

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr(query, rows);
		Map<String,Map<String, Integer>> map = new HashMap<String,Map<String, Integer>>();
		for(SolrDocument doc: response){
			Collection<Object> scholarTags = doc.getFieldValues("Scholar_tags");
			String authors = doc.getFieldValue("Grobid_authors").toString();

			if(authors.contains(",")){
				authors = authors.split(",")[0];
			}else if(authors.contains(" ")){
				authors = authors.split(" ")[0];
			}

			if(map.containsKey(authors)){
				Map<String, Integer> submap = map.get(authors);
				for(Object obj : scholarTags){
					String str = (String) obj;
					if(str != null && !str.equals("") && !str.equals("N/A")){
						str = str.split(",")[2];
						submap = populateMap(str, submap);
					}
				}				
				map.put(authors, submap);
			}else{
				Map<String, Integer> submap = new HashMap<String, Integer>();
				for(Object obj : scholarTags){
					String str = (String) obj;
					if(str != null && !str.equals("") && !str.equals("N/A")){
						System.out.println("str :"+ str);
						str = str.split(",")[2];
						submap = populateMap(str, submap);
					}
				}
				map.put(authors, submap);
			}
		}

		//Create JSON from map
		List<ParentDTO> mainList = new ArrayList<ParentDTO>();
		for(Map.Entry<String, Map<String, Integer>> entryMap : map.entrySet()){
			String mimetype = entryMap.getKey();
			Map<String, Integer> submap = entryMap.getValue();

			List<ChildDTO> listChildDto = new ArrayList<ChildDTO>();
			for(Map.Entry<String, Integer> entry : submap.entrySet()){
				String name = entry.getKey();
				int size = entry.getValue();
				ChildDTO childDto = new ChildDTO(name, size);
				listChildDto.add(childDto);				
			}

			ParentDTO parentDto = new ParentDTO(mimetype, listChildDto);
			mainList.add(parentDto);
		}

		RootParentDTO root = new RootParentDTO("Authors", mainList);
		return root;
	}


	private Map<String, Integer> populateMap(String name, Map<String, Integer> map){
		if(map.containsKey(name)){
			int count = map.get(name);
			map.put(name, count + 1);				
		}else{
			map.put(name, 1);
		}
		return map;
	}

	@GET
	@Path("/getBubbleChart")
	@Produces(MediaType.APPLICATION_JSON)
	public RootParentDTO getBubbleChart(
			@QueryParam("q") String query,
			@QueryParam("rows") int rows) {		
		System.out.println("------- bubble chart -------");
		if(rows == 0){
			rows = 100;	//default rows
		}		
		//	"NOT(Geographical_Name:N/A)&fl=ContentType,Geographical_Name"
		if(query == null || query.equals("")){
			query = "NOT(Geographical_Name:N/A)&fl=ContentType,Geographical_Name";
		}else{
			query = "Geographical_Name:"+ query + "&fl=ContentType,Geographical_Name";
		}
		System.out.println(query);		

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr(query,rows);

		Map<String,Map<String, Integer>> map = new HashMap<String,Map<String, Integer>>();
		for(SolrDocument doc: response){
			String mimeType = doc.getFieldValue("ContentType").toString().toLowerCase();
			String geographicName = doc.getFieldValue("Geographical_Name").toString().toLowerCase();

			if(map.containsKey(mimeType)){
				Map<String, Integer> submap = map.get(mimeType);
				submap = populateMap(geographicName, submap);
				map.put(mimeType, submap);
			}else{
				Map<String, Integer> submap = new HashMap<String, Integer>();
				submap = populateMap(geographicName, submap);
				map.put(mimeType, submap);
			}
		}

		//Create JSON from map
		List<ParentDTO> mainList = new ArrayList<ParentDTO>();
		for(Map.Entry<String, Map<String, Integer>> entryMap : map.entrySet()){
			String mimetype = entryMap.getKey();
			Map<String, Integer> submap = entryMap.getValue();

			List<ChildDTO> listChildDto = new ArrayList<ChildDTO>();
			for(Map.Entry<String, Integer> entry : submap.entrySet()){
				String name = entry.getKey();
				int size = entry.getValue();
				ChildDTO childDto = new ChildDTO(name, size);
				listChildDto.add(childDto);				
			}

			ParentDTO parentDto = new ParentDTO(mimetype, listChildDto);
			mainList.add(parentDto);
		}

		RootParentDTO root = new RootParentDTO("Content-Types", mainList);
		return root;
	}

}
