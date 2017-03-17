/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Dhaval
 */
@Entity
@Table(name = "sample_input_output")
@XmlRootElement
public class SampleInputOutput implements Serializable
{

    @Id
    @GeneratedValue
    private int sid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Problem problem;
    private String input;
    private String output;

    public SampleInputOutput(Problem problem, String input, String output)
    {
	this.problem = problem;
	this.input = input;
	this.output = output;
    }

    public SampleInputOutput()
    {
    }

    public String getInput()
    {
	return input;
    }

    public void setInput(String input)
    {
	this.input = input;
    }

    public String getOutput()
    {
	return output;
    }

    public void setOutput(String output)
    {
	this.output = output;
    }

    @Override
    public String toString()
    {
	return "Sample{" + "input=" + getInput() + ", output=" + getOutput() + '}';
    }

    @XmlTransient
    public int getSid()
    {
	return sid;
    }

    public void setSid(int sid)
    {
	this.sid = sid;
    }

    @XmlTransient
    public Problem getProblem()
    {
	return problem;
    }

    public void setProblem(Problem problem)
    {
	this.problem = problem;
    }

}
