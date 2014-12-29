package com.rss.worker.feedfetcher;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.rss.common.Article;

public class FeedData implements Serializable{
	public String etag;
	public List<Article> articles;	
	
	public FeedData(String gsonString) {
		Gson gson = new Gson();
		FeedData data = gson.fromJson(gsonString, FeedData.class);
		etag = data.etag;
		articles = data.articles;
	}
	
	public FeedData() {
		articles = new LinkedList<Article>();
	}	
	
	public String serializeToJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
