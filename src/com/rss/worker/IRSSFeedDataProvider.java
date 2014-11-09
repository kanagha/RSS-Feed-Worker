package com.rss.worker;


import java.util.List;

import com.rss.common.Article;

public interface IRSSFeedDataProvider {

	public FeedData getInfoForUrl(String url);
	
	public void addNewArticlesForUrl(String url, List<Article> newArticles, String etag);
	
}
