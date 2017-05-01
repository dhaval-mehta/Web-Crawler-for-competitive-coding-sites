/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dhaval
 */
@XmlRootElement
public class Link
{

    private String href;
    private String rel;

    public Link()
    {
    }

    public Link(String href, String rel)
    {
	this.href = href;
	this.rel = rel;
    }

    public String getHref()
    {
	return href;
    }

    public void setHref(String href)
    {
	this.href = href;
    }

    public String getRel()
    {
	return rel;
    }

    public void setRel(String rel)
    {
	this.rel = rel;
    }

}
