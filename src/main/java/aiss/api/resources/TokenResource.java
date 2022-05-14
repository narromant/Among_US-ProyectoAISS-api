package aiss.api.resources;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import aiss.model.Token;
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
	
}
