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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

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

    public static Problem getProblem(int id)
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	Problem problem = (Problem) session.get(Problem.class, id);
	session.close();
	return problem;
    }

    public static Problem addProblem(Problem problem)
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	Transaction transaction = session.getTransaction();
	transaction.begin();
	int id = (Integer) session.save(problem);
	transaction.commit();
	session.close();
	problem.setId(id);
	return problem;
    }

    public static Problem updateProblem(Problem problem)
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	Transaction transaction = session.getTransaction();
	transaction.begin();
	Problem problem1 = (Problem) session.get(Problem.class, problem.getId());
	problem1.getSampleInputOutputs().clear();
	problem1.getSampleInputOutputs().addAll(problem.getSampleInputOutputs());
	session.update(problem1);
	transaction.commit();
	transaction = session.beginTransaction();
	problem1.setConstraints(problem.getConstraints());
	problem1.setExplanation(problem.getExplanation());
	problem1.setInputFormat(problem.getInputFormat());
	problem1.setOutputFormat(problem.getOutputFormat());
	problem1.setPlatform(problem.getPlatform());
	problem1.setProblemStatement(problem.getProblemUrl());
	problem1.setProblemUrl(problem.getProblemUrl());
	problem1.setTimeLimit(problem.getTimeLimit());
	problem1.setTitle(problem.getTitle());
	problem1.setTags(problem.getTags());
	session.update(problem1);
	transaction.commit();
	session.close();
	return problem;
    }

    public static void deleteProblem(int id)
    {
	Session session = null;
	Transaction transaction = null;
	try
	{
	    session = HibernateUtil.getSessionFactory().openSession();
	    transaction = session.beginTransaction();
	    Problem problem = (Problem) session.get(Problem.class, id);
	    if (problem == null)
	    {
		throw new IllegalArgumentException("Invalid id " + id);
	    }
	    String hql = "delete " + Problem.class.getName() + " where id = :id";
	    Query query = session.createQuery(hql).setParameter("id", id);
	    query.executeUpdate();
	    transaction.commit();
	}
	catch (HibernateException e)
	{
	    if (transaction != null)
	    {
		transaction.rollback();
	    }

	    throw new RuntimeException(e);
	}
	finally
	{
	    if (session != null)
	    {
		session.close();
	    }
	}
    }
}
