package com.examples.auth.stateless;

import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Component
public class TokenCache {

	private static volatile Cache<String, Long> cache = CacheBuilder.newBuilder()
            // .expireAfterAccess(EXPIRE_TIME, TimeUnit.MINUTES)
            .build();
	
	public void put(String authToken, long userId) {
		cache.put(authToken, userId);
	}
	
	public void delete(String authToken) {
		cache.invalidate(authToken);
	}
	
	public Long getUserIdForToken(String authToken) {
		System.out.println("Getting internal...");
		System.out.println("Getting: " + cache.getIfPresent(authToken));
		return cache.getIfPresent(authToken);
	}
}
