package com.polarenrichment.solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;


public class QuerySolr {
	HttpSolrServer server;
	private static final String hostname = "localhost";
	private static final int solrPortNo = 8983;
	private static final String coreName = "polardata";

	public QuerySolr(String host, int port, String collection){
		server= new HttpSolrServer("http://"+host+":"+port+"/solr/"+collection+"/");
		server.setParser(new XMLResponseParser());
	}


	public SolrDocumentList getQueryFromSolr(String query,int rows){
		// specify the get request
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.setRows(rows);

		solrQuery.setHighlight(true);

		solrQuery.setHighlightRequireFieldMatch(true);
		QueryResponse response = null;
		try {
			response = server.query(solrQuery);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SolrDocumentList docs = response.getResults();
		return docs;
	}

	public static void main(String args[]) throws SolrServerException{

		QuerySolr querySolr=new QuerySolr(hostname,solrPortNo,coreName);
		SolrDocumentList response=querySolr.getQueryFromSolr("Scholar_tags:*.*",100);
		System.out.println(response.size());
	}
}



