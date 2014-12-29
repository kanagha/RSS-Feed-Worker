package com.rss.worker.cache;

import java.util.List;

import redis.clients.jedis.ShardedJedis;

import com.rss.common.Article;
import com.rss.worker.feedfetcher.FeedData;

public class RSSFeedCacheProvider implements IRSSFeedCacheProvider {
	
	private static final int NOOFENTRIES = 50;

	public FeedData getInfoForUrl(String url) {
		//Jedis jedis = new Jedis("localhost");
		ShardedJedis jedis = CacheInitializer.getInstance().getResource();
		String feedDataString = jedis.get(url);
		FeedData data = null;
		if (feedDataString != null) {
			data = new FeedData(feedDataString);
		}		
		return data;
	}

	@SuppressWarnings("rawtypes")
	public void addNewArticlesForUrl(String url, List<Article> newArticles,
			String etag) {
		FeedData data = getInfoForUrl(url);
		if (data == null) {
			data = new FeedData();
		}
		for (Article article : newArticles) {
			//TODO PLEASE FIX IT!!!!!!!!!!!!!!!!!!!!!
			/*data.articles.offerFirst(article);
			if (data.articles.size() > NOOFENTRIES) {
				data.articles.pollLast();
			}*/
		}
		
		// Now add it to redis
		// Jedis jedis = new Jedis("localhost");
		ShardedJedis jedis = CacheInitializer.getInstance().getResource();
		jedis.set(url, data.serializeToJson());
	}
}