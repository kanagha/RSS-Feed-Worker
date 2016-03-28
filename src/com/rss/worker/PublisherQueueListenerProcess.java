package com.rss.worker;

import static com.rss.common.aws.AWSDetails.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.rss.common.DBDataProvider;

/**
 * Process spawned inorder to retrieve messages from the publisher queue
 * Once a message is retrieved, it publishes the feeds to the client websocket endpoint
 *
 */
public class PublisherQueueListenerProcess extends Thread {
	@Autowired 
	private SimpMessagingTemplate template;	
	
	public void run() {
		
		String webServiceQueueUrl = SQS.getQueueUrl(new GetQueueUrlRequest(SQS_GETFEEDS_QUEUE)).getQueueUrl();
		while (true) {
            try {
                ReceiveMessageResult result = SQS.receiveMessage(
                        new ReceiveMessageRequest(webServiceQueueUrl).withWaitTimeSeconds(10).withMaxNumberOfMessages(1));
                for (Message msg : result.getMessages()) {
                	
                	// In this case, request just contains channelID
                	// TODO: Convert it into a class with json parser
                	String channelID =  msg.getBody(); 
                    
                	// Get the stomp endPoints for the corresponding channelId
                	// Fetch and publish all the latest feeds to the stomp endPoints
                	String stompEndPoint = DBDataProvider.getStompEndPoint(channelID);
                	
                	template.convertAndSend(stompEndPoint);
                    
                    // delete the message
                    SQS.deleteMessage(new DeleteMessageRequest(webServiceQueueUrl, msg.getReceiptHandle()));
                }
            } catch (Exception e) {
                System.out.println("Exception occurred in WebServiceQueueListenerProcess : " + e);
            }
        }
	}	
}
