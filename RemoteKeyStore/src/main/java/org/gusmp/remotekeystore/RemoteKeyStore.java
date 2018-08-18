package org.gusmp.remotekeystore;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RemoteKeyStore {

	private KeyStore keyStore;
	private static final String KeyStoreType = "REMOTE";
	
	private static final String APP_USER = "myapp1";
	private static final String APP_PASSWORD = "mypassword1";
	private static final String REMOTE_KEYSTORE_BO_URL = "http://localhost:8081/RemoteKeyStoreBO/get"; 
	private static final String COMMUNICATIONS_CIPHER_KEY = "12345";

	public static final String getDefaultType() {
		return KeyStoreType;
	}

	public static RemoteKeyStore getInstance(String type)
			throws KeyStoreException {
		return new RemoteKeyStore();
	}

	public void load(InputStream stream, char[] password) throws Exception {

		this.keyStore = KeyStore.getInstance("pkcs12");

		String ip = "";
		 try(final DatagramSocket socket = new DatagramSocket()) {
			 socket.connect(InetAddress.getByName("8.8.8.8"), 10002); 
			 ip = socket.getLocalAddress().getHostAddress(); 
		}
		
		String json = buildJsonRequestCertificate(APP_USER,APP_PASSWORD, new String(password),
				Inet4Address.getLocalHost().getHostName(), ip);
		String response = send(REMOTE_KEYSTORE_BO_URL, encrypt(json));

		byte[] keyStoreByte = Base64.getDecoder().decode(response.getBytes());
		
		this.keyStore.load(new ByteArrayInputStream(keyStoreByte), password);
	}

	public final String getType() {
		return RemoteKeyStore.KeyStoreType;
	}

	public final Key getKey(String alias, char[] password)
			throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableKeyException {

		return this.keyStore.getKey(alias, password);
	}

	public final Certificate[] getCertificateChain(String alias)
			throws KeyStoreException {
		return this.keyStore.getCertificateChain(alias);
	}

	public final Certificate getCertificate(String alias)
			throws KeyStoreException {
		return this.keyStore.getCertificate(alias);
	}

	public final Date getCreationDate(String alias) throws KeyStoreException {
		return this.keyStore.getCreationDate(alias);
	}

	public final Enumeration<String> aliases() throws KeyStoreException {
		return this.keyStore.aliases();
	}

	public final boolean containsAlias(String alias) throws KeyStoreException {
		return this.keyStore.containsAlias(alias);
	}

	public final int size() throws KeyStoreException {
		return this.keyStore.size();
	}

	public final boolean isKeyEntry(String alias) throws KeyStoreException {
		return this.keyStore.isKeyEntry(alias);
	}

	public final boolean isCertificateEntry(String alias)
			throws KeyStoreException {
		return this.keyStore.isCertificateEntry(alias);
	}

	public final String getCertificateAlias(Certificate cert)
			throws KeyStoreException {
		return this.keyStore.getCertificateAlias(cert);
	}

	public final KeyStore.Entry getEntry(String alias,
			KeyStore.ProtectionParameter protParam)
			throws NoSuchAlgorithmException, UnrecoverableEntryException,
			KeyStoreException {

		return this.keyStore.getEntry(alias, protParam);

	}

	/* EXTRA */

	public KeyStore getKeyStore() {
		return this.keyStore;
	}

	private String encrypt(String data) {

		try {
			Cipher cipher = Cipher.getInstance("AES");

			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] keyBytes = sha.digest(COMMUNICATIONS_CIPHER_KEY.getBytes());
			keyBytes = Arrays.copyOf(keyBytes, 16);

			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] dataEncrypted = cipher.doFinal(data.getBytes());

			return new String(Base64.getEncoder().encode(dataEncrypted));

		} catch (Exception exc) {
			throw new RuntimeException(exc.toString());
		}
	}

	/*
	private String decrypt(String data) {

		try {
			Cipher cipher = Cipher.getInstance("AES");

			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] keyBytes = sha.digest("12345".getBytes());
			keyBytes = Arrays.copyOf(keyBytes, 16);

			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] dataDecrypted = cipher.doFinal(Base64.getDecoder().decode(
					data.getBytes()));

			return new String(dataDecrypted);

		} catch (Exception exc) {
			throw new RuntimeException(exc.toString());
		}

	}
	*/
	
	private String buildJsonRequestCertificate(String user, String password,
			String label, String host, String ip) {

		String json = "{" + "\"user\" : \"" + user + "\"," + 
				"\"password\" : \"" + password + "\"," + 
				"\"label\" : \"" + label + "\","
				+ "\"host\" : \"" + host + "\"," + 
				"\"ip\" : \"" + ip + "\""
				+ "}";

		return json;
	}

	private String send(String remoteUrl, String data) {

		try {
			
			URL url = new URL(remoteUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
			
			// int responseCode = connection.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			return response.toString();

		} catch (Exception exc) {
			throw new RuntimeException(exc.toString());
		}
	}

}
