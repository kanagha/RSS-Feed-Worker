package com.rss.worker;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rss.common.Article;

public class FeedDataTest {
	public static void main(String args[]) {
		
		List<Article> articleList = new LinkedList<Article>();
		
		Article article = new Article();
		article.description =" This is a description";
		article.link = "This is a link";
		article.title = "This is a title";
		article.publishedDate = "This is the published date";
		articleList.add(article);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ArticlesList", articleList);
		System.out.println(jsonObject.toJSONString());
		
		System.out.println(article);
		
		FeedData body = new FeedData();
		body.etag = "etag";
		body.articles= (Deque<Article>) articleList;
		System.out.println(body.serializeToJson());
		
		
			Gson gson = new Gson();
			String str = gson.toJson(article);
			System.out.println("Checking one str : " + str);
			Article art = gson.fromJson(str, Article.class);
			System.out.println(art.title);
			
			Type listOfTestObject = new TypeToken<List<Article>>(){}.getType();
			String s = gson.toJson(articleList, listOfTestObject);
			System.out.println("Lets check this : " + s);
			List<Article> list2 = gson.fromJson(s, listOfTestObject);
			System.out.println("Title is : " + list2.get(0).title);
			
			FeedData data = new FeedData();
			data.etag = "etag";
			data.articles = (Deque<Article>) articleList;
			String feedDataString = gson.toJson(data);
			System.out.println("String  :" + feedDataString);
			
			FeedData dataCopy = gson.fromJson(feedDataString, FeedData.class);
			System.out.println(dataCopy.etag);
			System.out.println(dataCopy.articles.getFirst().title);
			
			
			
			FeedData data1 = new FeedData(body.serializeToJson());
			Assert.notNull(data1.articles);
			Assert.notEmpty(data1.articles);
		
		
	}
}