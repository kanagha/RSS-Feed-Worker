package com.rss.worker.cache;

import java.util.LinkedList;
import java.util.List;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class CacheInitializer {
	
	private List<JedisShardInfo> mShards;
	private ShardedJedisPool mPool;
	
	private CacheInitializer(){
		mShards = new LinkedList<JedisShardInfo>();
		mPool = new ShardedJedisPool(new GenericObjectPoolConfig(), mShards);	
		InitializeCache();
	}
	
	private static CacheInitializer NEW_INSTANCE = new CacheInitializer();
	
	public static CacheInitializer getInstance() {
		return NEW_INSTANCE;
	}
	
	public void InitializeCache() {
		mShards.add(new JedisShardInfo("localhost","someport"));
	}
	
	public void AddResourcesToCache(String host, String name) {
		mShards.add(new JedisShardInfo(host, name));
	}
	
	public void RemoveResourcesFromCache(String host, String name) {
		mShards.remove(new JedisShardInfo(host, name));
	}
	
	public ShardedJedis getResource() {
		return mPool.getResource();
	}
}
