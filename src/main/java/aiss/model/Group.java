package aiss.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Group {

    private String id;
    private String name;
    private String description;
    private Collection<User> users;
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

	public Collection<User> getUsers() {
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

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	//Funciones
    public void addUser(User user) {
        this.users.add(user); 
    }
    public void addMultipleUsers(Collection<User> users) {
        this.users.addAll(users);
    }
    public User getUser(String id) {
        if (this.users == null) return null;
        User user = users.stream().filter(u->u.getId()==id).findFirst().orElse(null);
        return user;
    }
	
}
