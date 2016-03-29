package com.rss.worker;

/**
 * This starts the RSSFeedsWorker application
 * Two threads are started, 
 * 1) one which listens to the feeds queue (where a user 
 *    sends a message to fetch latest feeds for selected urls).
 * 2) Another which listens to the publisher queue
 *    where a message is sent once latest feeds are fetched
 *    for a specific channelId
 *
 */
public class Application {
	public static void main(String args[]){
		new FetchFeedsQueueListener().start();
		new PublisherQueueListener().start();
	}
}
