package com.Netzwerk.de;

/**
 * This class describe a exceptopn if the opensockets is under 0
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

@SuppressWarnings("serial")
public class MyServerException extends Exception
{

	/**
	 * Overloaded constructor
	 * 
	 * @param psMessage as String
	 */
	public MyServerException(String psMessage)
	{
		super(psMessage);
	}

	/**
	 * Standard constructor of this class
	 */
	public MyServerException()
	{

	}

}
