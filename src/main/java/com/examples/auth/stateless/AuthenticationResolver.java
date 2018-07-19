package com.examples.auth.stateless;

import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationResolver {
	
	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
	
	@Inject
	private HttpServletRequest request;
	
	private final TokenCache cache;
	
	@Inject
	public AuthenticationResolver(
			@NotNull TokenCache cache) {
		this.cache = Objects.requireNonNull(cache);
	}
	
	public User resolveUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof UserAuthentication) {
			return ((UserAuthentication) authentication).getDetails();
		}
		
		/*
		 * Throw exception.
		 */
		throw new InvalidLoginException("User not logged in.");
	}

	public String resolveAuthToken() {
		return request.getHeader(AUTH_HEADER_NAME);
	}
	
	public Long resolveUserId() {
		final String token = resolveAuthToken();
		return resolveUserId(token);
	}
	
	public Long resolveUserId(String token) {
		return cache.getUserIdForToken(token);
	}
}
