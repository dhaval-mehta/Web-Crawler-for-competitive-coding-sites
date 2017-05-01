package scheduler;

import crawler.Crawler;
import crawler.CrawlerFactory;
import java.util.TimerTask;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dhaval
 */
public class CrawlerScheduler extends TimerTask
{

    @Override
    public void run()
    {
	Crawler crawler = CrawlerFactory.getCrawler("Codeforces", true);
	crawler.crawl();
	crawler = CrawlerFactory.getCrawler("Spoj", true);
	crawler.crawl();
	crawler = CrawlerFactory.getCrawler("HackerEarth", true);
	crawler.crawl();
    }

}
