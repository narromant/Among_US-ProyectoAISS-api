package aiss.api.resources.bodyModels;

import java.util.List;

public class Ids_Token {
	
	public List<String> users;
    public String token;
    
    public List<String> getUsers() {
        return users;
    }
    public String getToken() {
        return token;
    }
    public void setUsers(List<String> users) {
        this.users = users;
    }
    public void setToken(String token) {
        this.token = token;
    } 

}
