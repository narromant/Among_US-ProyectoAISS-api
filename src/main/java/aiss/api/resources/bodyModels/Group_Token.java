package aiss.api.resources.bodyModels;

import aiss.model.Group;

public class Group_Token {
	public static Group group;
    public static String token;
    
    public  Group getGroup() {
        return group;
    }
    public  String getToken() {
        return token;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public void setToken(String token) {
        this.token = token;
    }

}
