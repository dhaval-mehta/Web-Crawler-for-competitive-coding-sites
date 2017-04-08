/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import model.Contest;
import model.Platform;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dhaval
 */
public class ContestService
{

    private static final String CODEFORCES_CONTEST_PAGE_URL = "http://codeforces.com/contests";
    private static final String CODEFORCES_QUERY_PARAMETER_NAME = "complete";
    private static final String CODEFORCES_QUERY_PARAMETER_VALUE = "true";
    private static final String CODECHEF_CONTEST_PAGE_URL = "https://www.codechef.com/contests";
    private static List<Contest> contests = new ArrayList<>();
    private static Date gatheredTime = null;

    static
    {
	try
	{
	    gatherContestInfo();
	}
	catch (MalformedURLException | ParseException ex)
	{
	    Logger.getLogger(ContestService.class.getName()).log(Level.SEVERE, null, ex);
	}
	catch (IOException ex)
	{
	    Logger.getLogger(ContestService.class.getName()).log(Level.SEVERE, null, ex);
	}
	gatheredTime = new Date();
    }

    public static List<Contest> getContests() throws MalformedURLException, ParseException, IOException
    {
	Date date = new Date();
	long diff = TimeUnit.MILLISECONDS.toMinutes(date.getTime() - gatheredTime.getTime());

//	if (diff > 1)
//	{
	gatherContestInfo();
//	}

	return contests;
    }

    private static void gatherContestInfo() throws MalformedURLException, ParseException, IOException
    {
	contests.clear();
	addCodeforcesContests();
	addCodechefContests();
	gatheredTime = new Date();
    }

    private static void addCodeforcesContests() throws MalformedURLException, ParseException, IOException
    {
	String url;
	url = UriBuilder.fromUri(CODEFORCES_CONTEST_PAGE_URL).queryParam(CODEFORCES_QUERY_PARAMETER_NAME, CODEFORCES_QUERY_PARAMETER_VALUE).build().toString();
	Document document = Jsoup.connect(url).get();
	Element pageContent = document.getElementById("pageContent");
	Element datatable = pageContent.getElementsByClass("datatable").first();
	Element contestTable = datatable.getElementsByTag("tbody").first();
	Elements tableRows = contestTable.getElementsByTag("tr");
	tableRows.remove(0);

	Platform platform = Platform.Codeforces;

	for (Element row : tableRows)
	{
	    String contestCode = row.attr("data-contestid");
	    URL contestUrl = new URL(CODEFORCES_CONTEST_PAGE_URL + "/" + contestCode);

	    Elements cells = row.children();

	    Element titleCell = cells.first();
	    String name = titleCell.ownText();

	    Element dateCell = cells.get(2);
	    SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm");
	    sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

	    Date date = sdf.parse(dateCell.text());
	    Contest contest = new Contest(name, contestUrl, platform, date);
	    contests.add(contest);
	}
    }

    private static void addCodechefContests() throws IOException, ParseException
    {
	Document document = Jsoup.connect(CODECHEF_CONTEST_PAGE_URL).get();
	Element primaryContent = document.getElementById("primary-content");
	Elements dataTable = primaryContent.getElementsByTag("table");

	Platform platform = Platform.CodeChef;

	for (int i = 0; i < 2; i++)
	{
	    Element Contests = dataTable.get(i);
	    Elements rows = Contests.getElementsByTag("tr");
	    rows.remove(0);

	    for (Element row : rows)
	    {
		Elements cells = row.getElementsByTag("td");
		Element contestElement = cells.get(1);
		String name = contestElement.text();
		String contestUrlString = contestElement.getElementsByTag("a").first().absUrl("href");
		URL contestUrl = new URL(contestUrlString);
		String dateString = cells.get(2).attr("data-starttime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		Date date = sdf.parse(dateString);
		Contest contest = new Contest(name, contestUrl, platform, date);
		System.err.println(contest);
		contests.add(contest);
	    }
	}
    }

}
