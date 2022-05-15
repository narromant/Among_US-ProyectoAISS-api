package aiss.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import aiss.api.resources.GroupResource;
import aiss.api.resources.TokenResource;
import aiss.api.resources.UserResource;


public class UserApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	// Loads all resources that are implemented in the application
	// so that they can be found by RESTEasy.
	public UserApplication() {

		singletons.add(UserResource.getInstance());
		singletons.add(TokenResource.getInstance());
		singletons.add(GroupResource.getInstance());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}