package com.rss.worker;

import com.rss.common.Article;

import static com.rss.common.AWSDetails.*;

public class DBDataProvider {
	
	public static void addArticles(Article article, String rssFeedURL) {
		ArticleObjectMapper articleObj = new ArticleObjectMapper();
		articleObj.setGuid(article.guid);
		articleObj.setTitle(article.title);
		articleObj.setLink(article.link);
		articleObj.setPublishedDate(article.publishedDate);
		articleObj.setDescription(article.description);
		articleObj.setFeedURL(rssFeedURL);
		DYNAMODB_MAPPER.save(articleObj);
	}
	
	public static void getArticles(String rssFeedURL) {
		// need to write it with Condition and DynamoDBQueryExpression
		// Refer http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaQueryScanORMModelExample.html
	}
	
	public static void addFeedURL(String rssFeedURL, String eTag) {
		RSSFeedURLObjectMapper rssFeedObj = new RSSFeedURLObjectMapper();
		rssFeedObj.setETag(eTag);
		rssFeedObj.setURL(rssFeedURL);
		DYNAMODB_MAPPER.save(rssFeedObj);
	}
	
	public static String getETagForFeedURL(String rssFeedURL) {
		RSSFeedURLObjectMapper rssFeedObj = DYNAMODB_MAPPER.load(RSSFeedURLObjectMapper.class, rssFeedURL);
		return rssFeedObj.mETag;
	}
}