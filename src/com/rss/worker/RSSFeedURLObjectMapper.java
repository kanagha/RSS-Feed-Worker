package com.rss.worker;

import javax.xml.bind.annotation.XmlRootElement;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
Article RSSFeedURL:
URL <unique>
etag
 */

@XmlRootElement
@DynamoDBTable(tableName = "RSSFeedURL")
public class RSSFeedURLObjectMapper {
	
	String mURL;
	String mETag;
	
	// TODO: creating a default constructor else it cannot be returned by the REST service.
	public RSSFeedURLObjectMapper() {
		//mChannelIds = new LinkedHashSet<Integer>();
	}
	
	@DynamoDBHashKey
	public String getURL() {
		return mURL;
	}
	
	public void setURL(String url){
		mURL = url;
	}

	@DynamoDBAttribute
	public String getETag() {
		return mETag;
	}

	public void setETag(String eTag) {
		mETag = eTag;
	}
}