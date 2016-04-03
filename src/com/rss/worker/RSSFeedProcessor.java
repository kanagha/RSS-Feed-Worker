package com.rss.worker;

import static com.rss.common.aws.AWSDetails.SQS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.rss.common.Article;
import com.rss.common.DBDataProvider;
import com.rss.common.FeedNotifierData;
import com.rss.common.RSSFeedQueueRequest;
import com.rss.common.cache.FeedData;
import com.rss.worker.feedfetcher.IRSSFeedFetcher;
import com.rss.worker.feedfetcher.IRSSFeedFetcher.RSSFeedUrl;

/**
 * Processes a message from the FetchFeedsQueue
 * and fetches latest feeds for all the associated urls
 * in the given message
 *
 */
public class RSSFeedProcessor implements Runnable {

    private final String queueUrl;
    private final String publisherUrl;
    private final Message msg;

    private IRSSFeedFetcher mFetcher;

    RSSFeedProcessor(String getJobQueueUrl, String publisherUrl, Message msg, IRSSFeedFetcher fetcher) {
        this.queueUrl = getJobQueueUrl;
        this.publisherUrl = publisherUrl;
        this.msg = msg;
        mFetcher = fetcher;
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
                System.out.println("Fetching etag: " + etag  + " , for url : " + url);
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
            
            //Delete the message to make sure, it is not getting picked up again
            SQS.deleteMessage(new DeleteMessageRequest(queueUrl, msg.getReceiptHandle()));
            
            // And send a message to publisher queue
            // Create a feed notifier data and publish it to the queue
            FeedNotifierData notifierData = new FeedNotifierData(queueRequest.channelId, getLatestFeedsTitles(feedMap));
            SQS.sendMessage(new SendMessageRequest(publisherUrl, notifierData.serializeToJson()));

        } catch (IOException e) {
            System.out.println("IOException occurred while processing feed "+ e);
        } catch (ParseException e) {
            System.out.println("Parsing exception occurred while processing feed " + e);
        } catch (Exception e) {
            System.out.println("Received an exception : " + e);
        }
    }
    
    private List<String> getLatestFeedsTitles(Map<String, FeedData> feedMap) {
        List<String> latestTitles = new ArrayList<String>();
        for (FeedData feedData : feedMap.values()) {
            for (Article article : feedData.articles) {
                latestTitles.add(article.title);
            }
        }
        return latestTitles;
    }
}