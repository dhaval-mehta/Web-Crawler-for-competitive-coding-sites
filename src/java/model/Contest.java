/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.net.URL;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dhaval
 */
@XmlRootElement
public class Contest
{

    private String name;
    private URL contestUrl;
    private Platform platform;
    private Date startDate;

    public Contest()
    {
    }

    public Contest(String name, URL contestUrl, Platform platform, Date startDate)
    {
	this.name = name;
	this.contestUrl = contestUrl;
	this.platform = platform;
	this.startDate = startDate;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public Date getStartDate()
    {
	return startDate;
    }

    public void setStartDate(Date startDate)
    {
	this.startDate = startDate;
    }

    public URL getContestUrl()
    {
	return contestUrl;
    }

    public void setContestUrl(URL contestUrl)
    {
	this.contestUrl = contestUrl;
    }

    @Override
    public String toString()
    {
	return "Contest{" + "name=" + getName() + ", contestUrl=" + getContestUrl() + ", platform=" + getPlatform() + ", startDate=" + getStartDate() + '}';
    }

    public Platform getPlatform()
    {
	return platform;
    }

    public void setPlatform(Platform platform)
    {
	this.platform = platform;
    }

}
