package com.Netzwerk.de;
/**
 * Die Klasse Model beschreibt eine Klasse zur Datenhaltung von den Attributen:<br>
 * - maxLimit<br>
 * - xmlPath<br>
 * - xsdPath<br>
 * - modusNow<br>
 * - strategy<br>
 * - advancedClient<br>
 * 
 * @author Pascal Sch�fer
 */

public class Model {
	

	
	/**
	 * enth�lt das limit
	 */
	private int maxLimit = 85;

	/**
	 * enth�lt den Pfad zur xml datei
	 */
	private String xmlPath = "TSInfo.xml";
	/**
	 * enth�lt den pfad zur Schema Datei
	 */
	private String xsdPath = "TSInfo.xsd";
	
	/**
	 * enth�lt das Objekt der jetzigen ausgew�hlten Strategie
	 */
	private IStrategy strategy = new Balance(0);
	/**
	 * enth�lt die erlaubnis ob die clients im advanced modus anfragen d�rfen
	 */
	private boolean advancedClient = false;


	/**
	 * gibt den Wert von limit zur�ck
	 * @return limit als int
	 */
	public int getMaxLimit() {
		return maxLimit;
	}

	/**
	 * setzt den limit wert
	 * 
	 * @param psLimit als int
	 */
	public void setMaxLimit(int psLimit) {
		this.maxLimit = psLimit;
	}

	/**
	 * gibt den wert von xml pfad zur�ck
	 * 
	 * @return xmlpath als String
	 */
	public String getXMLPath() {
		return this.xmlPath;
	}

	/**
	 * setzt den wert von xmlpath
	 * 
	 * @param psXMLPath als String
	 */
	public void setXMLPath(String psXMLPath) {
		this.xmlPath = psXMLPath;
	}

	/**
	 * gibt den xsd Pfad zur�ck
	 * 
	 * @return xsdpath als String
	 */
	public String getXSDPath() {
		return this.xsdPath;
	}

	/**
	 * setzt den wert von xsdpath
	 * 
	 * @param psXSDPath als String
	 */
	public void setXSDPath(String psXSDPath) {
		this.xsdPath = psXSDPath;
	}
	
	/**
	 * 
	 * gibt die Strategie zur�ck
	 * 
	 * @return strategy als IStrategy
	 */
	public IStrategy getStrategy() {
		return strategy;
	}

	/**
	 * 
	 * setzt die Strategie
	 * 
	 * @param strategy als IStrategy
	 */
	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * 
	 * gibt den boolean wert zur�ck von advancedClient
	 * 
	 * @return advancedClient als boolean
	 */
	public boolean getAdvancedClient() {
		return advancedClient;
	}

	/**
	 * setzt den boolean wert von advanceClient
	 * 
	 * @param advancedClient als boolean 
	 */
	public void setAdvancedClient(boolean advancedClient) {
		this.advancedClient = advancedClient;
	}
}
