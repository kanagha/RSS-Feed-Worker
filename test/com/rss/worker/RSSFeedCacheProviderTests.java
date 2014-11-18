package com.rss.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

import com.rss.common.Article;
import com.rss.worker.IRSSFeedFetcher.RSSFeedBody;

public class RSSFeedCacheProviderTests {
	public static void main(String args[]){
		Setup();
        CheckIfRecordIsEmpty();
		
	    AddRecordsAndTest();
	    
	    TearDown();
	}

	private static void TearDown() {
			
		
	}

	private static void Setup() {
		Jedis jedis = new Jedis("localhost");
		jedis.del("http://rss.cnn.com/rss/edition.rss");
		jedis.close();
	}

	private static void CheckIfRecordIsEmpty() {
		IRSSFeedCacheProvider dataProvider = new RSSFeedCacheProvider();
		FeedData data = dataProvider.getInfoForUrl("http://rss.cnn.com/rss/edition.rss");
		Assert.isNull(data);		
	}

	private static void AddRecordsAndTest() {
		IRSSFeedFetcher fetcher = new RSSFeedFetcher(new ResultParser());
		List<RSSFeedBody> list = new ArrayList<RSSFeedBody>();
		list.add(new RSSFeedBody("http://rss.cnn.com/rss/edition.rss", ""));
		Map<String, FeedData> articleList = fetcher.fetchFeeds(list);
		
		IRSSFeedCacheProvider dataProvider = new RSSFeedCacheProvider();
		for (RSSFeedBody feedBody : list) {
			String url = feedBody.URL;
			dataProvider.addNewArticlesForUrl(url, (List<Article>) articleList.get(url).articles, articleList.get(url).etag);
		}
		// Now assert
		
		for (RSSFeedBody feedBody : list) {
			FeedData data = dataProvider.getInfoForUrl(feedBody.URL);
			Assert.notEmpty(data.articles);
		}		
	}
}