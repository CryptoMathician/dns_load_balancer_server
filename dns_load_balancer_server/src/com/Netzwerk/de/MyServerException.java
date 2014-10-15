package com.Netzwerk.de;



/**
 * 
 * Diese Exception Klasse beschreibt eine Exception wenn opensockets weniger als 0 werden
 * 
 * @author Pascal Schäfer
 *
 */

@SuppressWarnings("serial")
public class MyServerException extends Exception {

	/**
	 * Überladener Konstruktor mit möglichkeit der Übergabe einer Nachricht
	 * @param psMessage als String
	 */
	
	public MyServerException(String psMessage)
	{
		super(psMessage);
	}
	
	/**
	 * Standard Konstruktor der Exception
	 */
	public MyServerException()
	{
		
	}
	
}
