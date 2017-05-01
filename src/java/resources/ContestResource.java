/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import model.Contest;
import service.ContestService;

/**
 *
 * @author Dhaval
 */
@Path("/contests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContestResource
{

    @GET
    public List<Contest> getContests()
    {
	try
	{
	    List<Contest> contests = ContestService.getContests();
	    return contests;
	}
	catch (ParseException ex)
	{
	    throw new InternalServerErrorException("Cannot Parse Date.", ex);
	}
	catch (IOException ex)
	{
	    throw new InternalServerErrorException("IO Error.", ex);
	}
    }
}
