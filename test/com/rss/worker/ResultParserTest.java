package com.rss.worker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.util.Assert;

import com.rss.common.Article;

public class ResultParserTest {
	public static void main(String args[]) {
		IResultParser parser = new ResultParser();
		try {
			List<Article> articles = parser.parseXMLFeed(new FileInputStream("testdata.xml"));
			
			Assert.isTrue(articles.size() > 0);
			System.out.println("Articles Size : " + articles.size());
			System.out.println("first article title : " + articles.get(0).title);
		} catch (FileNotFoundException e) {
			System.out.println("File not found :" + e);
		}		
	}
}
