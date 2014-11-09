package com.rss.worker;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import static com.rss.common.AWSDetails.SQS;
import static com.rss.common.AWSDetails.SQS_QUEUE_NAME;
import static com.rss.common.AWSDetails.SQS_PUBLISHER_QUEUE;

public class ListenerProcess extends Thread {
	
	private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);
	
	public static void main(String args[]){
		new ListenerProcess().start();		
	}
	
	public void run() {
		
		String queueUrl = SQS.getQueueUrl(new GetQueueUrlRequest(SQS_QUEUE_NAME)).getQueueUrl();
		String publisherUrl = SQS.getQueueUrl(new GetQueueUrlRequest(SQS_PUBLISHER_QUEUE)).getQueueUrl();
		while (true) {
            try {
                ReceiveMessageResult result = SQS.receiveMessage(
                        new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(1));
                for (Message msg : result.getMessages()) {
                    executorService.submit(new RSSFeedProcessor(queueUrl, publisherUrl, msg, new RSSFeedFetcher(new ResultParser()), new RSSFeedDataProvider()));
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
