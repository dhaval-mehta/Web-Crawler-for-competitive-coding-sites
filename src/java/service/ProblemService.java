/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.Platform;
import model.Problem;
import model.ProblemInfo;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Dhaval
 */
public class ProblemService
{

    public static List<ProblemInfo> getProblems(List<Integer> platforms, List<String> tags)
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	String sql = "SELECT id, title, platform, problem_url from problem ";

	boolean emptyPlatform = true;
	boolean emptyTags = true;

	if (platforms != null && platforms.size() > 0)
	{
	    emptyPlatform = false;
	    sql += "where platform in(:platforms)";
	}

	if (tags != null && tags.size() > 0)
	{
	    emptyTags = false;

	    if (emptyPlatform)
	    {
		sql += "where ";
	    }
	    else
	    {
		sql += "and ";
	    }

	    sql += "id in(select id from tags where tag_name in (:tags))";
	}

	SQLQuery query = session.createSQLQuery(sql);

	if (!emptyPlatform)
	{
	    query.setParameterList("platforms", platforms);
	}
	if (!emptyTags)
	{
	    query.setParameterList("tags", tags);
	}

	List result = query.list();
	List<ProblemInfo> problems = new ArrayList<>();

	for (Iterator iterator = result.iterator(); iterator.hasNext();)
	{
	    Object[] objects = (Object[]) iterator.next();
	    int id = (Integer) objects[0];
	    String title = (String) objects[1];
	    Platform platform = Platform.fromOrdinal((Integer) objects[2]);
	    String problemUrl = (String) objects[3];
	    ProblemInfo problemInfo = new ProblemInfo(id, title, platform, problemUrl);
	    problems.add(problemInfo);
	}

	session.close();
	return problems;
    }

    public static Problem getProblems(int id)
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	Problem problem = (Problem) session.get(Problem.class, id);
	System.err.println(problem);
	session.close();
	return problem;
    }
}
