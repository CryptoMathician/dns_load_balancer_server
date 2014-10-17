package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * This class describe a algorithm to chose a server
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class Balance extends Object implements IStrategy 
{
	/**
	 * Store the type of the server
	 */
	private int type;
	
	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger( Balance.class);
	
	/**
	 * Random object
	 */
	private Random random = new Random();
	
	/**
	 * This object stores the strategy algorithm 
	 */
	private String info = "balance";
	
	/**
	 * public constructor of this class
	 * @param piTyp as int
	 */
	public Balance(int piTyp)
	{
		this.type = piTyp;
	}
	
	/**
	 * Sort the list and chose a server with a low usage rate and return a IPv4 address
	 * 
	 * @param table as Hashtable sort by the type
	 * @return the sIP as String
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
		String sTyp = String.valueOf(this.type);

		if(type < 0)
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
				logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP() + " ;Auslastung: " + akt.getUsageRate() + " ;Typ: " + akt.getType() + " ;Name: " + akt.getName());
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
				logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP() + " ;Auslastung: " + akt.getUsageRate() + " ;Typ: " + akt.getType() + " ;Name: " + akt.getName());
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
		logger.debug("Objekt welches ausgewaehlt wurde..." + sIP);

		logger.info("Ende der Methode Auswerten.balance");
		logger.info("");
		return sIP;
	}

	/**
	 * return the name of the strategy algorithm
	 * 
	 * @return info as String
	 */
	@Override
	public String getInfo() 
	{
		return this.info;
	}
	
	/**
	 * return the type
	 * 
	 * @return type as int
	 */
	@Override
	public int getTyp() 
	{
		return this.type;
	}
}
