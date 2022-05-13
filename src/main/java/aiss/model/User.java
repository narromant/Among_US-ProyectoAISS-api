package aiss.model;

import java.util.Map;

public class User {
	
	private String id;
	private String name;
	private String password;
	private Integer role;
	private Map<String, String> data;
	
	
	
	public User() {}
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public User(String id, String name, String password, Map<String, String> data) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.data = data;
	}
	

	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Integer getRole() {
		return role;
	}
	
	public Map<String, String> getData() {
		return data;
	}


	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRole(Integer role) {
		this.role = role;
	}
	
	public void setData(Map<String, String> d) {
		data = d;
	}

}
