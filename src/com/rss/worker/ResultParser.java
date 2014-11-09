package com.rss.worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.rss.common.Article;

public class ResultParser implements IResultParser {

	public List<Article> parseXMLFeed(InputStream xmlFeedInputStream) {
		// parse all the xml feeds to list of article
		List<Article> articleList = new LinkedList<Article>();
		String thisLine = null;
	      String itemPrefix = "<item>";
	      String itemSuffix = "</item>";
	      try{
	         // open input stream test.txt for reading purpose.
	         BufferedReader br = new BufferedReader(new InputStreamReader(xmlFeedInputStream));
	         while ((thisLine = br.readLine()) != null) {
	            //System.out.println(thisLine);
	            if (thisLine.startsWith(itemPrefix)) {
	            	Article article = SplitString(thisLine);
	            	articleList.add(article);
	            }                     
	         }       
	      } catch(IOException e){
	         System.out.println("IOException occurred while parsing XML Feed");
	      }
	      return articleList;
	   }

	private static Article SplitString(String line) {		
		Article article = new Article();
		int index = line.indexOf("<title>");
		index = index + 7;
		if (line.indexOf("<title>") != -1) {
			article.title =line.substring(index, line.indexOf("</title>"));
		}
		index = line.indexOf("<description>");
		if (line.indexOf("<description>") != -1) {
			article.description =line.substring(index + 13, line.indexOf("</description>"));
		}
		index = line.indexOf("<guid>");
		if (line.indexOf("<guid>") != -1) {
			article.link = line.substring(index + 6, line.indexOf("</guid>"));
		}
		index = line.indexOf("<pubDate>");
		if (line.indexOf("<pubDate>") != -1) {
			article.publishedDate = line.substring(index + 9, line.indexOf("</pubDate>"));
		}
		
		//System.out.println(line.substring(line.indexOf("<guid>")+ 6, line.indexOf("</guid>")));
		//System.out.println(" Index is : " + line.indexOf("<description>"));
		//System.out.println(line.substring(line.indexOf("<description>")+ 13, line.indexOf("</description>")));
		//System.out.println(line.substring(line.indexOf("<pubDate>")+ 9, line.indexOf("</pubDate>")));
		
		return article;
	}
}
