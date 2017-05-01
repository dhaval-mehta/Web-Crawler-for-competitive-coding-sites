/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import hibernate.HibernateUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import security.SecurityManager;

/**
 *
 * @author Dhaval
 */
@Path("/authentication")
public class AuthenticationEndpoint
{

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(User user, @Context UriInfo uriInfo)
    {
	try
	{
	    // Authenticate the user using the credentials provided
	    user = authenticate(user);

	    // Issue a token for the user
	    String token = "Bearer " + SecurityManager.issueToken(user);

	    // Return the token on the response
	    return Response.ok(token).build();

	}
	catch (Exception e)
	{
	    return Response.status(Response.Status.UNAUTHORIZED).build();
	}
    }

    private User authenticate(User user1) throws Exception
    {
	if (user1 == null)
	{
	    throw new RuntimeException("Unauthenticated User");
	}

	Session session = HibernateUtil.getSessionFactory().openSession();
	Transaction transaction = session.beginTransaction();
	User user = (User) session.get(User.class, user1.getUsername());
	transaction.commit();
	session.close();

	if (user1.getPassword() != null && user1.getPassword().equals(user.getPassword()))
	{
	    return user;
	}
	else
	{
	    throw new RuntimeException("Unauthenticated User");
	}
    }
}
