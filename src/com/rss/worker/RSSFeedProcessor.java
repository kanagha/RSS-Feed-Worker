package com.rss.worker;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.rss.common.Article;
import com.rss.common.DBDataProvider;
import com.rss.common.RSSFeedQueueRequest;
import com.rss.worker.cache.IRSSFeedCacheProvider;
import com.rss.worker.feedfetcher.FeedData;
import com.rss.worker.feedfetcher.IRSSFeedFetcher;
import com.rss.worker.feedfetcher.IRSSFeedFetcher.RSSFeedUrl;

import static com.rss.common.AWSDetails.SQS;

public class RSSFeedProcessor implements Runnable {

	private final String queueUrl;
	private final String publisherUrl;
    private final Message msg;

    private IRSSFeedFetcher mFetcher;
    private IRSSFeedCacheProvider mCacheProvider;
    private List<URL> mURLlist = new ArrayList<URL>();

    RSSFeedProcessor(String getJobQueueUrl, String publisherUrl, Message msg, IRSSFeedFetcher fetcher, IRSSFeedCacheProvider dataProvider) {
        this.queueUrl = getJobQueueUrl;
        this.publisherUrl = publisherUrl;
        this.msg = msg;
        mFetcher = fetcher;
        mCacheProvider = dataProvider;
    }

    public void run() {
        String request =  msg.getBody();       
        
        RSSFeedQueueRequest queueRequest = null;

		try {
			queueRequest = new RSSFeedQueueRequest(request);
			// Fetch urls to be processed.
	        List<String> urlList = queueRequest.getURLList();
	        
	        // go to database and fetch etag if the same url exists
	        
	        List<RSSFeedUrl> feedUrlListWithETag = new ArrayList<RSSFeedUrl>();
	        for (String url : urlList) {
	        	String etag = DBDataProvider.getETagForFeedURL(url);	        	
	            feedUrlListWithETag.add(new RSSFeedUrl(url, etag));
	        }
	        
	        // Now fetch the information using RSSFeedFetcher and add it to database.
	        Map<String, FeedData> feedMap = mFetcher.fetchFeeds(feedUrlListWithETag);
	        
	        // add rows to table	        
	        for (RSSFeedUrl feedUrl : feedUrlListWithETag) {
	        	FeedData data = feedMap.get(feedUrl.URL);
	        	for (Article article : data.articles) {
	        		DBDataProvider.addArticles(article, feedUrl.URL);
	        	}
	        }	        
	        
	        //TODO Need to delete message. Not sure if the message needs to be deleted before
	        SQS.deleteMessage(new DeleteMessageRequest(queueUrl, msg.getReceiptHandle()));
	        
	        // And send a message to publisher queue
	        SQS.sendMessage(new SendMessageRequest(publisherUrl, String.valueOf(queueRequest.jobId)));

		} catch (IOException e) {
			System.out.println("IOException occurred while processing feed "+ e);
		} catch (ParseException e) {
			System.out.println("Parsing exception occurred while processing feed " + e);
		}
    }
}