package org.gusmp.remotekeystorebo.service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CipherService {
	
	@Value( "${encriptionKey}" ) 
	private String key;
	
	public String decrypt(String data) {

		try {
			Cipher cipher = Cipher.getInstance("AES");

			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] keyBytes = sha.digest(key.getBytes());
			keyBytes = Arrays.copyOf(keyBytes, 16);

			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] dataDecrypted = cipher.doFinal(Base64.getDecoder().decode(
					data.getBytes()));

			return new String(dataDecrypted);

		} catch (Exception exc) {
			throw new RuntimeException("Error deciphering request: " + exc.toString());
		}
	}

}
