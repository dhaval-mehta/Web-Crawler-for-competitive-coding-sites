/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.Problem;
import model.SampleInputOutput;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dhaval
 */
public class SpojCrawler implements Crawler
{

    LinkedHashSet<String> ProblemLinks = new LinkedHashSet<String>();
    LinkedHashSet<String> CrawlableLinks = new LinkedHashSet<String>();
    TreeSet<String> CrawledLinks = new TreeSet<String>();
    LinkedHashSet<String> temporary = new LinkedHashSet<String>();
    int temp = 0, counter = 0, flag = 0;
    String baseURL = "http://www.spoj.com";

    List<String> tags = new ArrayList<String>();
    String input_format = "", output_format = "", time_limit = "";
    String problem_statement = "", input = "", output = "", explanations = "", constraints = "";

    @Override
    public void crawl()
    {
	try
	{

	    CrawlableLinks.add(baseURL);

	    while (true)
	    {
		for (String link : CrawlableLinks)
		{
		    if (CrawledLinks.contains(link))
		    {
			flag = 0;
			continue;
		    }
		    else
		    {
			flag = 1;
			// System.out.println(link+"--> mainlink  \n");
			getAllLinks(link);
			CrawledLinks.add(link);
		    }
		}
		if (flag == 0)
		{
		    break;
		}
		CrawlableLinks.addAll(temporary);
		temporary.clear();
		//System.out.println(CrawlableLinks.toString());
		//System.out.println("************\n");
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	System.out.println("Total ProblemLinks are: " + ProblemLinks.size());

	// Scrap process for each problem link //
	for (String problemlink : ProblemLinks)
	{
	    input_format = "";
	    output_format = "";
	    time_limit = "";
	    problem_statement = "";
	    input = "";
	    output = "";
	    explanations = "";
	    constraints = "";

	    try
	    {
		// String problemlink="http://www.spoj.com/HAUIANDU/problems/MSUBSTR/";

		Document doc = Jsoup.connect(problemlink).get();

		//To Scrap problem tags from problem page. //
		Element problem_tags = doc.getElementById("problem-tags");
		if (problem_tags != null)
		{
		    // System.out.println("inside ");
		    Elements tagele = problem_tags.getElementsByTag("a");
		    if (tagele == null)
		    {
			tags.add("no tags");
		    }
		    else
		    {
			for (Element e : tagele)
			{
			    tags.add(e.text());
			}
		    }
		}
		else
		{
		    tags.add("no tags");
		}

		//To Scrap time limit of problem. //
		int t_flag = 0;
		Elements elements = doc.getAllElements();
		for (Element element : elements)
		{
		    if (element.ownText().contains("Time limit:"))
		    {
			t_flag = 1;
			continue;
		    }
		    else
		    {
			time_limit = "1000";
		    }

		    if (t_flag == 1)
		    {
			time_limit = element.ownText();

			time_limit = time_limit.replace("s", "");
			if (time_limit.contains("-"))
			{
			    time_limit = time_limit.substring(0, time_limit.indexOf("-"));
			}
			t_flag = 0;
			break;
		    }
		}

		//If Problem link is from practice problems. //
		Element problem_body = doc.getElementById("problem-body");

		//If problem link is from Contest. //
		Element prob = doc.getElementsByClass("prob").first();

		if (problem_body != null)
		{
		    scrapProblem(problem_body);
		}
		else
		{
		    scrapProblem(prob);
		}

		//To save problem in database. //
		HibernateUtil hu = new HibernateUtil();
		SessionFactory factory = hu.getSessionFactory();
		Session session = factory.openSession();
		Transaction T = session.beginTransaction();
		Problem p = new Problem();

		p.setProblemStatement(problem_statement);
		p.setProblemUrl(problemlink);
		p.setExplanation(explanations);
		p.setConstraints(constraints);
		p.setInputFormat(input_format);
		p.setOutputFormat(output_format);
		SampleInputOutput s = new SampleInputOutput(p, input, output);

		s.setInput(input);
		s.setOutput(output);
		//   System.out.println(constraints+"\n\n\n\n"+output+"\n\n\n"+explanations);
		List<SampleInputOutput> l = new ArrayList<SampleInputOutput>();
		l.add(s);
		p.setSampleInputOutputs(l);
		p.setTimeLimit(Double.parseDouble(time_limit));

		p.setTags(tags);
		session.save(p);
		T.commit();
		session.close();

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
		System.out.println("Inside crawl");
	    }

	}
    }

    //This Method collect all problem link and crawlable links from given URL. //
    public void getAllLinks(String URL)
    {
	try
	{

	    String pattern = "(/[A-Z0-9]+)/";
	    Pattern p = Pattern.compile(pattern);

	    Document doc = Jsoup.connect(URL).timeout(0).get();
	    Elements eles = doc.getElementsByTag("a");
	    for (Element e : eles)
	    {

		Matcher m = p.matcher(e.attr("href"));
		String href = e.attr("href");
		if (href.contains("rss") || href.contains("schools") || href.contains("lang=") || href.contains("status")
			|| href.contains("ranks") || href.contains("rss") || href.contains("http://")
			|| href.contains("tag") || href.contains("info"))
		{
		    continue;
		}
		if (e.attr("href").contains("problems") || m.matches() || e.attr("href").contains("contests"))
		{
		    String checkLink = baseURL + e.attr("href");
		    if (isProblemPage(checkLink))
		    {
			ProblemLinks.add(checkLink);
		    }
		    else
		    {
			temporary.add(checkLink);

		    }
		}
	    }
	}
	catch (Exception e)
	{
	    System.out.println(e.getMessage());
	}
    }

    // This method decide Page is problem page or not. //
    private boolean isProblemPage(String link)
    {
	try
	{
	    String pattern = "(.*)(problems)(/[A-Z0-9_]+)(/*)";
	    Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(link);

	    String pattern1 = "(.*)(/[A-Z0-9_]+)/(problems)(/[A-Z0-9_]+)(/*)";
	    Pattern p1 = Pattern.compile(pattern);
	    Matcher m1 = p1.matcher(link);

	    if (m.matches() || m1.matches())
	    {
		return true;
	    }
	    else
	    {
		return false;
	    }
	}
	catch (Exception e)
	{

	}
	return false;

    }

    // Method to Scrap problem //
    private void scrapProblem(Element element)
    {
	//To Scrap Problem Statement,Input,Output.
	Elements eles = element.getAllElements();
	int flag = 0;
	for (Element ele : eles)
	{

	    String owntext = ele.ownText();
	    if (flag == 0 && owntext.contains("Input") != true)
	    {
		problem_statement = problem_statement + owntext;
	    }
	    if (flag == 0 && (owntext.contains("input") == true || owntext.contains("Input") == true || owntext.contains("INPUT") == true))
	    {
		flag = 1;
		continue;
	    }
	    if (flag == 1 && !(owntext.contains("output") == true || owntext.contains("Output") == true || owntext.contains("OUTPUT") == true))
	    {
		input_format = input_format + owntext;
	    }

	    if (flag == 1 && (owntext.contains("output") == true || owntext.contains("Output") == true || owntext.contains("OUTPUT") == true))
	    {
		flag = 2;
		continue;
	    }
	    if (flag == 2 && !(owntext.contains("Example") == true || owntext.contains("Input") == true || owntext.contains("Sample") == true || owntext.contains("example") == true || owntext.contains("Score") == true))
	    {
		output_format = output_format + owntext;

	    }
	    if (flag == 2 && owntext.contains("Score") == true)
	    {
		flag = 3;
		continue;
	    }
	    if (flag == 3 && owntext.contains("Example") == false)
	    {
		explanations = explanations + owntext;
		continue;
	    }
	    if ((flag == 2 || flag == 3) && (owntext.contains("Example") == true || owntext.contains("Input") == true || owntext.contains("Sample") == true))
	    {
		flag = 4;
		break;
	    }
	}

	//To Scrap Constraint,Sample input,Sample Output.Score,Warning.//
	String problem_body_text = element.text();

	if (problem_body_text.contains("Constraint"))
	{
	    int constraint_index = problem_body_text.indexOf("Constraint");
	    String afterconstraint = problem_body_text.substring(constraint_index + 11);
	    if (afterconstraint.contains("Example"))
	    {
		int exampleindex = afterconstraint.indexOf("Example");
		constraints = afterconstraint.substring(0, exampleindex);
	    }
	    else if (afterconstraint.contains("Sample"))
	    {
		int sampleindex = afterconstraint.indexOf("Sample");
		constraints = afterconstraint.substring(0, sampleindex);
	    }
	    else if (afterconstraint.contains("Input"))
	    {
		int inputindex = afterconstraint.indexOf("Input");
		constraints = afterconstraint.substring(0, inputindex);
	    }

	}

	int firstInput = problem_body_text.indexOf("Input");
	String afterFirstInput = problem_body_text.substring(firstInput + 5);
	int secondInput = afterFirstInput.indexOf("Input");
	String afterSecondInput = afterFirstInput.substring(secondInput + 5);
	int asi;
	if (afterSecondInput.contains("Output") == false)
	{
	    asi = afterSecondInput.indexOf("Sample") + 7;
	}
	else
	{
	    asi = afterSecondInput.indexOf("Output");
	}
	input = afterSecondInput.substring(0, asi);
	if (input.contains("Sample"))
	{
	    input = input.replace("Sample", "");
	}
	if (input.contains(":"))
	{
	    input = input.replace(":", "");
	}
	output = afterSecondInput.substring(asi + 6);
	if (output.contains(":"))
	{
	    output = output.replace(":", "");
	}
	if (output.contains("Tips"))
	{
	    int tipsindex = output.indexOf("Tips");
	    explanations = output.substring(tipsindex + 4);
	    output = output.substring(0, tipsindex);
	}
	if (output.contains("Scoring"))
	{
	    int scoringindex = output.indexOf("Scoring");
	    explanations = output.substring(scoringindex + 7);
	    output = output.substring(0, scoringindex);
	}
	if (output.contains("Warning"))
	{
	    int Warningindex = output.indexOf("Warning");
	    explanations = explanations + output.substring(Warningindex + 7);
	    output = output.substring(0, Warningindex);
	}
	if (output.contains("Explanation"))
	{
	    int explanationindex = output.indexOf("Explanation");
	    explanations = explanations + output.substring(explanationindex);
	    output = output.substring(0, explanationindex);

	}
	if (output.contains("Note"))
	{
	    int noteindex = output.indexOf("Note");
	    explanations = explanations + output.substring(noteindex + 4);
	    output = output.substring(0, noteindex);
	}

    }

}
