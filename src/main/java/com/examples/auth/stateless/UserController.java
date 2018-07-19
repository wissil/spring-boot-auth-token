package com.examples.auth.stateless;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthManager manager;
	
	@Autowired
	private AuthenticationResolver resolver;
	
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	public LoginResponseDTO login(@RequestBody final LoginRequestDTO login) {		
		final String token =  manager.login(login.getUsername(), login.getPassword());	
		return new LoginResponseDTO(token);
	}
	
	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public void logout() {
		manager.logout();
	}

	@RequestMapping(value = "/api/users/current", method = RequestMethod.GET)
	public User getCurrent() {
		return resolver.resolveUser();
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
