package aiss.api.resources;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.spi.NotFoundException;

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
	
	//   DEVOLVER TODOS LOS GRUPOS
	@GET
	@Produces("application/json")
	public Collection<Group> getAll() {
		return repository.getAllGroups();
	}
	
	//   DEVOLVER UN GRUPO
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Group getGroup(@PathParam("id") String id) {
		Group group = repository.getGroup(id);
		if (group == null) throw new NotFoundException("The group with id: [" + id + "] was not found");
		return group;
	}
	
	
	
}
