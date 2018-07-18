package com.examples.auth.stateless;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class TokenAuthenticationService {

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;
	
	private final TokenCache cache;
	
	private final UserRepository repository;

	@Autowired
	public TokenAuthenticationService(
			@Value("${token.secret}") String secret,
			@NotNull TokenCache cache,
			@NotNull UserRepository repository) {
		this.tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(secret));
		this.cache = Objects.requireNonNull(cache);
		this.repository = Objects.requireNonNull(repository);
	}

	public void addAuthentication(HttpServletResponse response, UserAuthentication authentication) throws IOException {
		final User user = authentication.getDetails();
		
		final String token = tokenHandler.createTokenForUser(user);
		response.addHeader(AUTH_HEADER_NAME, token);
		response.setContentType("application/json");
		
		cache.put(token, user.getId());
		
		//JsonNode json = new ObjectMapper().valueToTree(new LoginResponseDTO(token));
		
		//esponse.getOutputStream().write(json.toString().getBytes());
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		if (token != null) {
			//final User user = tokenHandler.parseUserFromToken(token);
			System.out.println("Cache... " + cache);
			System.out.println("Token: " + token);
			final Long userId = cache.getUserIdForToken(token);
			System.out.println("User ID: " + userId);
			final User user = repository.findById(userId);
			System.out.println("User: " + user);
			if (user != null) {
				return new UserAuthentication(user);
			}
		}
		return null;
	}
	
	public TokenHandler getTokenHandler() {
		return tokenHandler;
	}
}
