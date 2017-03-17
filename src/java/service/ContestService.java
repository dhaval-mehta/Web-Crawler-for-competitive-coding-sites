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

    private static final String CODEFORCES_CONTEST_PAGE_URL = "http://www.codeforces.com/contests";
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

	if (diff > 1)
	{
	    gatherContestInfo();
	}

	return contests;
    }

    private static void gatherContestInfo() throws MalformedURLException, ParseException, IOException
    {
	contests.clear();
	addCodeforcesContests();
	gatheredTime = new Date();
    }

    private static void addCodeforcesContests() throws MalformedURLException, ParseException, IOException
    {
	Document document = Jsoup.connect(CODEFORCES_CONTEST_PAGE_URL).get();
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
	    String name = titleCell.text();

	    Element dateCell = cells.get(2);
	    SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm");
	    sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

	    Date date = sdf.parse(dateCell.text());
	    Contest contest = new Contest(name, contestUrl, platform, date);
	    contests.add(contest);
	}
    }

}
