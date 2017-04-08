/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import hibernate.HibernateUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Platform;
import model.Problem;
import model.SampleInputOutput;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 *
 * @author Dhaval
 */
public class CodeForcesCrawler implements Crawler
{

    private static final int TAG_INDEX = 3;
    String BaseUrl = "http://www.codeforces.com/contests";
    String requestParameter = "complete=true";

    public CodeForcesCrawler(boolean restart)
    {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void crawl()
    {
	try
	{
	    scrapeContestListPage(BaseUrl + '?' + requestParameter);
	}
	catch (IOException ex)
	{
	    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    private void scrapeContestListPage(String url) throws IOException
    {
	Document doc = Jsoup.connect(url).get();
	Element pageContent = doc.getElementById("pageContent");
	Element contestsTable = pageContent.getElementsByClass("contests-table").first();
	Element datatable = contestsTable.getElementsByClass("datatable").first();
	Element table = datatable.getElementsByTag("table").first();
	Elements rows = table.getElementsByTag("tr");
	rows.remove(0);

	for (Element row : rows)
	{
	    mysleep();
	    Element link = row.getElementsByTag("a").first();
	    String contestUrl = link.absUrl("href");
	    scrapeContestPage(contestUrl);
	}
    }

    private void scrapeContestPage(String contestUrl) throws IOException
    {
	Document doc = Jsoup.connect(contestUrl).get();
	Element pageContent = doc.getElementById("pageContent");
	Element datatable = pageContent.getElementsByClass("datatable").first();
	Element table = datatable.getElementsByTag("table").first();
	Elements rows = table.getElementsByTag("tr");
	rows.remove(0);
	for (Element row : rows)
	{
	    mysleep();
	    Element link = row.getElementsByTag("a").first();
	    String problemUrl = link.absUrl("href");
	    scrapeProblemPage(problemUrl);
	}
    }

    private void scrapeProblemPage(String problemUrl) throws IOException
    {

	Response response = Jsoup.connect(problemUrl).execute();
	Document doc = response.parse();
	Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.INFO, "Processing: {0}\n"
		+ "HTTP Response status: " + response.statusCode() + " " + response.statusMessage() + "\n"
		+ "HTTP Response Size: {1} Characters", new Object[]
		{
		    problemUrl, doc.toString().length()
		});

	String fileName = "";

	for (StringTokenizer stringTokenizer = new StringTokenizer(problemUrl, "/:"); stringTokenizer.hasMoreTokens();)
	{
	    String token = stringTokenizer.nextToken();
	    fileName += token;
	}

	Element pageContent = doc.getElementById("pageContent");
	Element ttypography = doc.getElementsByClass("ttypography").first();
	Element problemStatement = doc.getElementsByClass("problem-statement").first();

	Element header = problemStatement.getElementsByClass("header").first();
	Node propertyTitle = header.getElementsByClass("property-title").first().nextSibling();

	StringTokenizer stringTokenizer = new StringTokenizer(propertyTitle.toString());

	try
	{
	    Problem problem = new Problem();
	    problem.setPlatform(Platform.Codeforces);
	    problem.setProblemUrl(problemUrl);

	    Element titleElement = header.getElementsByClass("title").first();
	    String title = titleElement.text();
	    problem.setTitle(title);

	    double timeLimit = Double.parseDouble(stringTokenizer.nextToken());
	    problem.setTimeLimit(timeLimit);

	    Element problemDescriptionElement = header.nextElementSibling();
	    StringBuilder problemDescription = new StringBuilder();

	    for (Element p : problemDescriptionElement.getElementsByTag("p"))
	    {
		problemDescription.append(p.text());
		problemDescription.append('\n');
	    }
	    problem.setProblemStatement(problemDescription.toString());

	    Element inputFormatElement = problemDescriptionElement.nextElementSibling();
	    StringBuilder inputFormat = new StringBuilder();

	    for (Element p : inputFormatElement.getElementsByTag("p"))
	    {
		inputFormat.append(p.text());
		inputFormat.append('\n');
	    }
	    problem.setInputFormat(inputFormat.toString());

	    Element outputFormatElement = inputFormatElement.nextElementSibling();
	    StringBuilder outputFormat = new StringBuilder();

	    for (Element p : outputFormatElement.getElementsByTag("p"))
	    {
		outputFormat.append(p.text());
		outputFormat.append('\n');
	    }
	    problem.setOutputFormat(outputFormat.toString());

	    String brTagRegex = "(?i)<br\\p{javaSpaceChar}*(?:/>|>)";
	    String newline = "\n";

	    Element sampleTests = outputFormatElement.nextElementSibling();
	    Element sampleInputOutputElement = sampleTests.getElementsByClass("sample-test").first();

	    for (Iterator<Element> iterator = sampleInputOutputElement.getElementsByTag("pre").iterator(); iterator.hasNext();)
	    {
		Element sampleInputElement = iterator.next();
		if (!iterator.hasNext())
		{
		    throw new RuntimeException("For problrm:" + problemUrl + ",Input found but no output found.");
		}
		Element sampleOutputElement = iterator.next();

		String sampleInput = sampleInputElement.html();
		sampleInput = sampleInput.replaceAll(brTagRegex, newline);
		sampleInput = sampleInput.replaceAll("\"", "");

		String sampleOutput = sampleOutputElement.html();
		sampleOutput = sampleOutput.replaceAll(brTagRegex, newline);
		sampleOutput = sampleOutput.replaceAll("\"", "");

		SampleInputOutput sampleInputOutput = new SampleInputOutput(problem, sampleInput, sampleOutput);
		problem.getSampleInputOutputs().add(sampleInputOutput);
	    }

	    StringBuilder explanation = new StringBuilder();
	    Element note = sampleTests.nextElementSibling();

	    if (note != null)
	    {
		for (Element element : note.getElementsByTag("p"))
		{
		    explanation.append(element.text());
		    explanation.append('\n');
		}
		problem.setExplanation(explanation.toString());
	    }

	    try
	    {
		Element sidebar = doc.getElementById("sidebar");
		Elements roundBoxes = sidebar.getElementsByClass("roundbox sidebox");
		Element thirdRoundBox = roundBoxes.get(TAG_INDEX);
		Elements tagBox = thirdRoundBox.getElementsByClass("tag-box");

		for (Element tagElement : tagBox)
		{
		    problem.getTags().add(tagElement.text());
		}

	    }
	    catch (Exception e)
	    {
	    }

	    Session session = null;
	    Transaction transaction = null;
	    try
	    {
		session = HibernateUtil.getSessionFactory().openSession();
		transaction = session.beginTransaction();

		String hql = "FROM Problem p where p.problemUrl = :problem_url";
		Problem oldProblem = (Problem) session.createQuery(hql).setString("problem_url", problemUrl).uniqueResult();

		String task;

		if (oldProblem != null)
		{
		    task = "updated";
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
		Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.INFO, "{0} {1}", new Object[]
		{
		    task, problem.getProblemUrl()
		});
	    }
	    catch (HibernateException e)
	    {
		if (transaction != null)
		{
		    transaction.rollback();
		}

		Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, "Cannot Insert/Update problem into databse: " + problemUrl, e);
	    }
	    finally
	    {
		if (session != null)
		{
		    session.close();
		}
	    }

	    System.err.println();
	    System.err.flush();
	    mysleep();
	}
	catch (NumberFormatException e)
	{
	    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, "Invalid time limit for: " + problemUrl, e);
	}

	catch (RuntimeException e)
	{
	    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, null, e);
	}
    }

    private void mysleep()
    {
	try
	{
	    Thread.sleep((long) (Math.random() * 5));
	}
	catch (InterruptedException ex)
	{
	    Logger.getLogger(CodeForcesCrawler.class.getName()).log(Level.SEVERE, "Error in sleep function", ex);
	}
    }
}
