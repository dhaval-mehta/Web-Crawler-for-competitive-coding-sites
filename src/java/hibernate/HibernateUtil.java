/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;

import java.util.logging.Level;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author Dhaval
 */
public class HibernateUtil
{

    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    static
    {
	java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
    }

    private static void init()
    {
	Configuration configuration = new Configuration().configure();
	serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
	sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static SessionFactory getSessionFactory()
    {
	if (sessionFactory == null)
	{
	    init();
	}

	return sessionFactory;
    }

    public static void close()
    {
	StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }
}
