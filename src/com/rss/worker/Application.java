package com.rss.worker;

public class Application {
	public static void main(String args[]){
		new FetchFeedsQueueListenerProcess().start();
		new PublisherQueueListenerProcess().start();
	}
}
