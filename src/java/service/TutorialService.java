/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import model.Tutorial;
import model.TutorialInfo;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Dhaval
 */
public class TutorialService
{

    public static Tutorial getTutorial(String name)
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	Tutorial tutorial = (Tutorial) session.get(Tutorial.class, name);
	session.close();
	return tutorial;
    }

    public static List<TutorialInfo> getTutorialList()
    {
	Session session = HibernateUtil.getSessionFactory().openSession();
	String hql = "SELECT t.name FROM Tutorial t";
	Query query = session.createQuery(hql);
	List<String> tutorialList = query.list();
	session.close();
	List<TutorialInfo> tutorialInfos = new ArrayList<>();
	tutorialList.forEach((string) ->
	{
	    tutorialInfos.add(new TutorialInfo(string));
	});

	return tutorialInfos;
    }
}
