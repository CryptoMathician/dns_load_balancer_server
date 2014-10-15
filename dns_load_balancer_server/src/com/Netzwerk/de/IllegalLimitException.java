package com.Netzwerk.de;


/**
 * 
 * Diese Exception Klasse beschreibt eine Exception f�r ein falsches Limit
 * 
 * @author Pascal Sch�fer
 *
 */
@SuppressWarnings("serial")
public class IllegalLimitException extends Exception {



	
	/**
	 * Konstruktor der Exception und gibt den �bergebenen Wert an die Klasse wetier von der sie Erbt
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
