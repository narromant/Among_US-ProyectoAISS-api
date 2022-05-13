package aiss.api.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.annotations.ContentEncoding;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

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
	
	//   DEVOLVER TODOS LOS USUARIOS
	@GET
	@Produces("application/json")
	public Collection<User> getAll() {
		System.out.println(1);
		return repository.getAllUsers();
	}
	
//	//   CREAR USUARIO A PARTIR DE OBJETO USUARIO
//	@POST
//	@Consumes("application/json")
//	@Produces("application/json")
//	public Response addUser(@Context UriInfo uriInfo, User user) {
//		System.out.println(1);
//		if (user.getName() == null || "".equals(user.getName())) throw new BadRequestException("The user's name can't be null");
//		if (user.getPassword().length() < 6) throw new BadRequestException("The user's password length must be at least 6 digits");
//		repository.addUser(user);
//		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
//		URI uri = ub.build(user.getId());
//		ResponseBuilder resp = Response.created(uri);
//		resp.entity(user);			
//		return resp.build();
//	}

	//   CREAR USUARIO A PARTIR DE NOMBRE Y CONTRASEÑA
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addUser(@Context UriInfo uriInfo, String name, String password) {
		System.out.println(2);
		if (name == null || "".equals(name)) throw new BadRequestException("The user's name can't be null");
		if (password.length() < 6) throw new BadRequestException("The user's password length must be at least 6 digits");
		
		User user = new User(name, password);
		if (repository.getAllUsers().isEmpty()) user.setRole(2);
		else user.setRole(0);
		
		repository.addUser(user);
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(user.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(user);			
		return resp.build();
	}

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
	
	//   FILTRO DE NOMBRES DE USUARIOS DADO UN DATO Y EL VALOR
	//   LISTA ORDENADA DE DATOS DE USUARIO DADO EL DATO
	//   POPULARIZACION DE LOS DATOS?
	//   BANEAR USUARIO COMO ROL 2
	//   MODIFICAR DATOS DE ROL 0 COMO ROL 1
	//   CREAR ROL 1 COMO ROL 2
	//   ELIMINAR ROL 1 COMO ROL 2
	//   LOGIN USUARIO -> [COMPRUBA SI EL NOMBRE Y CONTRASEÑA SON CORRECTOS, CREA UN TOKEN, Y LO DEVUELVE]
	
}
