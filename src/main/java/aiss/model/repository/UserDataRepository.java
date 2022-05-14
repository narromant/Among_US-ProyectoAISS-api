package aiss.model.repository;

import java.util.Collection;
import java.util.Map;

import aiss.model.Group;
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
	
	public void addToken(User i);
	public Collection<Token> getAllTokens();
	public Token getToken(String id);
	public void updateToken(Token i);
	public void removeToken(String id);
	public String getTokenValue(String id);
	public Token getUserIdToken(String id);
	public String getTokenUserId(String value);
	
	public void addGroup(Group i);
	public Collection<Group> getAllGroups();
	public Group getGroup(String id);
	public void updateGroup(Group i);
	public void deleteGroup(String id);
	public void addGroupUser(String id, String userId);
	public Collection<String> getAllGroupUsers(String id);
	public void deleteGroupUser(String id, String userId);

}
