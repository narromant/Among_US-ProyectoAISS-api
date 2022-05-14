package aiss.model.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.repackaged.org.joda.time.DateTime;

import aiss.model.Token;
import aiss.model.User;

public class MapUserDataRepository implements UserDataRepository {

	Map<String, User> userMap;
	Map<String, Token> tokenMap;
	private static MapUserDataRepository instance = null;
	private int indexUser = 1;
	private int indexToken = 1;
	
	public static MapUserDataRepository getInstance() {
		
		if (instance==null) {
			instance = new MapUserDataRepository();
			instance.init();
		}
		
		return instance;
	}
	
	/////////////////////////////     POPULAR DATOS DE API     /////////////////////////////
	
	public void init() {
		
		userMap = new HashMap<String, User>();
		tokenMap = new HashMap<String, Token>();
		
		Map<String, String> mapaInmutable;
		
		User user01 = new User();
		mapaInmutable = Map.of("Gender", "Male", "Phone", "601010101", "Office", "207", "Job", "Accountant", "WorkingOn", "Project01");
		user01.setName("User01");
		user01.setPassword("Password01");
		user01.setRole(1);
		user01.setData(new HashMap<>(mapaInmutable)); // Definir el mapa de esta manera hace que sea mutable
		
		User user02 = new User();
		mapaInmutable = Map.of("Gender", "Female", "Phone", "602020202", "Office", "405", "Job", "Programmer", "WorkingOn", "Project02");
		user02.setName("User02");
		user02.setPassword("Password02");
		user02.setRole(0);
		user02.setData(new HashMap<>(mapaInmutable));
		
		User user03 = new User();
		mapaInmutable = Map.of("Gender", "Male", "Phone", "603030303", "Office", "308", "Job", "Programmer", "WorkingOn", "Project01");
		user03.setName("User02");
		user03.setPassword("Password02");
		user03.setRole(0);
		user03.setData(new HashMap<>(mapaInmutable));

		User user04 = new User();
		mapaInmutable = Map.of("Gender", "Female", "Phone", "604040404", "Office", "501", "Job", "Director", "WorkingOn", "");
		user04.setName("User04");
		user04.setPassword("Password04");
		user04.setRole(2);
		user04.setData(new HashMap<>(mapaInmutable));

		User user05 = new User();
		mapaInmutable = Map.of("Gender", "Male", "Phone", "605050505", "Office", "", "Job", "Cleaner", "WorkingOn", "");
		user05.setName("User05");
		user05.setPassword("Password05");
		user05.setRole(0);
		user05.setData(new HashMap<>(mapaInmutable));
		
		addUser(user01);
		addUser(user02);
		addUser(user03);
		addUser(user04);
		addUser(user05);
		
		addToken(user01);;
		
	}
	
	/////////////////////////////     METODOS DE USER     /////////////////////////////
	
	public void addUser(User i) {
		String id = "u" + indexUser ++;
		i.setId(id);
		userMap.put(id, i);
	}
	
	public Collection<User> getAllUsers() {
		return userMap.values();
	}
	
	public User getUser(String id) {
		return userMap.get(id);
	}
	
	public void updateUser(User i) {
		userMap.put(i.getId(), i);
	}
	
	public void deleteUser(String id) {
		userMap.remove(id);
	}
	
	public String getOneData(String id, String key) {
		return userMap.get(id).getData().get(key);
	}
	
	public Map<String, String> getAllData(String id) {
		return userMap.get(id).getData();
	}
	
	public void updateData(String id, String key, String data) { // Los metodos ADD y UPDATE son iguales para los datos de usuario
		// TODO
	}
	
	public void deleteData(String id, String key) {
		userMap.remove(key);
	}
	
	// FUNCIONES EXTRA
	
	public String getUserName(String id) {
		return userMap.get(id).getName();
	}
	
	public String getUserId(String name) {
		User user = userMap.values().stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
		if (user != null) return user.getId();  // De esta manera evitamos que intente hacerle getId() a null y genere un error
		else return null;
	}
	
	public Boolean checkCorrectToken(String name, String token) {
		String userId = getUserId(name);
		return userId.equals(getTokenUserId(token)) && token != null && userId != null;
	}
	
	/////////////////////////////     METODOS DE TOKEN     /////////////////////////////
	
	public void addToken(User i) {  // Entre createToken() y assignToken() se forma el metodo ADD de Token
		Token token = new Token(i.getId());
		String id = "t" + indexToken ++;
		token.setId(id);
		tokenMap.put(id, token);
	}
	
	public Collection<Token> getAllTokens() {
		return tokenMap.values();
	}
	
	public Token getToken(String id) {
		return tokenMap.get(id);
	}
	
	public void updateToken(Token i) {
		tokenMap.put(i.getId(), i);
	}
	
	public void removeToken(String id) {
		tokenMap.remove(id);
	}
	
	// FUNCIONES EXTRA
	
	public String getTokenValue(String id) {
		return tokenMap.get(id).getValue();
	}
	
	public Token getUserIdToken(String id) {
		Token token = tokenMap.values().stream().filter(x -> x.getUserId().equals(id)).findFirst().orElse(null);
		return token;
	
	}
	
	public String getTokenUserId(String value) {
		Token token = tokenMap.values().stream().filter(x -> x.getValue().equals(value)).findFirst().orElse(null);
		if (token != null) return token.getUserId();  // De esta manera evitamos que intente hacerle getId() a null y genere un error
		else return null;
	}
	
}
