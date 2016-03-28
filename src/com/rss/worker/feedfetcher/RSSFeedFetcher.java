package com.rss.worker.feedfetcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.rss.common.Article;
import com.rss.common.DBDataProvider;
import com.rss.common.cache.FeedData;

public class RSSFeedFetcher implements IRSSFeedFetcher {
	
	IResultParser mParser;
	
	public RSSFeedFetcher(IResultParser parser){
		mParser = parser;
	}

	public Map<String, FeedData> fetchFeeds(List<RSSFeedUrl> feedList) {
		Map<String, FeedData> feedMap = new HashMap<String, FeedData>();
		for (RSSFeedUrl feedUrl : feedList) {
			
			FeedData feedResult = new FeedData();			
			
			try {
				HttpClient client = HttpClientBuilder.create().build();
				HttpGet get = new HttpGet(feedUrl.URL);
				
				if (feedUrl.ETag != null && !feedUrl.ETag.isEmpty()) {
					get.addHeader("If-None-Match", feedUrl.ETag);
				}				
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				Header[] headers = response.getAllHeaders();
				String latestETag = "";
				for (Header header : headers) {
					if (header.getName().compareToIgnoreCase("etag") == 0) {
						latestETag = header.getValue();
					}
				}
				// Set etag
				feedResult.etag = latestETag;
				DBDataProvider.addFeedURL(feedUrl.URL, feedResult.etag);

				// TODO: How to keep this file name random
				String filename = "tmp" + System.currentTimeMillis() + ".xml";
				File file = new File(filename);
				System.out.println("Full path: " + file.getAbsolutePath());
				PrintWriter writer = new PrintWriter(file);
				if (entity != null && entity.getContent() != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line = null;
					while ((line = reader.readLine()) != null) {
						writer.println(line);
					}
					writer.close();
				} else {
					System.out.println("Either entity or getContent is null for URL : " + feedUrl.URL + " , entity :" + entity
							+ " entity.getContent() :" + entity!=null? entity.getContent():null);
				}
				
				List<Article> articleList = mParser.parseXMLFeed(new FileInputStream(file));				
				feedResult.articles.addAll(articleList);				
				feedMap.put(feedUrl.URL, feedResult);
				// Now delete the file
				file.delete();
			} catch (FileNotFoundException e1) {
				System.out.println("File not found exception :" + e1);
			} catch (MalformedURLException e) {
				System.out.println("URL is malformed : "+ e);		
			}
			catch (IOException e) {
				System.out.println("IOException occurred : " + e);
			} 			
		}
		return feedMap;
	}
}
