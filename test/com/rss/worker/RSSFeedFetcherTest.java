package com.rss.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.rss.worker.IRSSFeedFetcher.RSSFeedBody;

public class RSSFeedFetcherTest {
	
	static void BasicTest() {
		IRSSFeedFetcher fetcher = new RSSFeedFetcher(new ResultParser());
		List<RSSFeedBody> list = new ArrayList<RSSFeedBody>();
		list.add(new RSSFeedBody("http://rss.cnn.com/rss/edition.rss", ""));
		fetcher.fetchFeeds(list);
	}
	
	static void BasicTestWithETag() throws ClientProtocolException, IOException {
		IRSSFeedFetcher fetcher = new RSSFeedFetcher(new ResultParser());
		List<RSSFeedBody> list = new ArrayList<RSSFeedBody>();
		
		// Fetch the etag first
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://rss.cnn.com/rss/edition.rss");
		HttpResponse response = httpClient.execute(get);   
		Header[] headers = response.getHeaders("ETag");  
		String etag = null;
		if (headers != null && headers.length > 0)
			   etag = headers[0].getValue();				
				
		list.add(new RSSFeedBody("http://rss.cnn.com/rss/edition.rss", etag));
		fetcher.fetchFeeds(list);
	}
	
	public static void main(String args[]) {
		System.out.println("Running RSSFeedFetcher Test...");
		
		BasicTest();
		try {
			BasicTestWithETag();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
