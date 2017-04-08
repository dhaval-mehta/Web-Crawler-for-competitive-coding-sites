/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import model.Platform;

/**
 *
 * @author Dhaval
 */
class CrawlerFactory
{

    public static Crawler getCrawler(String platformString, boolean restart)
    {
	Platform platform = Platform.valueOf(platformString);

	switch (platform)
	{
	    case Codeforces:
		return new CodeForcesCrawler(restart);
	    case HackerEarth:
		return new HackerEarthCrawler();
	    case Spoj:
		return new SpojCrawler();
	    default:
		throw new IllegalArgumentException();
	}
    }

}
