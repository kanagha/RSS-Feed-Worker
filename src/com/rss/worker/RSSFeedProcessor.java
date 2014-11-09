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
import com.rss.common.Article;
import com.rss.common.RSSFeedQueueRequest;
import com.rss.worker.IRSSFeedFetcher.RSSFeedBody;

import static com.rss.common.AWSDetails.SQS;

public class RSSFeedProcessor implements Runnable {

	private final String queueUrl;
	private final String publisherUrl;
    private final Message msg;

    private IRSSFeedFetcher mFetcher;
    private IRSSFeedDataProvider mDataProvider;
    private List<URL> mURLlist = new ArrayList<URL>();

    RSSFeedProcessor(String queueUrl, String publisherUrl, Message msg, IRSSFeedFetcher fetcher, IRSSFeedDataProvider dataProvider) {
        this.queueUrl = queueUrl;
        this.publisherUrl = publisherUrl;
        this.msg = msg;
        mFetcher = fetcher;
        mDataProvider = dataProvider;
    }

    public void run() {
        String request =  msg.getBody();       
        
        RSSFeedQueueRequest queueRequest = null;
		try {
			queueRequest = new RSSFeedQueueRequest(request);
			// Fetch urls to be processed.
	        List<String> urlList = queueRequest.getURLList();
	        
	        // go to database and fetch etag if the same url exists
	        List<RSSFeedBody> feedBodyList = new ArrayList<RSSFeedBody>();
	        for (String url : urlList) {
	        	FeedData data = mDataProvider.getInfoForUrl(url);
	        	String etag = "";
	        	if (data != null) {
	        		etag = data.etag;
	        	}
	        	feedBodyList.add(new RSSFeedBody(url, etag));
	        }
	        
	        // Now fetch the information using RSSFeedFetcher and add it to database.
	        Map<String, FeedData> feedMap = mFetcher.fetchFeeds(feedBodyList);
	        
	        // AddRowsToTable(feedMap);
	        
	        for (RSSFeedBody feedBody : feedBodyList) {
	        	List<Article> articleList = new LinkedList<Article>();
	        	articleList.addAll(feedMap.get(feedBody.URL).articles);
	        	mDataProvider.addNewArticlesForUrl(feedBody.URL, articleList, feedBody.ETag);
	        }	        
	        
	        // TODO just send a message in the queue with the job id
	        //RSSFeedPublishRequest publisherRequest = new RSSFeedPublishRequest(queueRequest.getSubscriberId(), feedMap);
	        
	        //TODO Not sure
	        SQS.deleteMessage(new DeleteMessageRequest(queueUrl, msg.getReceiptHandle()));
	        
	        // And send a message to publisher queue
	        //SQS.sendMessage(new SendMessageRequest(publisherUrl, publisherRequest.serializeToJSON()));

		} catch (IOException e) {
			System.out.println("IOException occurred while processing feed "+ e);
		} catch (ParseException e) {
			System.out.println("Parsing exception occurred while processing feed " + e);
		}
    }

	/*private void AddRowsToTable(Map<String, List<Article>> feedMap) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put(key, value)
	}*/
}