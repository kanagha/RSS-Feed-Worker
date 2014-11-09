package com.rss.worker;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.rss.common.Article;

public class FeedData implements Serializable{
	String etag;
	Deque<Article> articles;	
	
	/*@SuppressWarnings("unchecked")
	FeedData(String jsonString) throws ParseException {
		JSONParser parser =  new JSONParser();
		Object object = parser.parse(jsonString);
		JSONObject jsonObject =(JSONObject)object;
		etag = (String) jsonObject.get("ETAG");
		articles = (Deque<Article>) jsonObject.get("ArticlesList");
	}*/
	
	FeedData(String gsonString) {
		Gson gson = new Gson();
		FeedData data = gson.fromJson(gsonString, FeedData.class);
		etag = data.etag;
		articles = data.articles;
	}
	
	public FeedData() {
		articles = new LinkedList<Article>();
	}

	/*public String serializeToJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ETAG", etag);		
		jsonObject.put("ArticlesList", articles);
		return jsonObject.toJSONString();
	}*/
	
	public String serializeToJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
