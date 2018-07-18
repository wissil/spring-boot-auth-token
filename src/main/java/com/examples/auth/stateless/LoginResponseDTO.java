package com.examples.auth.stateless;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class LoginResponseDTO {

	private String token;
	
	public LoginResponseDTO() {
	}
	
	public LoginResponseDTO(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
}
