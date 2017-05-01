/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dhaval
 */
public class TutorialInfo
{

    private String name;
    private List<Link> links;

    public TutorialInfo()
    {
	links = new ArrayList<>();
    }

    public TutorialInfo(String name)
    {
	this();
	this.name = name;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
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
