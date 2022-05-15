package aiss.api.resources;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import aiss.api.resources.bodyModels.Id_Token;
import aiss.api.resources.bodyModels.User_Token;
import aiss.api.resources.bodyModels.Value_Token;
import aiss.api.resources.comparators.ComparatorIdUser;
import aiss.api.resources.comparators.ComparatorIdUserReversed;
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
	
	@GET
    @Produces("application/json")
    public List<User> getAll(@QueryParam("order") String order, @QueryParam("onlyAdmins") Boolean onlyAdmins, @QueryParam("name") String name) {
        
        if (onlyAdmins != null && name != null) throw new BadRequestException("You can not use both filters");
        List<User> unfilteredResult = repository.getAllUsers().stream().collect(Collectors.toList());
        Set<User> result = new HashSet<User>();
        for (User user: unfilteredResult) {
            // COMO NO SE PUEDEN USAR LOS DOS FILTRADOS A LA VEZ SE PUEDE HACER EN DISTINTAS LINEAS
            if (name == null && (onlyAdmins == null || (onlyAdmins && user.getRole() != 0) || (!onlyAdmins))) result.add(user);
            if ((onlyAdmins == null || onlyAdmins == false) && (name == null || user.getName().equals(name))) result.add(user);
        }
        List<User>resultList=result.stream().collect(Collectors.toList());
        if (order != null) {
            if (order.equals(" name")) Collections.sort(resultList, new ComparatorNameUser());
            else if (order.equals("-name")) Collections.sort(resultList, new ComparatorNameUserReversed());
            else if (order.equals(" id")) Collections.sort(resultList, new ComparatorIdUser());
            else if (order.equals("-id")) Collections.sort(resultList, new ComparatorIdUserReversed());
            else throw new BadRequestException("The order parameter must be +name, -name, +id or -id");
        }
        return resultList;
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

	//   DEVOLVER UN USUARIO
	@GET
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public User getUser(@PathParam("id") String id) {
		User user = repository.getUser(id);
		if (user == null) throw new NotFoundException("The user with id: [" + id + "] was not found");
		return user;
	}
	
	 //   ELIMINAR UN USUARIO
    @DELETE
    @Path("/{id}")
    @Consumes("application/json")
    public Response removeUser(@PathParam("id") String id, Id_Token body) {
        String token= body.getToken();
        User toberemoved = repository.getUser(id);
        User deleter = repository.getUser(repository.getTokenUserId(token));
        
        if (deleter == null) throw new NotFoundException("The token does not exist");
        if (toberemoved == null) throw new NotFoundException("The user with id: ["+ id +"] was not found");
        if (toberemoved.getRole()==2) throw new UnauthorizedException("El owner no puede ser eliminado");
        if (deleter.getRole() != 2 && !deleter.getId().equals(toberemoved.getId())) throw new UnauthorizedException("You can not delete other accounts if you are not the owner");
        
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
       
       if (oldUser == null) throw new NotFoundException("The user with id: [" + id + "] was not found");  
       if (oldUser.getId() != repository.getTokenUserId(token)) throw new BadRequestException("The identification token is not correct");        
       if (user.getData() != null || user.getRole() != null || user.getPassword() != null) throw new BadRequestException("You can only change the name of the user with this function.");
       if (user.getName()!=null) oldUser.setName(user.getName());
       
       return Response.noContent().build();
   }
	
   //   OBTENER UN DATO DEL USUARIO [NECESITA TOKEN]
   @GET
   @Path("/{id}/data/{dataKey}")
   @Consumes("application/json")
   @Produces("application/json")
   public String getData(@PathParam("id") String id, @PathParam("dataKey") String dataKey) {
       String dato = repository.getOneData(id, dataKey);
       
       if (repository.getUser(id) == null) throw new NotFoundException("The user with id: [" + id + "] was not found");
       if (dato == null) throw new NotFoundException(String.format("The data [%s] was not found", dato));
       return dato;
   }
   
	@POST    
    @Path("/{id}/data/{dataKey}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addData(@Context UriInfo uriInfo,@PathParam("id") String id, @PathParam("dataKey") String dataKey, Value_Token body) {                
        String token = body.getToken();
        String dataValue = body.getValue();
        User user = repository.getUser(id);
        User adder= repository.getUser(repository.getTokenUserId(token));
 
        if(token==null) throw new NotFoundException("The token does not exist");
        if (user == null) throw new NotFoundException("The user with id: [" + id + "] was not found");
        if(adder.getId() != user.getId() && adder.getRole()==0) throw new NotFoundException("You can not edit another account if you are not an admin or the owner");
        if (repository.getOneData(id, dataKey) != null) throw new BadRequestException("The data is already included in the user.");
        repository.updateData(id, dataKey, dataValue);        
        UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass());
        URI uri = ub.build(id);
        ResponseBuilder resp = Response.created(uri);
        resp.entity(user);            
        return resp.build();
    }
	
//  EDITAR UN NUEVO DATO DE USUARIO
   @PUT    
   @Path("/{userId}/data/{dataKey}")
   @Consumes("application/json")
   @Produces("application/json")
   public Response editData(@Context UriInfo uriInfo,@PathParam("userId") String userId, @PathParam("dataKey") String dataKey, Value_Token body) {                
       String token = body.getToken();
       String dataValue = body.getValue();
       User user = repository.getUser(userId);
       User adder= repository.getUser(repository.getTokenUserId(token));
       
       if(token==null) throw new NotFoundException("The token does not exist");
       if (user == null) throw new NotFoundException("The user with id: [" + userId + "] was not found");
       if(adder.getId() != user.getId() && adder.getRole()==0) throw new NotFoundException("You can not edit another account if you are not an admin or the owner");
       repository.updateData(userId, dataKey, dataValue);        
       UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass());
       URI uri = ub.build(userId);
       ResponseBuilder resp = Response.created(uri);
       resp.entity(user);            
       return resp.build();
   }
	
   @DELETE
   @Path("/{id}/data/{dataKey}")
   @Consumes("application/json")
   public Response removeData(@PathParam("id") String id, @PathParam("dataKey") String dataKey, Value_Token body) {
       
       User user = repository.getUser(id);
       String dato = repository.getOneData(id, dataKey);
       String token = body.getToken();
       String removerId=repository.getTokenUserId(token);
       User remover= repository.getUser(removerId);

       if(token==null) throw new NotFoundException("The token does not exist");
       if (user == null) throw new NotFoundException("The user with id: [" + id + "] was not found");
       if (dato == null) throw new NotFoundException(String.format("The data [%s] was not found", dato));
       if(remover.getId() != user.getId() && remover.getRole()==0) throw new NotFoundException("You can not edit another account if you are not an admin or the owner");
       
       repository.deleteData(id, dataKey);        
       
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
	
}
