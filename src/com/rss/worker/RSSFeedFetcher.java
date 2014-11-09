package com.rss.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.rss.common.Article;

public class RSSFeedFetcher implements IRSSFeedFetcher {
	
	IResultParser mParser;
	
	RSSFeedFetcher(IResultParser parser){
		mParser = parser;
	}

	public Map<String, FeedData> fetchFeeds(List<RSSFeedBody> feedList) {
		Map<String, FeedData> feedMap = new HashMap<String, FeedData>();
		for (RSSFeedBody body : feedList) {
			
			FeedData feedResult = new FeedData();

			// Make a GET request using the URL and ETag
			
			try {
				//URL url = new URL(body.URL);
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(body.URL);
				
				if (body.ETag != null && !body.ETag.isEmpty()) {
					get.addHeader("If-None-Match", body.ETag);
				}				
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				Header[] headers = response.getAllHeaders();
				String latestETag = "";
				for (Header header : headers) {
					if (header.getName().compareToIgnoreCase("ETAG") == 0) {
						latestETag = header.getValue();
					}
				}
				// Set etag
				feedResult.etag = latestETag;
				
				
				File file = new File("tmp.xml");
				PrintWriter writer = new PrintWriter(file);
				if (entity != null && entity.getContent() != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line = null;
					while ((line = reader.readLine()) != null) {
						writer.println(line);
					}
					writer.close();
				} else {
					System.out.println("Either entity or getContent is null for URL : " + body.URL);
				}
				
				/*// TODO: How to keep this file name random
				File file = new File("tmp.xml");
				PrintWriter writer = new PrintWriter(file);				
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					writer.println(line);
				}
				writer.close();*/		
				List<Article> articleList = mParser.parseXMLFeed(new FileInputStream(file));
				
				feedResult.articles.addAll(articleList);
				
				feedMap.put(body.URL, feedResult);
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
