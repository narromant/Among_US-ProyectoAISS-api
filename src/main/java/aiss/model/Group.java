package aiss.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Group {

    private String id;
    private String name;
    private String description;
    private Collection<String> users;
    private User creator;
    
    public Group(String name,String description, User creator) {
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

	public Collection<String> getUsers() {
		return users;
	}

	public User getCreator() {
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

	public void setUsers(Collection<String> users) {
		this.users = users;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	//Funciones
    public void addUser(String userId) {
        this.users.add(userId); 
    }
    public void addMultipleUsers(Collection<String> users) {
        this.users.addAll(users);
    }

    public void deleteUser(String userId) {
    	this.users.remove(userId); 
    }
	
}
