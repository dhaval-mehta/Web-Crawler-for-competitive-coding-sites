/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Dhaval
 */
public class TorConncetor
{

    public static void connect()
    {
	try
	{
	    System.setProperty("socksProxyHost", "127.0.0.1");
	    System.setProperty("socksProxyPort", "9150");

	    Document doc = Jsoup.connect("https://check.torproject.org/?lang=en_US").get();
	    Element contentDiv = doc.getElementsByClass("content").first();
	    String response = contentDiv.getElementsByTag("h1").first().text();

	    switch (response)
	    {
		case "Congratulations. This browser is configured to use Tor.":
		    Logger.getLogger(TorConncetor.class.getName()).log(Level.INFO, "Connected with Tor.");
		    break;
		case "Sorry. You are not using Tor.":
		    throw new RuntimeException("Sorry. You are not using Tor.");
		default:
		    throw new RuntimeException("Auto torrent connection detection failed.");
	    }
	}
	catch (IOException ex)
	{
	    Logger.getLogger(TorConncetor.class.getName()).log(Level.SEVERE, "Could not connect with Tor. Crawling Aborted", ex);
	    System.exit(1);
	}
    }
}
