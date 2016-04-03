package com.rss.worker;

import static com.rss.common.aws.AWSDetails.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.rss.common.Channel;
import com.rss.common.DBDataProvider;
import com.rss.common.FeedNotifierData;
import com.rss.common.Subscriber;
import com.rss.common.dataprovider.IChannelDBProvider;
import com.rss.common.dataprovider.ISubscriberDBProvider;

/**
 * Process spawned inorder to retrieve messages from the publisher queue
 * Once a message is retrieved, it publishes the feeds to the client websocket endpoint
 *
 */
public class PublisherQueueListener extends Thread {
    @Autowired 
    private SimpMessagingTemplate template;    
    
    @Autowired
    private IChannelDBProvider channelDBProvider;
    
    @Autowired
    private ISubscriberDBProvider subscriberDBProvider;

    public void run() {
        
        String webServiceQueueUrl = SQS.getQueueUrl(new GetQueueUrlRequest(SQS_GETFEEDS_QUEUE)).getQueueUrl();
        while (true) {
            try {
                ReceiveMessageResult result = SQS.receiveMessage(
                        new ReceiveMessageRequest(webServiceQueueUrl).withWaitTimeSeconds(10).withMaxNumberOfMessages(1));
                for (Message msg : result.getMessages()) {
                    
                    // In this case, request just contains channelID
                    // TODO: Convert it into a class with json parser
                    String feedNotifierDataGsonString =  msg.getBody(); 
                    FeedNotifierData notifierData = new FeedNotifierData(feedNotifierDataGsonString);
                    
                    // fetch the userId for channelId
                    Channel channel = channelDBProvider.getChannel(notifierData.channelId);
                    Subscriber subscriber = subscriberDBProvider.getSubscriber(channel.getSubscriberId());

                    // How will you send all the latest feeds
                    // Do a loop from DB and read all the feeds and send it?
                    template.convertAndSendToUser(subscriber.getName(), "/queue/messages", feedNotifierDataGsonString);

                    // delete the message
                    SQS.deleteMessage(new DeleteMessageRequest(webServiceQueueUrl, msg.getReceiptHandle()));
                }
            } catch (Exception e) {
                System.out.println("Exception occurred in WebServiceQueueListenerProcess : " + e);
            }
        }
    }    
}
