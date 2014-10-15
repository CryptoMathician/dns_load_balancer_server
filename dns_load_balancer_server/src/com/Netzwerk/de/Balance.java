package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * Die Klasse Balance beschreibt den Algorithmus zum Auswählen des Servers
 * 
 * @author Pascal Schäfer
 *
 */

public class Balance implements IStrategy 
{
	/**
	 * Enthält den Typ welcher ausgewählt wird
	 */
	private int typ;
	/**
	 * Objekt eines Loggers zum Loggen von informationen
	 */
	private static Logger logger = Logger.getLogger( Balance.class);
	/**
	 * Ein Objekt der Klasse Random erstellen
	 */
	private Random random = new Random();
	/**
	 * enthält die Strategie als String
	 */
	private String info = "balance";
	
	/**
	 * Konstruktor der Klasse Balance setzt die Attribute auf die Übergebenen Werte
	 * @param piTyp als int
	 */
	public Balance(int piTyp)
	{
		this.typ = piTyp;
	}
	
	/**
	 * Sortiert die Liste die gebraucht wird und sucht einen Server mit niedriger Auslastung aus und gibt eine IP-Adresse als String zurück
	 * 
	 * @param table enthält die Server geordnet nach deren Typ
	 * @return gibt eine IP-Adresse als String Objekt zurück
	 */
	@Override
	public String chooseServer(Hashtable<String,ArrayList<TSInfo>> table) 
	{
		logger.info("Anfang der Methode Auswerten.balance");
		
		int zufallszahl = 0;
		double groesse = 0.0;
		double endgroesse = 0.0;
		int s20prozent = 0;
		String sIP = "";
		TSInfo tsInfoObjekt;
		String sTyp = String.valueOf(this.typ);

		if(typ < 0)
		{
			logger.debug("Typ must be in the positive range");
			return ("Typ must be in the positive range");
		}
		if(!(table.containsKey(sTyp)))
		{
			logger.debug("The server of " + sTyp + " is unavailable");
			return ("The server of " + sTyp + " is unavailable");
		}

		logger.debug("Groesse der Tabelle " + sTyp + " : " + table.get(sTyp).size());

		if(table.get(sTyp).size() < 3)
		{
			logger.info("Liste vom Typ " + sTyp + " hat weniger als 3 Server");
			Collections.sort(table.get(sTyp));

			int iAnzahl = 1;
			int i = 1;
			for(TSInfo akt : table.get(sTyp))
			{
				logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP() + " ;Auslastung: " + akt.getAuslastung() + " ;Typ: " + akt.getTyp() + " ;Name: " + akt.getName());
				i++;
				iAnzahl++;
			}

			logger.debug("\n\n");
			logger.debug("Anzahl der TSInfo Objekte: " + iAnzahl);

			zufallszahl = random.nextInt(table.get(sTyp).size());

			logger.debug("Zufallszahl ist " + zufallszahl);
			tsInfoObjekt = table.get(sTyp).get(zufallszahl);
			sIP = tsInfoObjekt.getIP();
		}
		else
		{
			logger.info("Liste vom Typ " + sTyp + " hat mehr als 2 Server");

			logger.debug("\n\n\n\n");
			logger.debug("jetzt wird ausbalanciert sortiert!!\n\n\n\n");


			Collections.sort(table.get(sTyp));


			logger.debug("Sortierte Liste: ");



			int iAnzahl = 1;
			int i = 1;
			for(TSInfo akt : table.get(sTyp))
			{
				logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP() + " ;Auslastung: " + akt.getAuslastung() + " ;Typ: " + akt.getTyp() + " ;Name: " + akt.getName());
				i++;
				iAnzahl++;
			}


			logger.debug("\n\n");
			logger.debug("Anzahl der TSInfo Objekte: " + iAnzahl);

			groesse = table.get(sTyp).size()*1.0;
			endgroesse = groesse*0.20;
			s20prozent = (int) Math.round(endgroesse);

			logger.debug("\n\n");
			logger.debug("20 Prozent der Liste ist: " + s20prozent);

			if(s20prozent < 3) s20prozent = 3;

			zufallszahl = random.nextInt(s20prozent);


			logger.debug("die zufallszahl ist " + zufallszahl);
			tsInfoObjekt = table.get(sTyp).get(zufallszahl);
			sIP = tsInfoObjekt.getIP();
		}
		logger.debug("\n\n");
		logger.debug("Objekt welches ausgewählt wurde..." + sIP);

		logger.info("Ende der Methode Auswerten.balance");
		logger.info("");
		return sIP;
	}

	/**
	 * 
	 * gibt die Strategie als String zurück
	 * 
	 * @return info als String
	 */
	@Override
	public String getInfo() {
		return this.info;
	}
	/**
	 * 
	 * gibt den Typ der ausgewählt wird zurück
	 * 
	 * @return typ als int
	 */
	@Override
	public int getTyp() {
		return this.typ;
	}
}
