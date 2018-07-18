package com.examples.auth.stateless;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

	private final Map<String, User> users;
	
	private final Map<Long, User> ids;
	
	public UserRepository() {
		this.users = new HashMap<>();
		this.ids = new HashMap<>();
	}
	
	public void save(User user) {
		ids.put(user.getId(), user);
		users.put(user.getUsername(), user);
	}
	
	public void saveAndFlush(User user) {
		save(user);
	}
	
	public User findById(long id) {
		return ids.get(id);
	}
	
	public User findByUsername(String username) {
		return users.get(username);
	}
	
	public User loadByUsername(String username) {
		return findByUsername(username);
	}
	
	public List<User> findAll() {
		return users.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
	}
}
