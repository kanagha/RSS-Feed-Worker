package com.rss.worker.feedfetcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.rss.common.Article;

public class ResultParser implements IResultParser {
    
    private static final String ITEM_PREFIX = "<item>";
    private static final String ITEM_SUFFIX = "</item>";
    private static final String TITLE_PREFIX = "<title>";
    private static final String TITLE_SUFFIX = "</title>";
    private static final String DESC_PREFIX = "<description>";
    private static final String DESC_SUFFIX = "</description>";
    private static final String GUID_PREFIX = "<guid>";
    private static final String GUID_SUFFIX = "</guid>";
    private static final String PUBDATE_PREFIX = "<pubDate>";
    private static final String PUBDATE_SUFFIX = "</pubDate>";

    public List<Article> parseXMLFeed(InputStream xmlFeedInputStream) {
        // parse all the xml feeds to list of article
        List<Article> articleList = new LinkedList<Article>();
        String thisLine = null;
          boolean hasStarted = false;
          StringBuffer combinedLine = null;
          try{
             BufferedReader br = new BufferedReader(new InputStreamReader(xmlFeedInputStream));
             while ((thisLine = br.readLine()) != null) {
                 int suffixPos = -1;
                 
                while (thisLine.contains(ITEM_PREFIX)) {
                    hasStarted = true;
                    System.out.println("Contains a new line");
                    
                    combinedLine = new StringBuffer();
                    
                    if (hasStarted) {
                        combinedLine.append(thisLine);
                    }
                    if (hasStarted && thisLine.contains(ITEM_SUFFIX)) {
                        hasStarted = false;
                        suffixPos = thisLine.indexOf(ITEM_SUFFIX) + ITEM_SUFFIX.length();
                        Article article = SplitString(combinedLine.toString());
                        System.out.println("Parsing an article");
                        if (article.guid != null) {
                            System.out.println("Contained a guid:");
                            articleList.add(article);
                        }
                        thisLine = thisLine.substring(suffixPos);
                    }
                }
                
             }       
          } catch(IOException e){
             System.out.println("IOException occurred while parsing XML Feed");
          }
          return articleList;
       }

    private static Article SplitString(String line) {
        Article article = new Article();
        
        int index = line.indexOf(TITLE_PREFIX);
        index = index + 7;
        if (line.indexOf(TITLE_PREFIX) != -1) {
            article.title =line.substring(index, line.indexOf(TITLE_SUFFIX));
        }
        index = line.indexOf(DESC_PREFIX);
        if (line.indexOf(DESC_PREFIX) != -1) {
            article.description =line.substring(index + 13, line.indexOf(DESC_SUFFIX));
        }
        index = line.indexOf(GUID_PREFIX);
        if (index != -1) {
            // the cnn news have guid starting with guid isPermaLink="false". So accounting for the whole length
            // changed the length from 26 to 6
            article.guid = line.substring(index + 6, line.indexOf(GUID_SUFFIX));
        } else {
            if ((index = line.indexOf("<guid isPermaLink=")) != -1) {
                // the cnn news have guid starting with guid isPermaLink="false". So accounting for the whole length
                // changed the length from 26 to 6
                article.guid = line.substring(index + 26, line.indexOf(GUID_SUFFIX));
            }
        }
        
        index = line.indexOf(PUBDATE_PREFIX);
        if (line.indexOf(PUBDATE_PREFIX) != -1) {
            article.publishedDate = line.substring(index + 9, line.indexOf(PUBDATE_SUFFIX));
        }
        return article;
    }
}
