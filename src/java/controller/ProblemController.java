/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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
public class ProblemController extends Application
{

    @GET
    @Path("/{id}")
    public Response getProblem(@PathParam("id") int id)
    {
	Problem problem = ProblemService.getProblems(id);
	System.err.println(problem);

	if (problem == null)
	{
	    return Response.status(Response.Status.NOT_FOUND).build();
	}

	return Response.ok(problem).build();
    }

    @GET
    public Response getProblems(@Context UriInfo uriInfo)
    {
	List<Integer> platforms = new ArrayList<>();
	MultivaluedMap<String, String> queryParameterMap = uriInfo.getQueryParameters();
	List<String> tags = queryParameterMap.get("tag");
	List<String> platformsInString = queryParameterMap.get("platform");

	if (platformsInString != null)
	{
	    for (String string : platformsInString)
	    {
		try
		{
		    platforms.add(Platform.valueOf(string).ordinal());
		}
		catch (Exception e)
		{
		    return Response.status(Response.Status.BAD_REQUEST).build();
		}
	    }
	}

	List<ProblemInfo> problemList = ProblemService.getProblems(platforms, tags);

	if (problemList.isEmpty())
	{
	    return Response.status(Response.Status.NOT_FOUND).build();
	}

	return Response.ok(problemList).build();
    }
}
