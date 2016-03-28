package com.rss.worker.feedfetcher;

import java.io.InputStream;
import java.util.List;

import com.rss.common.Article;

public interface IResultParser {
	
	public List<Article> parseXMLFeed(InputStream xmlFeedInputStream);
}
