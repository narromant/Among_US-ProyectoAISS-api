package aiss.model.repository;

import java.util.Collection;
import java.util.Map;

import aiss.model.Token;
import aiss.model.User;

public interface UserDataRepository {
	
	public void addUser(User i);
	public Collection<User> getAllUsers();
	public User getUser(String id);
	public void updateUser(User i);
	public void deleteUser(String id);
	public String getOneData(String id, String key);
	public Map<String, String> getAllData(String id);
	public void updateData(String id, String key, String data);
	public void deleteData(String id, String key);
	public String getUserName(String id);
	public String getUserId(String name);
	public Boolean checkCorrectToken(String name, String token);
	
	public Token createToken(User i);
	public void assignToken(Token i);
	public Collection<Token> getAllTokens();
	public Token getToken(String id);
	public void updateToken(Token i);
	public void removeToken(String id);
	public String getTokenValue(String id);
	public String getTokenUserId(String value);

}
