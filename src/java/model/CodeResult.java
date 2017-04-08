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
public class CodeResult
{

    private String standradOutput;
    private String standradError;
    private String compilerMessage;
    private int exitStatus;
    private double executionTime;

    public String getStandradOutput()
    {
	return standradOutput;
    }

    public void setStandradOutput(String standradOutput)
    {
	this.standradOutput = standradOutput;
    }

    public String getStandradError()
    {
	return standradError;
    }

    public void setStandradError(String standradError)
    {
	this.standradError = standradError;
    }

    public String getCompilerMessage()
    {
	return compilerMessage;
    }

    public void setCompilerMessage(String compilerMessage)
    {
	this.compilerMessage = compilerMessage;
    }

    public int getExitStatus()
    {
	return exitStatus;
    }

    public void setExitStatus(int exitStatus)
    {
	this.exitStatus = exitStatus;
    }

    public double getExecutionTime()
    {
	return executionTime;
    }

    public void setExecutionTime(double executionTime)
    {
	this.executionTime = executionTime;
    }
}
