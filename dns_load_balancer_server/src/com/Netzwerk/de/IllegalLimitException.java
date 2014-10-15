package com.Netzwerk.de;


/**
 * 
 * Diese Exception Klasse beschreibt eine Exception für ein falsches Limit
 * 
 * @author Pascal Schäfer
 *
 */
@SuppressWarnings("serial")
public class IllegalLimitException extends Exception 
{
	/**
	 * Konstruktor der Exception und gibt den übergebenen Wert an die Klasse wetier von der sie Erbt
	 * 
	 * @param psMessage als String
	 */
	public IllegalLimitException(String psMessage)
	{
		super(psMessage);
	}
	
	/**
	 * Standard Konstruktor der Exception
	 */
	public IllegalLimitException()
	{
		
	}

}
