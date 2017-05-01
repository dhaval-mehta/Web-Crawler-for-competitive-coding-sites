/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dhaval
 */
@Entity
@Table(name = "problem")
@XmlRootElement
public class Problem implements Serializable
{

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "problem_url", unique = true)
    private String problemUrl;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private Platform platform;

    @Column(name = "problem_statement")
    private String problemStatement;

    @Column(name = "input_format")
    private String inputFormat;

    @Column(name = "output_format")
    private String outputFormat;

    @Column(name = "problem_constraints")
    private String constraints;

    @Column(name = "time_limit")
    private double timeLimit;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "problem")
    private List<SampleInputOutput> sampleInputOutputs;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "tag_name")
    private List<String> tags;

    @Column(name = "explanation")
    private String explanation;

    @Transient
    private List<Link> links;

    public Problem(int id, String problemUrl, String problemStatement, String inputFormat, String outputFormat, String constraints, double timeLimit, List<SampleInputOutput> sampleInputOutputs, List<String> tags, String explanation)
    {
	this();
	this.id = id;
	this.problemUrl = problemUrl;
	this.problemStatement = problemStatement;
	this.inputFormat = inputFormat;
	this.outputFormat = outputFormat;
	this.constraints = constraints;
	this.timeLimit = timeLimit;
	this.sampleInputOutputs = sampleInputOutputs;
	this.tags = tags;
	this.explanation = explanation;
    }

    public Problem()
    {
	links = new ArrayList<>();
	tags = new ArrayList<>();
	sampleInputOutputs = new ArrayList<>();
    }

    public String getInputFormat()
    {
	return inputFormat;
    }

    public void setInputFormat(String inputFormat)
    {
	this.inputFormat = inputFormat;
    }

    public String getOutputFormat()
    {
	return outputFormat;
    }

    public void setOutputFormat(String outputFormat)
    {
	this.outputFormat = outputFormat;
    }

    public String getConstraints()
    {
	return constraints;
    }

    public void setConstraints(String constraints)
    {
	this.constraints = constraints;
    }

    public double getTimeLimit()
    {
	return timeLimit;
    }

    public void setTimeLimit(double timeLimit)
    {
	this.timeLimit = timeLimit;
    }

    public List<String> getTags()
    {
	return tags;
    }

    public void setTags(List<String> tags)
    {
	this.tags = tags;
    }

    public String getProblemStatement()
    {
	return problemStatement;
    }

    public void setProblemStatement(String problemStatement)
    {
	this.problemStatement = problemStatement;
    }

    public String getProblemUrl()
    {
	return problemUrl;
    }

    public void setProblemUrl(String problemUrl)
    {
	this.problemUrl = problemUrl;
    }

    public String getExplanation()
    {
	return explanation;
    }

    public void setExplanation(String explanation)
    {
	this.explanation = explanation;
    }

    public List<SampleInputOutput> getSampleInputOutputs()
    {
	return sampleInputOutputs;
    }

    public void setSampleInputOutputs(List<SampleInputOutput> sampleInputOutputs)
    {
	this.sampleInputOutputs = sampleInputOutputs;
    }

    public int getId()
    {
	return id;
    }

    public void setId(int id)
    {
	this.id = id;
    }

    public String getTitle()
    {
	return title;
    }

    public void setTitle(String title)
    {
	this.title = title;
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

    @Override
    public String toString()
    {
	StringBuilder sb = new StringBuilder();

	sb.append("Id: ");
	sb.append(getId());
	sb.append("\n");
	sb.append("Problem Statement: ");
	sb.append(getProblemStatement());
	sb.append("\n");
	sb.append("InputFormat: ");
	sb.append(getInputFormat());
	sb.append("\n");
	sb.append("OutputFormat: ");
	sb.append(getOutputFormat());
	sb.append("\n");
	sb.append("constraints: ");
	sb.append(getConstraints());
	sb.append("\n");
	sb.append("timeLimit: ");
	sb.append(getTimeLimit());
	sb.append("\n");
	sb.append("sampleInputOutputs: ");
	sb.append(getSampleInputOutputs());
	sb.append("\n");
	sb.append("tags: ");
	sb.append(getTags());
	sb.append("\n");
	sb.append("explanation: ");
	sb.append("\n");
	sb.append(getExplanation());

	return sb.toString();
    }
}
