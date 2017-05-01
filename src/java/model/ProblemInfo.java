/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dhaval
 */
@XmlRootElement
public class ProblemInfo
{

    private int id;
    private String title;
    private Platform platform;
    private String problemUrl;
    private List<Link> links;

    public ProblemInfo()
    {
	links = new ArrayList<>();
    }

    public ProblemInfo(int id, String title, Platform platform, String problemUrl)
    {
	this();
	this.id = id;
	this.title = title;
	this.platform = platform;
	this.problemUrl = problemUrl;
    }

    public String getProblemUrl()
    {
	return problemUrl;
    }

    public void setProblemUrl(String problemUrl)
    {
	this.problemUrl = problemUrl;
    }

    public String getTitle()
    {
	return title;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public int getId()
    {
	return id;
    }

    public void setId(int id)
    {
	this.id = id;
    }

    public Platform getPlatform()
    {
	return platform;
    }

    public void setPlatform(Platform platform)
    {
	this.platform = platform;
    }

    public void addLink(Link link)
    {
	links.add(link);
    }

    public List<Link> getLinks()
    {
	return links;
    }

    public void setLinks(List<Link> links)
    {
	this.links = links;
    }
}
