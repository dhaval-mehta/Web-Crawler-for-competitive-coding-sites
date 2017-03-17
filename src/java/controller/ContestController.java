/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
public class ContestController
{

    @GET
    public List<Contest> getContests()
    {
	List<Contest> contests = null;
	try
	{
	    contests = ContestService.getContests();
	}
	catch (Exception ex)
	{
	    Logger.getLogger(ContestController.class.getName()).log(Level.SEVERE, null, ex);
	}
	System.err.println(contests);
	return contests;
    }
}
