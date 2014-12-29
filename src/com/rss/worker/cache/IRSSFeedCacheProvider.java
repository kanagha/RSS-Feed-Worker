package com.rss.worker.cache;

import java.util.List;

import com.rss.common.Article;
import com.rss.worker.feedfetcher.FeedData;

public interface IRSSFeedCacheProvider {

	public FeedData getInfoForUrl(String url);
	
	public void addNewArticlesForUrl(String url, List<Article> newArticles, String etag);
	
}
