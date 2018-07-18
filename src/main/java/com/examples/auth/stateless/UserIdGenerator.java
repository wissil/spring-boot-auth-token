package com.examples.auth.stateless;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class UserIdGenerator {

	private final AtomicLong sequence;
	
	public UserIdGenerator() {
		this.sequence = new AtomicLong(1L);
	}
	
	public Long nextVal() {
		return sequence.getAndIncrement();
	}
}
