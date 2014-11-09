package com.rss.worker;

import java.util.List;
import java.util.Map;

import com.rss.common.Article;

public interface IRSSFeedFetcher {
	
	class RSSFeedBody {
		public String URL;
		public String ETag;
		
		RSSFeedBody(String url, String etag) {
			URL = url;
			ETag = etag;
		}
	}
	
	Map<String, FeedData> fetchFeeds(List<RSSFeedBody> feedList);
}
