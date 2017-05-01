/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import hibernate.HibernateUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Platform;
import model.Problem;
import model.SampleInputOutput;
import model.Tutorial;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dhaval
 */
public class HackerEarthCrawler implements Crawler
{

    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String baseUrl = "https://www.hackerearth.com/";

    public HackerEarthCrawler()
    {
    }

    //to check if given url has problem data or not
    public static boolean isProblemUrl(String str)
    {
	//keywords which can't be in problem url
	String[] discard =
	{
	    "/#", "/activity/", "@", "/messages/", "/companies/", "/logout/", "/login/", "/jobs/", "/signup/", "/recruit", "/recruiter/", "/leaderboard/", "/ama/", "/notes/", "/tutorial/", "/customers/", "/users/", "privacy/", "/docs/", "/sql/", "/multiplayer", "/machine-learning/", "/mapreduce/", "/frontend/"
	};
	for (int i = 0; i < discard.length; i++)
	{
	    if (str.contains(discard[i]))
	    {

		return false;
	    }
	}
	//if url don't contain 'practice-problem' or 'problem',it's not a problem url
	if (!str.contains("/practice-problems/") && !str.contains("/problem/"))
	{

	    return false;
	}
	else if ((str.contains("/practice-problems/") && str.endsWith("/practice-problems/")) || str.contains("p_level=#") || str.contains("p_level="))
	{

	    return false;
	}
	return true;
    }

    //to check if given url has any problem url or not
    public static boolean isCrawlable(String str)
    {
	//if these keywords are present in url it cannot have any new problem urls
	String[] discard =
	{
	    "/problem/", "@", "/messages/", "/companies/", "/logout/", "/login/", "/jobs/", "/signup/", "/recruit", "/recruiter/", "/leaderboard/", "/ama/", "/notes/", "/tutorial/", "/customers/", "/users/", "privacy/", "/docs/"
	};
	for (int i = 0; i < discard.length; i++)
	{
	    if (str.contains(discard[i]))
	    {
		return false;
	    }
	}
	//if this conditions satisfies it is problem url itself,so not need to be further crawl
	if (str.contains("/practice-problems/") && !str.endsWith("/practice-problems/") && !str.endsWith("p_level="))
	{
	    return false;
	}
	else if (str.endsWith("/#"))
	{
	    return false;
	}
	return true;
    }

    @Override
    public void crawl()
    {

	int flag = 0;

	//set of urls which should be crawled
	TreeSet<String> linksset = new TreeSet<String>();
	TreeSet<String> tempset = new TreeSet<String>();
	TreeSet<String> tutorialset = new TreeSet<String>();
	//final set of problem urls
	TreeSet<String> problemset = new TreeSet<String>();
	//visited for maintaing status of if url is already crawled or not
	TreeMap<String, Integer> visited = new TreeMap<String, Integer>();

	//add base url
	linksset.add(baseUrl);
	//mark base url as not crawled
	visited.put(baseUrl, 0);

	try
	{
	    while (true)
	    {
		flag = 0;
		tempset.clear();

		for (String str : linksset)
		{
		    //check if url is already crawled or not and it has valid domain name
		    if ((visited.get(str) == 0) && (str.startsWith("https://www.hackerearth.com/")))
		    {
			System.out.println("crawling  " + str);

			//retriving response of current url as document
			Document doc = Jsoup.connect(str).timeout(0).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").ignoreHttpErrors(true).get();
			//retriving all urls from current page
			Elements links = doc.select("a[href]");

			//mark url as crawled
			visited.put(str, 1);

			//mark flag as url is crawled
			flag = 1;
			//retrive all urls
			for (Element link : links)
			{
			    if (link.absUrl("href").endsWith("/tutorial/"))
			    {
				tutorialset.add(link.absUrl("href"));
			    }
			    //check if url is problem url then add it in problemurlset
			    if (link.absUrl("href").startsWith("https://www.hackerearth.com/") && isProblemUrl(link.absUrl("href")))
			    {
				problemset.add(link.absUrl("href"));
			    }
			    //check if url has valid domain and it has problem urls or not
			    if (link.absUrl("href").contains(("https://www.hackerearth.com/")) && isCrawlable(link.absUrl("href")))
			    {
				//if link is not visited then mark it as uncrawled
				if (!visited.containsKey(link.absUrl("href")))
				{
				    visited.put(link.absUrl("href"), 0);
				}
				//add it in tempsetorary set
				tempset.add(link.absUrl("href"));
				//System.out.println("\n  base: "+str+" ::: link  : " + link.absUrl("href"));
			    }
			}
		    }
		}
		//if nothing is left to crawl break the loop
		if (flag == 0)
		{
		    break;
		}
		//add all retrieved links to linksset
		linksset.addAll(tempset);
	    }

	    System.out.println("\n\ntotal problem urls " + problemset.size());

	    int i = 0;
	    for (String str : problemset)
	    {
		System.out.println("link " + i + " : " + str);
		i++;
	    }

	}
	catch (IOException ex)
	{
	    Logger.getLogger(HackerEarthCrawler.class.getName()).log(Level.SEVERE, null, ex);
	}

	//scrap and store into database
	//for every problem url scrap problem page
	for (String problemUrl : problemset)
	{

	    System.out.println("problemUrl :" + problemUrl);
	    try
	    {
		//create problem class to store in database
		Problem problem = new Problem();
		String problemSIOC = "", problemIOC = "";
		String problemTitle = "", problemStatement = "", problemInput = "", problemOutput = "", problemConstraints = "";
		String sampleInput = "", sampleOutput = "";
		String problemExplanation = "";
		//set default timelimit to 1 second
		double problemTimeLimit = 1.0;
		ArrayList<String> tags = new ArrayList<String>();

		//get response for given problem url
		Response response = Jsoup.connect(problemUrl).execute();
		Document doc = response.parse();

		//retrieve problem title from page
		Element elementTitle = doc.getElementsByTag("title").first();
		StringTokenizer stTitle = new StringTokenizer(elementTitle.text(), "|");
		problemTitle = stTitle.nextToken().trim();

		Element content = doc.getElementsByClass("starwars-lab").first();
		problemSIOC = content.text();
		Elements e = content.children();

		//to find problem statement
		String breakloop[] =
		{
		    "input", "input:", "input :", "input format:", "input format :", "input format", "Input and output", "constraints :", "constraints:", "constraints", "$$Input :$$"
		};
		flag = 0;
		for (Element p : e)
		{
		    String tempStatement = "";
		    for (Element pp : p.getAllElements())
		    {

			for (String strbreak : breakloop)
			{
			    if (StringUtils.equalsIgnoreCase(pp.ownText(), strbreak))
			    {
				//System.out.println("strbreak :"+strbreak);

				tempStatement = p.text().substring(0, p.text().toLowerCase().indexOf(strbreak.toLowerCase()));
				// System.out.println("temp "+tempStatement);
				flag = 1;
				break;
			    }
			}
		    }

		    if (flag == 1)
		    {
			problemStatement += tempStatement;
			//remove extra space at end
			if (tempStatement.length() == 0)
			{
			    problemStatement = problemStatement.substring(0, problemStatement.length() - 1);
			}
			break;
		    }
		    problemStatement += p.text() + " ";
		}

		System.out.println("problemSIOC :" + problemSIOC);
		System.out.println("problemStatement :" + problemStatement);

		if (problemStatement.length() <= problemSIOC.length())
		{
		    //remove problem statement from whole text and remove extra spaces at the beginning and the end
		    problemIOC = problemSIOC.substring(problemStatement.length()).trim();
		}
		else
		{
		    problemIOC = "";
		}

		System.out.println("problemIOC :" + problemIOC);

		//keywords for identifying input
		String decideInput[] =
		{
		    "Input format :", "Input format:", "Input format", "inputformat:", "inputformat :", "inputformat", "input and output", "input :", "input:", "input"
		};
		//keywords for identifying output
		String decideOutput[] =
		{
		    "output format :", "output format:", "Output format", "outputformat:", "outputformat :", "outputformat", "output :", "output:", "output"
		};
		//keywords for identifying constraint
		String decideConstraint[] =
		{
		    "constraints:", "constraints :", "constraints", "Constraints :", "constraint:", "constraint :", "constraint", "Contraints :"
		};

		int posin = 0, posoutput = 0, poscon = 0, idxin, idxout, idxcon, flaginput = 0, flagoutput = 0, flagcon = 0, inlen = 0, outlen = 0, conlen = 0;

		//find inputformat position,length of keyword
		for (idxin = 0; idxin < decideInput.length; idxin++)
		{
		    if (StringUtils.containsIgnoreCase(problemIOC, decideInput[idxin]))
		    {

			posin = problemIOC.toLowerCase().indexOf(decideInput[idxin].toLowerCase());
			flaginput = 1;
			inlen = decideInput[idxin].length();

			//decide it is keyowrd for actucal input or it is "sample input"
			if (StringUtils.containsIgnoreCase(problemIOC, "sample input"))
			{
			    if (posin > problemIOC.toLowerCase().indexOf("sample input"))
			    {
				flaginput = 0;
				inlen = 0;
			    }
			    else
			    {
				break;
			    }
			}
			else
			{
			    break;
			}
		    }
		}

		//find outputformat position,length of keyword
		for (idxout = 0; idxout < decideOutput.length; idxout++)
		{
		    if (StringUtils.containsIgnoreCase(problemIOC, decideOutput[idxout]))
		    {
			posoutput = problemIOC.toLowerCase().indexOf(decideOutput[idxout].toLowerCase());
			flagoutput = 1;
			outlen = decideOutput[idxout].length();
			break;
		    }
		}

		//find constraint position,length of keyword
		for (idxcon = 0; idxcon < decideConstraint.length; idxcon++)
		{
		    if (StringUtils.containsIgnoreCase(problemIOC, decideConstraint[idxcon]))
		    {
			poscon = problemIOC.toLowerCase().indexOf(decideConstraint[idxcon].toLowerCase());
			flagcon = 1;
			conlen = decideConstraint[idxcon].length();
			break;
		    }
		}

		System.out.println("input " + flaginput + " " + inlen + " " + posin);
		System.out.println("output " + flagoutput + " " + outlen + " " + posoutput);
		System.out.println("constraint " + flagcon + " " + conlen + " " + poscon);
		//retrieve problem input and output if present in problem page

		//if input format is present
		if (flaginput == 1)
		{
		    //if input keyword is "input and output" and contraint is present in problem page
		    if (idxin == 6 && flagcon == 1)
		    {
			problemInput = problemIOC.substring(inlen, poscon);
		    }
		    //if input keyword is "input and output" and contraint is not present in problem page
		    else if (idxin == 6 && flagcon == 0)
		    {
			problemInput = problemIOC.substring(inlen);
		    }
		    //if output format and constraint is present
		    else if (flagoutput == 1 && flagcon == 1)
		    {
			//if constraint is present before input format
			if (poscon < posin)
			{
			    problemInput = problemIOC.substring(posin + inlen, posoutput);
			    problemOutput = problemIOC.substring(posoutput + outlen);
			}
			//if constraint is present before sample
			else if (poscon < posoutput)
			{
			    problemInput = problemIOC.substring(inlen, poscon);
			    problemOutput = problemIOC.substring(posoutput + outlen);
			}
			else
			{
			    problemInput = problemIOC.substring(inlen, posoutput);
			    problemOutput = problemIOC.substring(posoutput + outlen, poscon);
			}
		    }
		    //if constraint is not present
		    else if (flagoutput == 1 && flagcon == 0)
		    {
			problemInput = problemIOC.substring(inlen, posoutput);
			problemOutput = problemIOC.substring(posoutput + outlen);
		    }
		    else if (flagoutput == 0 && flagcon == 1)
		    {
			if (poscon < posin)
			{
			    problemInput = problemIOC.substring(posin + inlen);
			}
			else
			{
			    problemInput = problemIOC.substring(poscon + conlen, posin);
			}
			problemOutput = "";
		    }
		    else
		    {
			problemInput = problemIOC.substring(inlen);
			problemOutput = "";
		    }
		}
		//if input format and output format is not present
		else
		{
		    problemInput = "";
		    problemOutput = "";
		}

		//if constraint is present
		if (flagcon == 1)
		{
		    //if constraint is present before input format
		    if (poscon < posin)
		    {
			problemConstraints = problemIOC.substring(0, posin);
		    }
		    //if constraint is present before output format
		    else if (poscon < posoutput)
		    {
			problemConstraints = problemIOC.substring(poscon + conlen, posoutput);
		    }
		    else
		    {
			problemConstraints = problemIOC.substring(poscon + conlen);
		    }
		}

		System.out.println("problemInput :" + problemInput);
		System.out.println("problemOutput :" + problemOutput);
		System.out.println("problemConstraints :" + problemConstraints);

		//retrieve problem tags from problem page
		Element elementtag = doc.getElementsByClass("problem-tags").first().child(1);
		StringTokenizer st = new StringTokenizer(elementtag.text(), ",");
		while (st.hasMoreTokens())
		{
		    tags.add(st.nextToken().trim());
		}

		//retrieve sample input sample output if present
		Element elementSIO = doc.getElementsByClass("input-output-container").first();
		//if sample input output is present
		if (elementSIO != null)
		{
		    //find position of sample output
		    int soutpos = elementSIO.text().indexOf("SAMPLE OUTPUT");
		    sampleInput = elementSIO.text().substring(12, soutpos);
		    sampleOutput = elementSIO.text().substring(soutpos + 13);
		    System.out.println("Sample input :\n" + sampleInput + "\n\n\n");
		    System.out.println("Sample Output :\n" + sampleOutput);
		}
		else
		{
		    sampleInput = "";
		    sampleOutput = "";
		}

		//retrieve problem explanation from problem page if present
		Element elementExplanation = doc.getElementsByClass("standard-margin").first().child(0);
		if (elementExplanation.text().toLowerCase().contains("explanation"))
		{
		    problemExplanation = elementExplanation.nextElementSibling().text();
		}
		System.out.println("Explanation :" + problemExplanation);

		//retrieve timelimit
		Element elementTL = doc.getElementsByClass("problem-guidelines").first().child(0).child(1);
		StringTokenizer stTL = new StringTokenizer(elementTL.ownText(), " ");
		problemTimeLimit = Double.parseDouble(stTL.nextToken());

		//System.out.println("problemTimeLimit :"+problemTimeLimit);
		//set all retrieved information to problem class
		problem.setProblemUrl(problemUrl);
		if (problemTitle.length() == 0)
		{
		    problemTitle = null;
		}
		if (problemStatement.length() == 0)
		{
		    problemStatement = null;
		}
		if (problemInput.length() == 0)
		{
		    problemInput = null;
		}
		if (problemOutput.length() == 0)
		{
		    problemOutput = null;
		}
		if (problemExplanation.length() == 0)
		{
		    problemExplanation = null;
		}
		if (problemConstraints.length() == 0)
		{
		    problemConstraints = null;
		}
		problem.setTitle(problemTitle);
		problem.setProblemUrl(problemUrl);
		problem.setProblemStatement(problemStatement);
		problem.setInputFormat(problemInput);
		problem.setOutputFormat(problemOutput);
		problem.setTimeLimit(problemTimeLimit);
		problem.setExplanation(problemExplanation);
		problem.setConstraints(problemConstraints);

		//set sample input output to problem class
		SampleInputOutput sampleInputOutput = new SampleInputOutput(problem, sampleInput, sampleOutput);
		problem.getSampleInputOutputs().add(sampleInputOutput);
		//set platform as hackerearth
		problem.setPlatform(Platform.HackerEarth);
		for (String strtag : tags)
		{
		    problem.getTags().add(strtag);
		}

		//store in database
		Session session = null;
		Transaction transaction = null;
		try
		{
		    //start session
		    session = HibernateUtil.getSessionFactory().openSession();
		    transaction = session.beginTransaction();

		    //check if problem is already stored in database
		    String hql = "FROM Problem p where p.problemUrl = :problem_url";
		    Problem oldProblem = (Problem) session.createQuery(hql).setString("problem_url", problemUrl).uniqueResult();
		    String task;

		    //if problem is present in database
		    if (oldProblem != null)
		    {
			//update the old problem
			task = "updated";
			//retrieve id of old problem
			problem.setId(oldProblem.getId());
			session.delete(oldProblem);
			session.flush();
			session.save(problem);
		    }
		    else
		    {
			task = "saved";
			session.save(problem);
		    }

		    transaction.commit();
		    //log the info to console
		    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.INFO, "{0} {1}", new Object[]
		    {
			task, problem.getProblemUrl()
		    });
		}
		catch (HibernateException ee)
		{
		    if (transaction != null)
		    {
			transaction.rollback();
		    }
		    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, "Cannot Insert/Update problem into databse: " + problemUrl, e);
		}
		finally
		{
		    //close the session
		    if (session != null)
		    {
			session.close();
		    }
		}
	    }
	    catch (Exception ee)
	    {
		System.out.println(ee.toString());
	    }
	}

	System.out.println("\n\n\n\ntutorial urls\n\n");
	try
	{

	    for (String tutorialurl : tutorialset)
	    {
		//System.out.println(tutorialurl+"\n\n");
		Response tutorialres = Jsoup.connect(tutorialurl).execute();
		Document doc = tutorialres.parse();

		Tutorial tutorial = new Tutorial();
		tutorial.setContent(doc.getElementsByClass("tutorial").first().text());

		tutorial.setName(baseUrl);
		tutorialurl = tutorialurl.substring(0, tutorialurl.length() - 10);
		StringTokenizer tutorialtok = new StringTokenizer(tutorialurl, "/");

		String tempstr = "";
		while (tutorialtok.hasMoreTokens())
		{
		    tempstr = tutorialtok.nextToken();
		}

		Session session = null;
		Transaction transaction = null;
		try
		{
		    //start session
		    session = HibernateUtil.getSessionFactory().openSession();
		    transaction = session.beginTransaction();

		    //check if problem is already stored in database
		    String hql = "FROM Tutorial p where p.name = :name";
		    Tutorial oldProblem = (Tutorial) session.createQuery(hql).setString("name", tempstr).uniqueResult();
		    String task;

		    //if problem is present in database
		    if (oldProblem != null)
		    {
			//update the old problem
			task = "updated";
			//retrieve id of old problem
			tutorial.setName(oldProblem.getName());
			session.delete(oldProblem);
			session.flush();
			session.save(tutorial);
		    }
		    else
		    {
			task = "saved";
			tutorial.setName(tempstr);
			session.save(tutorial);
		    }

		    transaction.commit();
		    //log the info to console
		    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.INFO, "{0} {1}", new Object[]
		    {
			task, tutorial.getName()
		    });
		}
		catch (HibernateException ee)
		{
		    if (transaction != null)
		    {
			transaction.rollback();
		    }
		    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, "Cannot Insert/Update problem into databse: " + tempstr, ee);
		}
		finally
		{
		    //close the session
		    if (session != null)
		    {
			session.close();
		    }
		}

	    }
	}
	catch (Exception e)
	{
	    System.out.println(e.getMessage());
	}
    }
}
