package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Die Schnittstelle Strategy beschreibt eine Methode 
 * die von Klassen benutzt werden kann um Neue Auswahlstrategien zu Implementieren
 * 
 * @author Pascal Sch�fer
 *
 */

public interface IStrategy 
{
	/**
	 * Diese Methode ist eine Abstrakte methode die von den Klassen Implementiert und
	 * �berschrieben werden muss
	 * 
	 * @param table als Hashtable
	 * @return String
	 */
	public String chooseServer(Hashtable<String,ArrayList<TSInfo>> table);
	
	public String getInfo();
	
	public int getTyp();
}
