package aiss.api.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;


import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import aiss.api.resources.bodyModels.Id_Token;
import aiss.api.resources.bodyModels.User_Token;
import aiss.api.resources.comparators.ComparatorNameUser;
import aiss.api.resources.comparators.ComparatorNameUserReversed;
import aiss.model.Token;
import aiss.model.User;
import aiss.model.repository.MapUserDataRepository;
import aiss.model.repository.UserDataRepository;

@Path("/users")
public class UserResource {

	/* Singleton */
	public static UserResource _instance=null;
	UserDataRepository repository;
	
	private UserResource() {
		repository = MapUserDataRepository.getInstance();
	}
	
	public static UserResource getInstance() {
		if(_instance == null) _instance = new UserResource();
		return _instance;
	}
	
	////////////////////////////   OPERACIONES CRUD USUARIO   ////////////////////////////
	
	//   DEVOLVER TODOS LOS USUARIOS
	@GET
	@Produces("application/json")
	public Collection<User> getAll(@QueryParam("order") String order) {
		List<User> result = repository.getAllUsers().stream().collect(Collectors.toList());
		if (order != null) {
			if (order.equals(" name")) Collections.sort(result, new ComparatorNameUser());
			else if (order.equals("-name")) Collections.sort(result, new ComparatorNameUserReversed());
			else if (order.equals(" id")) Collections.sort(result, new ComparatorNameUser());
			else if (order.equals("-id")) Collections.sort(result, new ComparatorNameUserReversed());
			else throw new BadRequestException("The order parameter must be +name, -name, +id or -id");
		}
		return result;
	}

	//   CREAR USUARIO A PARTIR DE OBJETO USUARIO
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addUser(@Context UriInfo uriInfo, User user) {
		
		user.setData(new HashMap<>());
		if (repository.getAllUsers().isEmpty()) user.setRole(2);
		else user.setRole(0);
		if (user.getName() == null || "".equals(user.getName())) throw new BadRequestException("The user's name can't be null");
		if (user.getPassword() == null || user.getPassword().length() < 6) throw new BadRequestException("The user's password length must be at least 6 digits");
		repository.addUser(user);
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass());
		
		URI uri = ub.build(user.getId());

		ResponseBuilder resp = Response.created(uri);
		resp.entity(user);			
		return resp.build();
	}
	
	
//	//   CREAR USUARIO A PARTIR DE NOMBRE Y CONTRASEÑA
//	@POST
//	@Consumes("application/json")
//	@Produces("application/json")
//	public Response addUser(@Context UriInfo uriInfo) {
//		String name = "nacho";
//		String password = "heleeep";
//
//		if (name == null || "".equals(name)) throw new BadRequestException("The user name can not be null");
//		if (password.length() < 6) throw new BadRequestException("The user password length must be at least 6 digits");
//		
//		User user = new User(name, password);
//		if (repository.getAllUsers().isEmpty()) user.setRole(2);
//		else user.setRole(0);
//		repository.addUser(user);
//		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
//		System.out.println("\n\n\n\n");
//		URI uri = ub.build(user.getId());
//		System.out.println("\n\n\n\n");
//		ResponseBuilder resp = Response.created(uri);
//		resp.entity(user);			
//		return resp.build();
//	}

	//   DEVOLVER UN USUARIO
	@GET
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public User getUser(@PathParam("id") String id, String token) {
		System.out.println(token);
		User user = repository.getUser(id);
		if (user == null) throw new NotFoundException("The user with id: [" + id + "] was not found");
		if (repository.checkCorrectToken(user.getName(), token)) throw new BadRequestException("The identification token is not correct");
		return user;
	}
	
	//   ELIMINAR UN USUARIO
	@DELETE
	@Path("/{id}")
	public Response removeUser(@PathParam("id") String id) {
		User toberemoved = repository.getUser(id);
		if (toberemoved == null) throw new NotFoundException("The user with id: ["+ id +"] was not found");
		else repository.deleteUser(id);
		return Response.noContent().build();
	}
	
//  EDITAR EL NOMBRE DEL USUARIO [NECESITA TOKEN]
   @PUT
   @Path("/{id}")
   @Consumes("application/json")
   public Response updateUser(@PathParam("id") String id, User_Token body) {
       
       User user = body.getUser();
       String token = body.getToken();
       User oldUser = repository.getUser(id);
       
       System.out.println("\\n\\n\\n"+token);
       System.out.println(oldUser.getName());
       System.out.println(repository.getTokenUserId(token));
       System.out.println(token.equals(repository.getTokenUserId(id)));
       
       if (oldUser == null) throw new NotFoundException("The user with id: [" + id + "] was not found");  
       if (oldUser.getId() != repository.getTokenUserId(token)) throw new BadRequestException("The identification token is not correct");        
       if (user.getData() != null || user.getRole() != null || user.getPassword() != null) throw new BadRequestException("You can only change the name of the user with this function.");
       if (user.getName()!=null) oldUser.setName(user.getName());
       
       return Response.noContent().build();
   }
	
	//   INSERTAR UN NUEVO DATO DE USUARIO
	@POST	
	@Path("/{userId}/{dataKey}/{dataValue}")
	@Consumes("text/plain")
	@Produces("application/json")
	public Response addData(@Context UriInfo uriInfo,@PathParam("userId") String userId, @PathParam("dataKey") String dataKey, @PathParam("dataValue") String dataValue) {				
	
		User user = repository.getUser(userId);
		if (user == null) throw new NotFoundException("The user with id: [" + userId + "] was not found");
		
		if (repository.getOneData(userId, dataKey) != null) throw new BadRequestException("The data is already included in the user.");

		repository.updateData(userId, dataKey, dataValue);		

		// Builds the response
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(userId);
		ResponseBuilder resp = Response.created(uri);
		resp.entity(user);			
		return resp.build();
	}
	
	//   EDITAR UN DATO DE USUARIO
	@DELETE
	@Path("/{userId}/{dataKey}")
	public Response removeDataKey(@PathParam("userId") String userId, @PathParam("dataKey") String dataKey) {
		User user = repository.getUser(userId);
		String dato = repository.getOneData(userId, dataKey);
		
		if (user == null) throw new NotFoundException("The playlist with id: [" + userId + "] was not found");
		if (dato == null) throw new NotFoundException(String.format("The data [%s] was not found", dato));
		
		repository.deleteData(userId, dataKey);		
		
		return Response.noContent().build();
	}
	
	////////////////////////////   OPERACIONES EXCLUSIVAS DE ROLES SUPERIORES   ////////////////////////////
	
	//   DEVUELVE TODOS LOS USUARIOS ADMINS
	@GET
	@Path("/admins")
	@Produces("application/json")
	public Collection<User> getAllAdmins() {
		return repository.getAllUsers().stream().filter(x -> x.getRole() == 1).collect(Collectors.toList());
	}
	
	//   CAMBIAR ROL DE USUARIO
	@PUT
    @Path("/admins")
	@Consumes("application/json")
    public Response switchRole(Id_Token body) {
        
        String userId = body.getUser();
        String ownerToken = body.getToken();
        User owner = repository.getUser(repository.getTokenUserId(ownerToken));
        if (owner == null) throw new BadRequestException("The token does not exist");
        if (owner.getRole() != 2) throw new UnauthorizedException("This function is exclusive to the owner");
        User user = repository.getUser(userId);
        if (user.getRole() == 2) throw new BadRequestException("The owner can not change roles");
        else if (user.getRole() == 0) user.setRole(1);
        else if (user.getRole() == 1) user.setRole(0);
        return Response.noContent().build();
    }
	
	////////////////////////////   OPERACIONES DE GRUPOS   ////////////////////////////
	
	
	////////////////////////////   OPERACIONES DE FILTRADO Y ORDENACION   ////////////////////////////
	
	
	
	//   TODO SIN HACER
	
	//   CRUD DATOS DE USUARIO
	
	//   FILTRO DE NOMBRES DE USUARIOS DADO UN DATO Y EL VALOR
	
	//   ACCESO A FUNCIONES DE ROL 1 Y 2
	
	//   TODO ACTUALIZAR
	
	//   DELETE USER // BANEAR USUARIO COMO ROL 2
	
}
