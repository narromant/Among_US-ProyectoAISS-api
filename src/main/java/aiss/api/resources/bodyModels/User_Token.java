package aiss.api.resources.bodyModels;

import aiss.model.User;

public class User_Token {
	public User user;
    public String token;
    
    public User getUser() {
        return user;
    }
    public String getToken() {
        return token;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setToken(String token) {
        this.token = token;
    }

}
