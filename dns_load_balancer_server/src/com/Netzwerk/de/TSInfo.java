package com.Netzwerk.de;

import org.apache.log4j.Logger;

/**
 * Beschreibung eines Terminalservers mit der Auslastung, dem Namen, dem Typ und
 * der IP
 *
 * @version 1.0 vom 17.01.2013
 * @author Pascal Schäfer
 */

public class TSInfo implements Comparable<TSInfo> 
{
	// Anfang Attribute
	/**
	 * Beschreibt die Auslastung des Servers als Int
	 */
	private int auslastung;
	
	/**
	 * Beschreibt den Typ des Server als Int
	 */
	private int typ;
	
	/**
	 * Beschreibt die IP-Adresse des Servers als String
	 */
	private String ip;
	
	/**
	 * Beschreibt den Namen des Servers als String
	 */
	private String name;
	
	/**
	 * Logger Objekt zum mit loggen
	 */
	private static Logger logger = Logger.getLogger(TSInfo.class);

	// Ende Attribute

	/**
	 * Dem Konstruktor werden die Paramter übergeben um diese in die
	 * Klassenattribute hinein zu schreiben
	 * 
	 * @param piAuslastung
	 *            gibt die Auslastung als Int an
	 * @param piTyp
	 *            gibt den Typ als Int an
	 * @param psIP
	 *            gibt die IP als String an
	 * @param psName
	 *            gibt den Namen als String an
	 */
	public TSInfo(int piAuslastung, int piTyp, String psIP, String psName) 
	{
		logger.info("Anfang Konstruktor TSInfo");
		this.auslastung = piAuslastung;
		this.typ = piTyp;
		this.ip = psIP;
		this.name = psName;
		logger.info("Ende des Konstruktors TSInfo");
	}

	/**
	 * gibt die Auslastung als Int zurück
	 * 
	 * @return Auslastung als Int
	 */
	// Anfang Methoden
	public int getAuslastung() 
	{
		return auslastung;
	}

	/**
	 * gibt den Typ als Int zurück
	 * 
	 * @return Typ als int
	 */
	public int getTyp() 
	{
		return typ;
	}

	/**
	 * gibt die IP als String zurück
	 * 
	 * @return IP als String
	 */
	public String getIP() 
	{
		return ip;
	}

	/**
	 * gibt den Namen als String zurück
	 * 
	 * @return Name als String
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Überschriebene ComapreTo Methode zum Sortieren der TSInfo Objekte
	 */
	@Override
	public int compareTo(TSInfo poTSInfo) 
	{
		return this.auslastung - poTSInfo.getAuslastung();
	}

	// Ende Methoden
} // end of TSInfo