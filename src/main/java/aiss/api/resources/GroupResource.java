package aiss.api.resources;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;

import aiss.api.resources.bodyModels.Group_Token;
import aiss.api.resources.bodyModels.Id_Token;
import aiss.api.resources.bodyModels.Ids_Token;
import aiss.model.Group;
import aiss.model.User;
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
//  AGREGAR UN NUEVO GRUPO. EXCLUSIVO PARA ROL 1 Y 2
   @POST
   @Consumes("application/json")
   @Produces("application/json")
   public Response addGroup(@Context UriInfo uriInfo, Group_Token body) {
	   Group group = body.getGroup();
	   String adminToken = body.getToken();
	   System.out.println("\n\n\n"+repository.getTokenUserId(adminToken)+"\n\n\n");
       User admin = repository.getUser(repository.getTokenUserId(adminToken));
       //System.out.println("\n\n\n"+admin);
       if (admin == null) throw new BadRequestException("The token does not exist");
       if (admin.getRole() == 0) throw new UnauthorizedException("This function is exclusive to admins and the owner");
       group.setCreator(admin.getId());
       repository.addGroup(group);
       UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass());
       URI uri = ub.build(group.getId());
       ResponseBuilder resp = Response.created(uri);
       resp.entity(group);            
       return resp.build();
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
	
	@PUT
    @Path("/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response editGroup(@Context UriInfo uriInfo, @PathParam("id") String id, Group_Token body) {
        
        Group group = body.getGroup();
        String token = body.getToken();
        Group oldGroup = repository.getGroup(id);
        User creator= repository.getUser(oldGroup.getCreator());
        User admin = repository.getUser(repository.getTokenUserId(token));
        
        if (admin == null) throw new BadRequestException("The token does not exist");
        if (admin.getRole() == 0 || !creator.getId().equals(admin.getId()) && admin.getRole() !=2) throw new UnauthorizedException("This function is exclusive to the creator and the owner");
        
        if (group.getName() != null) oldGroup.setName(group.getName());
        if (group.getDescription() != null) oldGroup.setDescription(group.getDescription());
        
        return Response.noContent().build();
    }
	
    @DELETE
    @Path("/{id}")
    @Consumes("application/json")
    public Response deleteGroup(@Context UriInfo uriInfo, @PathParam("id") String id, String token) {
        Group toberemoved = repository.getGroup(id);
        User creator= repository.getUser(toberemoved.getCreator());
        User admin = repository.getUser(repository.getTokenUserId(token));
        if (toberemoved == null) throw new NotFoundException("The group with id: ["+ id +"] was not found");
        if (admin == null) throw new NotFoundException("The token does not exist");
        if (admin.getRole() == 0 || admin.getId() != creator.getId() && admin.getRole() !=2) throw new UnauthorizedException("This function is exclusive to the creator and the owner");
        repository.deleteGroup(id);
        return Response.noContent().build();
    }
    
    @PUT
    @Path("/{id}/user")
    @Consumes("application/json")
    public Response addUserGroup(@Context UriInfo uriInfo, @PathParam("id") String id, Id_Token body) {
        
        Group group = repository.getGroup(id);
        String token = body.getToken();
        User admin = repository.getUser(repository.getTokenUserId(token));
        User creator= repository.getUser(group.getCreator());
        
        System.out.println("\n"+body.getUser()+"\n");
        System.out.println("\n"+admin.getRole()+"\n");
        
        if (repository.getGroup(id) == null) throw new NotFoundException("The group with id: ["+ id +"] was not found");
        if (admin == null) throw new BadRequestException("The token does not exist");
        if (admin.getRole() == 0 || admin.getId() != creator.getId() && admin.getRole() !=2) throw new UnauthorizedException("This function is exclusive to the creator and the owner");
        if (repository.getUser(body.getUser()) == null) throw new NotFoundException("The user with id: ["+ body.getUser() +"] was not found");
            
        repository.addGroupUser(id, body.getUser());
        return Response.noContent().build();
    }
//  ELIMINA UN MIEMBRO DEL GRUPO. EXCLUSIVO PARA ROL 2 O CREADOR DEL GRUPO
   @DELETE
   @Path("/{id}/user")
   @Consumes("application/json")
   public Response deleteUserGroup(@Context UriInfo uriInfo, @PathParam("id") String id, Id_Token body) {
       
       Group group = repository.getGroup(id);
       String token = body.getToken();
       User admin = repository.getUser(repository.getTokenUserId(token));
       User creator= repository.getUser(group.getCreator());
       
       if (repository.getGroup(id) == null) throw new NotFoundException("The group with id: ["+ id +"] was not found");
       if (admin == null) throw new BadRequestException("The token does not exist");
       if (!group.getUsers().contains(body.getUser())) throw new BadRequestException("The desired user to remove does not belong to the selected group");
       if (admin.getRole() == 0 || admin.getId() != creator.getId() && admin.getRole() !=2) throw new UnauthorizedException("This function is exclusive to the creator and the owner");
       if (repository.getUser(body.getUser()) == null) throw new NotFoundException("The user with id: ["+ body.getUser() +"] was not found");
       
       repository.deleteGroupUser(id, body.getUser());
       return Response.noContent().build();
   }
	
	
   //   AGREGA MULTIPLOS MIEMBROS AL GRUPO. EXCLUSIVO PARA ROL 2 O CREADOR DEL GRUPO
    @PUT
    @Path("/{id}/users")
    @Consumes("application/json")
    public Response addMultipleUsersGroup(@Context UriInfo uriInfo, @PathParam("id") String id, Ids_Token body) {
        
        Group group = repository.getGroup(id);
        String token = body.getToken();
        User admin = repository.getUser(repository.getTokenUserId(token));
        User creator= repository.getUser(group.getCreator());
        
        if (repository.getGroup(id) == null) throw new NotFoundException("The group with id: ["+ id +"] was not found");
        if (admin == null) throw new BadRequestException("The token does not exist");
        if (admin.getRole() == 0 || admin.getId() != creator.getId() && admin.getRole() !=2) throw new UnauthorizedException("This function is exclusive to the creator and the owner");
        for(String userId: body.users) if (repository.getUser(userId) == null) throw new NotFoundException("The user with id: ["+ userId +"] was not found");
        
        for(String userId: body.users) repository.addGroupUser(id, userId); 
        return Response.noContent().build();
    }

}
