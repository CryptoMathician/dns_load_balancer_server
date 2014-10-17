package com.Netzwerk.de;

/**
 * This exception class describe the limit exception
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public class IllegalLimitException extends Exception
{
	/**
	 * Overloaded public constructor of this class
	 * 
	 * @param psMessage as String
	 */
	public IllegalLimitException(String psMessage)
	{
		super(psMessage);
	}

	/**
	 * Standard public constructor of this class
	 */
	public IllegalLimitException()
	{

	}

}
