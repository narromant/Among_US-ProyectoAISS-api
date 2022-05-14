package aiss.api.resources;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import aiss.model.Group;
import aiss.model.repository.MapUserDataRepository;
import aiss.model.repository.UserDataRepository;

@Path("/groups")
public class GroupResource {

	/* Singleton */
	public static GroupResource _instance=null;
	UserDataRepository repository;
	
	private GroupResource() {
		repository = MapUserDataRepository.getInstance();
	}
	
	public static GroupResource getInstance() {
		if(_instance == null) _instance = new GroupResource();
		return _instance;
	}
	
	//   DEVOLVER TODOS LOS TOKENS
	@GET
	@Produces("application/json")
	public Collection<Group> getAll() {
		return repository.getAllGroups();
	}
	
}
