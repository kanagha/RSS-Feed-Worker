package com.rss.worker;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.rss.worker.cache.RSSFeedCacheProvider;
import com.rss.worker.feedfetcher.RSSFeedFetcher;
import com.rss.worker.feedfetcher.ResultParser;

import static com.rss.common.AWSDetails.*;

public class ListenerProcess extends Thread {
	
	private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);
	
	public static void main(String args[]){
		new ListenerProcess().start();		
	}
	
	public void run() {
		
		String jobQueueUrl = SQS.getQueueUrl(new GetQueueUrlRequest(SQS_ADDJOB_GETFEEDS_QUEUE)).getQueueUrl();
		String feedsQueueUrl = SQS.getQueueUrl(new GetQueueUrlRequest(SQS_GETFEEDS_QUEUE)).getQueueUrl();
		while (true) {
            try {
                ReceiveMessageResult result = SQS.receiveMessage(
                        new ReceiveMessageRequest(jobQueueUrl).withMaxNumberOfMessages(1));
                for (Message msg : result.getMessages()) {
                    executorService.submit(new RSSFeedProcessor(jobQueueUrl, feedsQueueUrl, msg, new RSSFeedFetcher(new ResultParser()), new RSSFeedCacheProvider()));
                }
                sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new RuntimeException("Worker interrupted");
            } catch (Exception e) {
                System.out.println("Exception occurred in ListenerProcess : " + e);
            }
        }
	}
}
