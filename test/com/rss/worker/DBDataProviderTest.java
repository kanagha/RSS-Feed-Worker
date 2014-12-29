package com.rss.worker;

import com.rss.common.DBDataProvider;

public class DBDataProviderTest {
	public static void main(String args[]) {
		System.out.println(DBDataProvider.getETagForFeedURL("someurl"));
	}
}
