/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Dhaval
 */
public class InputCode
{

    private String id;
    private String code;
    private String compilerOptions;
    private double timeLimit;
    private String language;
    private String compilerName;

    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public String getCode()
    {
	return code;
    }

    public void setCode(String code)
    {
	this.code = code;
    }

    public String getCompilerOptions()
    {
	return compilerOptions;
    }

    public void setCompilerOptions(String compilerOptions)
    {
	this.compilerOptions = compilerOptions;
    }

    public double getTimeLimit()
    {
	return timeLimit;
    }

    public void setTimeLimit(double timeLimit)
    {
	this.timeLimit = timeLimit;
    }

    public String getLanguage()
    {
	return language;
    }

    public void setLanguage(String language)
    {
	this.language = language;
    }

    public String getCompilerName()
    {
	return compilerName;
    }

    public void setCompilerName(String compilerName)
    {
	this.compilerName = compilerName;
    }
}
