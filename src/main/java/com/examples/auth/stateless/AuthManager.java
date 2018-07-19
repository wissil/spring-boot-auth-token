package com.examples.auth.stateless;

import java.util.Objects;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class AuthManager {

	private final AuthenticationManager manager;

	private final UserDetailsService detailsService;
	
	private final TokenHandler tokenHandler;
	
	private final TokenCache cache;
	
	private final AuthenticationResolver resolver;
	
	@Inject
	public AuthManager(
			@NotNull AuthenticationManager manager,
			@NotNull UserDetailsService detailsService,
			@NotNull TokenHandler tokenHandler,
			@NotNull TokenCache cache,
			@NotNull AuthenticationResolver resolver) {
		this.manager = Objects.requireNonNull(manager);
		this.detailsService = Objects.requireNonNull(detailsService);
		this.tokenHandler = Objects.requireNonNull(tokenHandler);
		this.cache = Objects.requireNonNull(cache);
		this.resolver = Objects.requireNonNull(resolver);
	}

	public String login(String username, String password) {
		final UsernamePasswordAuthenticationToken loginToken = 
				new UsernamePasswordAuthenticationToken(username, password);

		final Authentication authentication =  manager.authenticate(loginToken);

		// Lookup the complete User object from the database and create an Authentication for it
		// UserBO
		final User authenticatedUser = (User) detailsService.loadUserByUsername(authentication.getName());
		final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		
		// Generate token
		final String token = tokenHandler.createToken();
		
		// Store the token to cache
		cache.put(token, authenticatedUser.getId());
		
		return token;
	}
	
	public void logout() {
		final String token = resolver.resolveAuthToken();
		cache.delete(token);
	}

}
