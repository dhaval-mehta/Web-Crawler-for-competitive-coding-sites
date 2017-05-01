package listener;

import java.util.Timer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import scheduler.CrawlerScheduler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Web application life cycle listener.
 *
 * @author Dhaval
 */
public class ContextListener implements ServletContextListener
{

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
	Timer timer = new Timer();
	CrawlerScheduler scheduler = new CrawlerScheduler();
	int delay = 24 * 60 * 60;
	timer.schedule(scheduler, delay);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {

    }
}
