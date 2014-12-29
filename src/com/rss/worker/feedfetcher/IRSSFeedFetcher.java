package com.rss.worker.feedfetcher;

import java.util.List;
import java.util.Map;

import com.rss.common.Article;

public interface IRSSFeedFetcher {
	
	public class RSSFeedUrl {
		public String URL;
		public String ETag;
		
		public RSSFeedUrl(String url, String etag) {
			URL = url;
			ETag = etag;
		}
	}
	
	Map<String, FeedData> fetchFeeds(List<RSSFeedUrl> feedList);
}
