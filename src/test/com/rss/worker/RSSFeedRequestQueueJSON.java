package test.com.rss.worker;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.rss.common.RSSFeedQueueRequest;

/**
 * This is just for testing purposes
 * TODO: Remove it
 *
 */
public class RSSFeedRequestQueueJSON {

    public static void main(String args[]) {
        RSSFeedQueueRequest request = new RSSFeedQueueRequest();
        request.channelId="1";
        request.URLlist = new ArrayList<String>();
        request.URLlist.add("http://rss.cnn.com/rss/money_latest.rss");
        
        Gson gson = new Gson();
        String gsonString = gson.toJson(request, RSSFeedQueueRequest.class);
        System.out.println("Gson string: " + gsonString);
    }
}