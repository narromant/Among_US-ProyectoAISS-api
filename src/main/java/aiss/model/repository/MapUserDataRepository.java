package aiss.model.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aiss.model.Group;
import aiss.model.Token;
import aiss.model.User;

public class MapUserDataRepository implements UserDataRepository {

	Map<String, User> userMap;
	Map<String, Token> tokenMap;
	Map<String, Group> groupMap;
	private static MapUserDataRepository instance = null;
	private int indexUser = 1;
	private int indexToken = 1;
	private int indexGroup = 1;
	
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
		groupMap = new HashMap<String, Group>();
		
		Map<String, String> mapaInmutable;
		List<String> usuarios;
		
		User user01 = new User();
		mapaInmutable = Map.of("Gender", "Female", "Phone", "601010101", "Office", "501", "Job", "Director", "Salary", "5000");
		user01.setName("Lucia");
		user01.setPassword("Password01");
		user01.setRole(2);
		user01.setData(new HashMap<>(mapaInmutable)); // Definir el mapa de esta manera hace que sea mutable
		
		User user02 = new User();
		mapaInmutable = Map.of("Gender", "Female", "Phone", "602020202", "Office", "405", "Job", "Programmer", "Salary", "2300");
		user02.setName("Mar");
		user02.setPassword("Password02");
		user02.setRole(0);
		user02.setData(new HashMap<>(mapaInmutable));
		
		User user03 = new User();
		mapaInmutable = Map.of("Gender", "Male", "Phone", "603030303", "Office", "908", "Job", "Programmer", "Salary", "1800");
		user03.setName("Tadeo");
		user03.setPassword("Password03");
		user03.setRole(0);
		user03.setData(new HashMap<>(mapaInmutable));

		User user04 = new User();
		mapaInmutable = Map.of("Gender", "Female", "Phone", "604040404", "Office", "207", "Job", "Accountant", "Salary", "2000");
		user04.setName("Paco");
		user04.setPassword("Password04");
		user04.setRole(1);
		user04.setData(new HashMap<>(mapaInmutable));

		User user05 = new User();
		mapaInmutable = Map.of("Gender", "Male", "Phone", "605050505", "Office", "", "Job", "Cleaner", "Salary", "800");
		user05.setName("Ignacio");
		user05.setPassword("Password05");
		user05.setRole(0);
		user05.setData(new HashMap<>(mapaInmutable));
		
		User user06 = new User();
		mapaInmutable = Map.of("Gender", "Female", "Phone", "606060606", "Office", "405", "Job", "Accountant", "Salary", "2500");
		user06.setName("Adela");
		user06.setPassword("Password06");
		user06.setRole(1);
		user06.setData(new HashMap<>(mapaInmutable));
		
		User user07 = new User();
		mapaInmutable = Map.of("Gender", "Male", "Phone", "607070707", "Office", "102", "Job", "Security", "Salary", "2500");
		user07.setName("Alejandro");
		user07.setPassword("Password07");
		user07.setRole(0);
		user07.setData(new HashMap<>(mapaInmutable));
		
		Group group01= new Group();
		usuarios=List.of("u2","u3");
		group01.setName("Nueva Estrategia de Marketing");
		group01.setDescription("Grupo de integrantes del proyecto solicitado por Finlandia");
		group01.setUsers(usuarios);
		group01.setCreator("u6");

		Group group02= new Group();
		usuarios=List.of("u5","u3", "u7");
		group02.setName("API para AISS");
		group02.setDescription("Grupo de integrantes de un proyecto de gestión de usuarios");
		group02.setUsers(usuarios);
		group02.setCreator("u4");

		Group group03= new Group();
		usuarios=List.of("u5","u2");
		group03.setName("Gente a punto de ser Despedida");
		group03.setDescription("El nombre lo dice todo");
		group03.setUsers(usuarios);
		group03.setCreator("u6");
		
		addUser(user01);
		addUser(user02);
		addUser(user03);
		addUser(user04);
		addUser(user05);
		addUser(user06);
		addUser(user07);
		
		addGroup(group01);
		addGroup(group02);
		addGroup(group03);
		
		addToken(user01);
		
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
	
	/////////////////////////////     METODOS DE GROUPS     /////////////////////////////
	
	public void addGroup(Group i) {
		String id = "g" + indexGroup ++;
		i.setId(id);
		groupMap.put(id, i);
	}
	
	public Collection<Group> getAllGroups() {
		return groupMap.values();
	}
	
	public Group getGroup(String id) {
		return groupMap.get(id);
	}
	
	public void updateGroup(Group i) {
		groupMap.put(i.getId(), i);
	}
	
	public void deleteGroup(String id) {
		groupMap.remove(id);
	}
	
	public void addGroupUser(String id, String userId) {
		groupMap.get(id).addUser(userId);
	}
	
	public Collection<String> getAllGroupUsers(String id) {
		return groupMap.get(id).getUsers();
	}
	
	public void deleteGroupUser(String id, String userId) {
		groupMap.get(id).deleteUser(userId);
	}
	
}
