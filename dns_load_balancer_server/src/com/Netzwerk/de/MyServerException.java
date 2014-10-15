package com.Netzwerk.de;



/**
 * 
 * Diese Exception Klasse beschreibt eine Exception wenn opensockets weniger als 0 werden
 * 
 * @author Pascal Sch�fer
 *
 */

@SuppressWarnings("serial")
public class MyServerException extends Exception {

	/**
	 * �berladener Konstruktor mit m�glichkeit der �bergabe einer Nachricht
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
