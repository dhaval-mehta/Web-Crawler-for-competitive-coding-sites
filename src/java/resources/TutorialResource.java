/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import model.Tutorial;
import service.TutorialService;

/**
 *
 * @author Dhaval
 */
@Path("/tutorials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TutorialResource
{

    @GET
    @Path("/{name}")
    public Tutorial getTutorial(@PathParam("name") String name)
    {
	System.err.println(name);
	Tutorial tutorial = TutorialService.getTutorial(name);

	if (tutorial == null)
	{
	    throw new NotFoundException();
	}

	return tutorial;
    }

    @GET
    public List<String> getTutorialList()
    {
	try
	{
	    return TutorialService.getTutorialList();
	}
	catch (Exception e)
	{
	    String message = "There is a problem with the resource you are looking for.";
	    throw new InternalServerErrorException(message, e);
	}
    }
}
