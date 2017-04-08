/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.UriInfo;
import model.Platform;
import model.Problem;
import model.ProblemInfo;
import service.ProblemService;

/**
 *
 * @author Dhaval
 */
@Path("/problems")
@ApplicationPath("webapi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProblemResource extends Application
{

    @GET
    @Path("/{id}")
    public Problem getProblem(@PathParam("id") int id)
    {
	Problem problem = ProblemService.getProblem(id);

	if (problem == null)
	{
	    throw new NotFoundException();
	}

	return problem;
    }

    @GET
    public List<ProblemInfo> getProblems(@Context UriInfo uriInfo) throws NoContentException
    {
	MultivaluedMap<String, String> queryParameterMap = uriInfo.getQueryParameters();

	List<String> tags = queryParameterMap.get("tag");
	List<String> platformsInString = queryParameterMap.get("platform");

	List<Integer> platforms = new ArrayList<>();
	if (platformsInString != null)
	{
	    platformsInString.forEach((string) ->
	    {
		try
		{
		    platforms.add(Platform.valueOf(string).ordinal());
		}
		catch (Exception e)
		{
		    throw new BadRequestException("Invalid Platform", e);
		}
	    });
	}

	List<ProblemInfo> problems = ProblemService.getProblems(platforms, tags);

	if (problems.isEmpty())
	{
	    throw new NoContentException("No Problems found.");
	}

	return problems;
    }
}
