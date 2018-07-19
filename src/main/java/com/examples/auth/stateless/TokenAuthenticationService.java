package com.examples.auth.stateless;

import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class TokenAuthenticationService {

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;
	
	private final TokenCache cache;
	
	private final UserRepository repository;

	@Inject
	public TokenAuthenticationService(
			@NotNull TokenCache cache,
			@NotNull UserRepository repository,
			@NotNull TokenHandler tokenHandler) {
		this.tokenHandler = Objects.requireNonNull(tokenHandler);
		this.cache = Objects.requireNonNull(cache);
		this.repository = Objects.requireNonNull(repository);
	}

	public void addAuthentication(UserAuthentication authentication) {
		final User user = authentication.getDetails();
		
		final String token = tokenHandler.createToken();
		cache.put(token, user.getId());
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		if (token != null) {
			final Long userId = cache.getUserIdForToken(token);
			if (userId == null) return null;
			
			final User user = repository.findById(userId);
			if (user != null) {
				return new UserAuthentication(user);
			}
		}
		return null;
	}
}
