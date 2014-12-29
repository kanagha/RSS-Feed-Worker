package com.rss.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.util.Assert;

import com.rss.common.Article;
import com.rss.worker.feedfetcher.FeedData;
import com.rss.worker.feedfetcher.IRSSFeedFetcher;
import com.rss.worker.feedfetcher.RSSFeedFetcher;
import com.rss.worker.feedfetcher.ResultParser;
import com.rss.worker.feedfetcher.IRSSFeedFetcher.RSSFeedUrl;

public class RSSFeedFetcherTest {
	private static String urlString = "http://rss.cnn.com/rss/edition.rss";
	
	static void BasicTest() {
		IRSSFeedFetcher fetcher = new RSSFeedFetcher(new ResultParser());
		List<RSSFeedUrl> list = new ArrayList<RSSFeedUrl>();
		list.add(new RSSFeedUrl(urlString, ""));
		Map<String, FeedData> feedDataMap = fetcher.fetchFeeds(list);
		Assert.notEmpty(feedDataMap);
		List<Article> articles = feedDataMap.get(urlString).articles;
		System.out.println("No.of articles :" + articles.size());
		Iterator<Article> iterator = articles.iterator();
		while (iterator.hasNext()) {
			Article article = iterator.next();
			if (article.guid != null) {
				System.out.println("Found an article with valid content");
				break;
			}
		}
	}
	
	static void BasicTestWithETag() throws ClientProtocolException, IOException {
		IRSSFeedFetcher fetcher = new RSSFeedFetcher(new ResultParser());
		List<RSSFeedUrl> list = new ArrayList<RSSFeedUrl>();
		
		// Fetch the etag first
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://rss.cnn.com/rss/edition.rss");
		HttpResponse response = httpClient.execute(get);   
		Header[] headers = response.getHeaders("ETag");  
		String etag = null;
		if (headers != null && headers.length > 0)
			   etag = headers[0].getValue();				
				
		list.add(new RSSFeedUrl("http://rss.cnn.com/rss/edition.rss", etag));
		fetcher.fetchFeeds(list);
	}
	
	public static void main(String args[]) {
		System.out.println("Running RSSFeedFetcher Test...");
		
		BasicTest();
		/*try {
			BasicTestWithETag();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
