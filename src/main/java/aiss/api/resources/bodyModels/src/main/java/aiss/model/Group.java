package aiss.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Group {

    private String id;
    private String name;
    private String description;
    private List<String> users;
    private String creator;
    
    public Group(String name,String description, String creator) {
        this.name = name;
        this.description = description;
        this.users = new ArrayList<>();
        this.creator = creator;    
    }
    
    public Group() {}
    

    
    public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getUsers() {
		return users;
	}

	public String getCreator() {
		return creator;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	//Funciones
    public void addUser(String userId) {
        this.users.add(userId); 
    }
    public void addMultipleUsers(List<String> users) {
        this.users.addAll(users);
    }

    public void deleteUser(String userId) {
    	this.users.remove(userId); 
    }
	
}
