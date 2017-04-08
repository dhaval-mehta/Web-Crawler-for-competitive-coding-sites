/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dhaval
 */
@Entity
@Table(name = "tutorial")
@XmlRootElement
public class Tutorial implements Serializable
{

    @Id
    private String name;

    private String content;

    @Override
    public String toString()
    {
	return "Tutorial{" + "name=" + getName() + ", content=" + getContent() + '}';
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public String getContent()
    {
	return content;
    }

    public void setContent(String content)
    {
	this.content = content;
    }
}
