package aiss.api.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

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
	public Collection<User> getAll() {
		return repository.getAllUsers();
	}

	//   CREAR USUARIO A PARTIR DE OBJETO USUARIO
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addUser(@Context UriInfo uriInfo, User user) {
		if (user.getName() == null || "".equals(user.getName())) throw new BadRequestException("The user's name can't be null");
		if (user.getPassword().length() < 6) throw new BadRequestException("The user's password length must be at least 6 digits");
		repository.addUser(user);
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(user.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(user);			
		return resp.build();
	}
	
//	//   CREAR USUARIO A PARTIR DE NOMBRE Y CONTRASEÑA
//	@POST
//	@Consumes("application/json")
//	@Produces("application/json")
//	public Response addUser(@Context UriInfo uriInfo, String name, String password) {
//		System.out.println(2);
//		if (name == null || "".equals(name)) throw new BadRequestException("The user name can not be null");
//		//if (password.length() < 6) throw new BadRequestException("The user password length must be at least 6 digits");
//		
//		User user = new User(name, password);
//		if (repository.getAllUsers().isEmpty()) user.setRole(2);
//		else user.setRole(0);
//		
//		repository.addUser(user);
//		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
//		URI uri = ub.build(user.getId());
//		ResponseBuilder resp = Response.created(uri);
//		resp.entity(user);			
//		return resp.build();
//	}

	//   DEVOLVER UN USUARIO
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public User get(@PathParam("id") String id) {
		User user = repository.getUser(id);
		if (user == null) throw new NotFoundException("The user with id: [" + id + "] was not found");
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
	
	//   EDITAR EL NOMBRE DEL USUARIO [NECESITA TOKEN]
	@PUT
	@Consumes("application/json")
	public Response updateUser(User user, String token) {
		User oldUser = repository.getUser(user.getId());
		if (repository.checkCorrectToken(user.getName(), token)) throw new BadRequestException("The identification token is not correct");
		if (oldUser == null) throw new NotFoundException("The user with id: [" + user.getId() + "] was not found");			
		if (user.getData() != null || user.getRole() != null || user.getPassword() != null) throw new BadRequestException("You can only change the name of the user with this function.");
		if (user.getName()!=null) oldUser.setName(user.getName());
		return Response.noContent().build();
	}
	
   ////////////////////////////   OPERACIONES CON TOKENS   ////////////////////////////
	
	//  INICIA SESION [DEVUELVE TOKEN]
	@GET
	@Path("/{id}/login/{password}")
	@Consumes("application/json")
	@Produces("application/json")
	public String createUserToken(@PathParam("id") String id, @PathParam("password") String password) {
		System.out.println(String.format("User %s attempted to login with password %s.", id, password));
		User user = repository.getUser(id);
		if (!user.getPassword().equals(password)) throw new BadRequestException("Incorrect password. Please try again.");
		repository.addToken(user);
		Token token = repository.getUserIdToken(id);
		return token.getValue();
	}
	
	//  CIERRA SESION [ELIMINA TOKEN]
	@DELETE
	@Path("/{id}/logout")
	public Response deleteUserToken(@PathParam("id") String id) {
		System.out.println(String.format("User %s attempted to logout.", id));
		Token token = repository.getUserIdToken(id);
		if (token == null) throw new NotFoundException("The user with id: ["+ id +"] is not logged.");
		repository.removeToken(token.getId());
		return Response.noContent().build();
	}
	
	//   INSERTAR UN NUEVO DATO DE USUARIO
	@POST	
	@Path("/{userId}/{dataKey}")
	@Consumes("text/plain")
	@Produces("application/json")
	public Response addData(@Context UriInfo uriInfo,@PathParam("userId") String userId, @PathParam("dataKey") String dataKey, String dataValue) {				
	
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
	public Response removeSong(@PathParam("userId") String userId, @PathParam("dataKey") String dataKey) {
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
	@Path("/admins/{userId}/{ownerToken}")
	public Response switchRole(String userId, String ownerToken) {
		User owner = repository.getUser(repository.getTokenUserId(ownerToken));
		if (owner == null) throw new BadRequestException("The token does not exist");
		if (owner.getRole() != 2) throw new UnauthorizedException("This function is exclusive to the owner");
		User user = repository.getUser(userId);
		if (user.getRole() == 2) throw new BadRequestException("The owner can not change roles");
		else if (user.getRole() == 0) user.setRole(1);
		else if (user.getRole() == 1) user.setRole(0);
		return Response.noContent().build();
	}
	
	
	//   TODO SIN HACER
	
	//   CRUD DATOS DE USUARIO
	//   LAS MIERDAS DE LOS GRUPOS
	//   FILTRO DE NOMBRES DE USUARIOS DADO UN DATO Y EL VALOR
	//   LISTA ORDENADA DE DATOS DE USUARIO DADO EL DATO
	//   MODIFICAR DATOS DE ROL 0 COMO ROL 1
	
	//   TODO ACTUALIZAR
	
	//   LOGIN
	//   POST USER
	//   DELETE USER // BANEAR USUARIO COMO ROL 2
	//   MODIFICAR ROLES COMO ROL 2 (no se sabe si funciona lol)
	
}
