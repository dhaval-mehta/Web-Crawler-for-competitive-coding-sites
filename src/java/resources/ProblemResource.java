/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.ok;
import javax.ws.rs.core.UriInfo;
import model.Link;
import model.Platform;
import model.Problem;
import model.ProblemInfo;
import model.SampleInputOutput;
import security.Role;
import security.Secured;
import service.ProblemService;

/**
 *
 * @author Dhaval
 */
@Path("/problems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProblemResource
{

    @POST
    @Secured(
	    roles =
	    {
		Role.ADMIN
	    })
    public Response addProblem(Problem problem, @Context UriInfo uriInfo)
    {
	for (SampleInputOutput sampleInputOutput : problem.getSampleInputOutputs())
	{
	    sampleInputOutput.setProblem(problem);
	}

	problem = ProblemService.addProblem(problem);
	URI problemUri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(problem.getId())).build();
	return created(problemUri).build();
    }

    @PUT
    @Secured(
	    roles =
	    {
		Role.ADMIN
	    })
    @Path("/{id}")
    public Response updateProblem(@PathParam("id") int id, Problem problem, @Context UriInfo uriInfo)
    {

	problem.setId(id);

	for (SampleInputOutput sampleInputOutput : problem.getSampleInputOutputs())
	{
	    sampleInputOutput.setProblem(problem);
	}

	problem = ProblemService.updateProblem(problem);

	if (id == problem.getId())
	{
	    return ok(problem).build();
	}
	else
	{
	    URI problemUri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(problem.getId())).build();
	    return created(problemUri).build();
	}
    }

    @DELETE
    @Path("/{id}")
    @Secured(
	    roles =
	    {
		Role.ADMIN
	    })
    public void deleteProblem(@PathParam("id") int id)
    {
	try
	{
	    ProblemService.deleteProblem(id);
	}
	catch (IllegalArgumentException ex)
	{
	    throw new NotFoundException(ex);
	}
	catch (Exception ex)
	{
	    throw new InternalServerErrorException(ex);
	}
    }

    @GET
    @Path("/{id}")
    public Problem getProblem(@PathParam("id") int id, @Context UriInfo uriInfo)
    {
	Problem problem = ProblemService.getProblem(id);

	if (problem == null)
	{
	    throw new NotFoundException();
	}
	String href = uriInfo.getBaseUriBuilder().path(ProblemResource.class).path(Integer.toString(id)).build().toString();
	String rel = "self";

	Link link = new Link(href, rel);
	problem.addLink(link);

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
		    System.err.println("ffgfgfgfgfg");
		    throw new BadRequestException("Invalid Platform", e);
		}
	    });
	}

	List<ProblemInfo> problems = ProblemService.getProblems(platforms, tags);
	if (problems.isEmpty())
	{
	    throw new NoContentException("No Problems found.");
	}

	problems.forEach((problem) ->
	{
	    String href = uriInfo.getBaseUriBuilder().path(ProblemResource.class).path(Integer.toString(problem.getId())).build().toString();
	    String rel = "self";

	    Link link = new Link(href, rel);
	    problem.addLink(link);
	});

	return problems;
    }
}
