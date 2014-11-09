package com.rss.common;

import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.parser.ParseException;

public class RSSFeedQueueRequestTest {
	public static void main(String args[]) throws IOException, ParseException {
		RSSFeedQueueRequest request = new RSSFeedQueueRequest();
		request.subscriberId = 1;
		request.URLlist = new LinkedList<String>();
		request.URLlist.add("https://www.youtube.com/watch?v=YeRNErD81VA");
		request.URLlist.add("https://github.com/awslabs/java-meme-generator-sample");
		String jsonString = request.serializeToJSON();
		//String jString = "{\"Id\":1,\"URLlist\":[https://www.youtube.com/watch?v=YeRNErD81VA,https://github.com/awslabs/java-meme-generator-sample]}";

		System.out.println(jsonString);
		RSSFeedQueueRequest req = new RSSFeedQueueRequest(jsonString);
		System.out.println("Id : "+ req.subscriberId);
		System.out.println("Size : "+ req.URLlist.size());
		System.out.println("Size : "+ req.URLlist.get(0));
		System.out.println("Size : "+ req.URLlist.get(1));
	}

}
