package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Die Schnittstelle Strategy beschreibt eine Methode 
 * die von Klassen benutzt werden kann um Neue Auswahlstrategien zu Implementieren
 * 
 * @author Pascal Schäfer
 *
 */

public interface IStrategy 
{
	/**
	 * Diese Methode ist eine Abstrakte methode die von den Klassen Implementiert und
	 * überschrieben werden muss
	 * 
	 * @param table als Hashtable
	 * @return String
	 */
	public String chooseServer(Hashtable<String,ArrayList<TSInfo>> table);
	
	/**
	 * Gibt den Namen des Algorithmus zurück
	 * 
	 * @return den Namen des Algorithmus zurück
	 */
	public String getInfo();
	
	/**
	 * Gibt den Typ des Servers an nach dem Sortiert wird
	 * 
	 * @return den Typ des Servers als Integer
	 */
	public int getTyp();
}
