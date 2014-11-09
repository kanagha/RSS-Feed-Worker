package com.rss.worker;

import java.util.List;

import org.json.simple.parser.ParseException;

import redis.clients.jedis.Jedis;

import com.rss.common.Article;

public class RSSFeedDataProvider implements IRSSFeedDataProvider {
	
	private static final int NOOFENTRIES = 50;

	public FeedData getInfoForUrl(String url) {
		Jedis jedis = new Jedis("localhost");
		String feedDataString = jedis.get(url);
		FeedData data = null;
		if (feedDataString != null) {
			data = new FeedData(feedDataString);
		}			
		jedis.close();
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
			data.articles.offerFirst(article);
			if (data.articles.size() > NOOFENTRIES) {
				data.articles.pollLast();
			}
		}
		
		// Now add it to redis
		Jedis jedis = new Jedis("localhost");
		jedis.set(url, data.serializeToJson());
	}
}
