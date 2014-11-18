package com.rss.worker;

import javax.xml.bind.annotation.XmlRootElement;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@XmlRootElement
@DynamoDBTable(tableName = "article")
public class ArticleObjectMapper {

	private String mGuid;
	private String mTitle;
	private String mLink;
	private String mDescription;
	private String mPublishedDate;
	// This URL will be mapping to FeedURL table
	private String mFeedURL;
	
	// TODO: Necessary to provide a default constructor. Need to investigate
	public ArticleObjectMapper() {
		
	}
	
	@DynamoDBHashKey(attributeName="mGuid")
	public String getGuid() {
		return mGuid;
	}
	
	public void setGuid(String guid) {
		mGuid = guid;
	}
	
	@DynamoDBAttribute
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	@DynamoDBAttribute
	public String getLink() {
		return mLink;
	}
	
	public void setLink(String link) {
		mLink = link;
	}

	@DynamoDBAttribute
	public String getDescription() {
		return mDescription;
	}
	
	public void setDescription(String description) {
		mDescription = description;
	}

	@DynamoDBAttribute
	public String getPublishedDate() {
		return mPublishedDate;
	}
	
	public void setPublishedDate(String publishedDate) {
		mPublishedDate = publishedDate;
	}
	
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = "FeedURL")
	@DynamoDBAttribute
	public String getFeedURL() {
		return mFeedURL;
	}
	
	public void setFeedURL(String url) {
		mFeedURL = url;
	}
}