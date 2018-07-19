package com.examples.auth.stateless;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

@Component
public final class TokenHandler {

	private static final String HASH_ALGO = "SHA-256";

	private final MessageDigest salt;

	public TokenHandler() {
		try {
			this.salt = MessageDigest.getInstance(HASH_ALGO);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String createToken() {
		salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
		return Hex.encodeHexString(salt.digest());
	}

}
