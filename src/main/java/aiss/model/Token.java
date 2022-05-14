package aiss.model;


import java.security.SecureRandom;
import java.util.Base64;

import com.google.appengine.repackaged.org.joda.time.DateTime;


public class Token {
	
	private static final SecureRandom secureRandom = new SecureRandom();
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

	
	
	private String id;
	private String value;
	private String userId;
	private DateTime createdDateTime;
	
	public Token(String userId) {
		this.id = "";
		this.value = generateNewToken();
		this.userId = userId;
		this.createdDateTime = DateTime.now();
	}
	
	public Token(String id, String value, String userId, DateTime createdDateTime) {
		this.id = id;
		this.value = value;
		this.userId = userId;
		this.createdDateTime = createdDateTime;
	}

	
	
	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getUserId() {
		return userId;
	}

	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	
	
	public void setId(String id) {
		this.id = id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}



	public static String generateNewToken() {
	    byte[] randomBytes = new byte[24];
	    secureRandom.nextBytes(randomBytes);
	    return base64Encoder.encodeToString(randomBytes);
	}
	
	
	
}
