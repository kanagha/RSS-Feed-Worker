package com.rss.worker.feedfetcher;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.rss.common.cache.FeedData;

@Component
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
