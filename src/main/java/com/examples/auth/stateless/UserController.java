package com.examples.auth.stateless;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private TokenAuthenticationService authService;
	
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenCache cache;
	
	@Autowired
	private HttpServletRequest request;
	
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	public String login(@RequestBody final LoginRequestDTO login) {		
		final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
				login.getUsername(), login.getPassword());
		Authentication authentication =  manager.authenticate(loginToken);
		
		// Lookup the complete User object from the database and create an Authentication for it
		// UserBO
		final User authenticatedUser = (User) userDetailsService.loadUserByUsername(authentication.getName());
		final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

		// Add the custom token as HTTP header to the response
		final String token = authService.getTokenHandler().createTokenForUser(authenticatedUser);
		
		cache.put(token, authenticatedUser.getId());
		
		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		
		return token;
	}
	
	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public void logout() {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		System.out.println("TOKEN: " + token);
		
		cache.delete(token);
	}

	@RequestMapping(value = "/api/users/current", method = RequestMethod.GET)
	public User getCurrent() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Auth: " + authentication.getDetails());
		System.out.println("Cred: " + authentication.getCredentials());
		if (authentication instanceof UserAuthentication) {
			return ((UserAuthentication) authentication).getDetails();
		}
		
		/*
		 * Throw exception.
		 */
		throw new InvalidLoginException("User not logged in.");
	}
	
	@RequestMapping(value = "/admin/hello", method = RequestMethod.GET)
	public ResponseEntity<String> helloAdmin() {
		return new ResponseEntity<String>("Hello, admin!", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user/hello", method = RequestMethod.GET)
	public ResponseEntity<String> helloUser() {
		return new ResponseEntity<String>("Hello, user!", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/users/{user}/grant/role/{role}", method = RequestMethod.POST)
	public ResponseEntity<String> grantRole(@PathVariable User user, @PathVariable UserRole role) {
		if (user == null) {
			return new ResponseEntity<String>("invalid user id", HttpStatus.UNPROCESSABLE_ENTITY);
		}

		user.grantRole(role);
		userRepository.saveAndFlush(user);
		return new ResponseEntity<String>("role granted", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/users/{user}/revoke/role/{role}", method = RequestMethod.POST)
	public ResponseEntity<String> revokeRole(@PathVariable User user, @PathVariable UserRole role) {
		if (user == null) {
			return new ResponseEntity<String>("invalid user id", HttpStatus.UNPROCESSABLE_ENTITY);
		}

		user.revokeRole(role);
		userRepository.saveAndFlush(user);
		return new ResponseEntity<String>("role revoked", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/users", method = RequestMethod.GET)
	public List<User> list() {
		return userRepository.findAll();
	}
}
