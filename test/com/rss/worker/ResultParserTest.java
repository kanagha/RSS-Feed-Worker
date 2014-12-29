package com.rss.worker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.util.Assert;

import com.rss.common.Article;
import com.rss.worker.feedfetcher.IResultParser;
import com.rss.worker.feedfetcher.ResultParser;

public class ResultParserTest {
	public static void main(String args[]) {
		IResultParser parser = new ResultParser();
		try {
			List<Article> articles = parser.parseXMLFeed(new FileInputStream("tmp.xml"));
			
			Assert.isTrue(articles.size() > 0);
			System.out.println("Articles Size : " + articles.size());
			System.out.println("first article title : " + articles.get(0).title);
			System.out.println("first article guid : " + articles.get(0).guid);
			System.out.println("first article pubDate : " + articles.get(0).publishedDate);
			System.out.println("first article desc : " + articles.get(0).description);
		} catch (FileNotFoundException e) {
			System.out.println("File not found :" + e);
		}		
	}
}
