package aiss.api.resources;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import aiss.model.Token;
import aiss.model.User;
import aiss.model.repository.MapUserDataRepository;
import aiss.model.repository.UserDataRepository;

@Path("/tokens")
public class TokenResource {

	/* Singleton */
	public static TokenResource _instance=null;
	UserDataRepository repository;
	
	private TokenResource() {
		repository = MapUserDataRepository.getInstance();
	}
	
	public static TokenResource getInstance() {
		if(_instance == null) _instance = new TokenResource();
		return _instance;
	}
	
	//   DEVOLVER TODOS LOS TOKENS
	@GET
	@Produces("application/json")
	public Collection<Token> getAll() {
		return repository.getAllTokens();
	}
	
	//  INICIA SESION [DEVUELVE TOKEN]
	@POST
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public String createUserToken(@PathParam("id") String id, String password) {
		System.out.println(String.format("\n\n\nUser %s attempted to login with password %s.\n\n\n", id, password));
		User user = repository.getUser(id);

		if (user == null) throw new BadRequestException("User not found");
		if (repository.getUserIdToken(id) != null) throw new BadRequestException("User is already logged");
		if (!user.getPassword().equals(password)) throw new BadRequestException("Incorrect password. Please try again.");
		repository.addToken(user);
		Token token = repository.getUserIdToken(id);
		return token.getValue();
	}
	
	//  CIERRA SESION [ELIMINA TOKEN]
	@DELETE
	@Path("/{id}")
	public Response deleteUserToken(@PathParam("id") String id) {
		System.out.println(String.format("User %s attempted to logout.", id));
		Token token = repository.getUserIdToken(id);
		if (token == null) throw new NotFoundException("The user with id: ["+ id +"] is not logged.");
		repository.removeToken(token.getId());
		return Response.noContent().build();
	}
	
}
