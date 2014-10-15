package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * Die Klasse Save beschreibt den Algorithmus zum Ausw�hlen des Servers
 * @author Pascal Sch�fer
 *
 */

public class Save implements IStrategy 
{
	/**
	 * Enth�lt den Typ welcher ausgew�hlt wird
	 */
	private int typ;
	/**
	 * Enth�lt das Maximum Limit
	 */
	private int maxLimit;
	/**
	 * Objekt eines Loggers zum Loggen von informationen
	 */
	private static Logger logger = Logger.getLogger( Save.class);
	/**
	 * Ein Objekt der Klasse Random erstellen
	 */
	private Random random = new Random();
	/**
	 * Enth�lt die Strategie als String
	 */
	private String info = "save";

	/**
	 * Konstruktor der Klasse Save setzt die Attribute auf die �bergebenen Werte
	 * @param piTyp als int
	 * @param piMaxLimit als int
	 */
	public Save(int piTyp,int piMaxLimit)
	{
		this.typ = piTyp;
		this.maxLimit = piMaxLimit;
	}
	
	/**
	 * erstellt eine neue Liste aus der alten Liste 
	 * und sucht einen Server mit hoher Auslastung aus der unter dem piMaxLimit liegt 
	 * und gibt eine IP-Adresse als String zur�ck.
	 * 
	 * @param table enth�lt die Server geordnet nach deren Typ
	 * @return gibt eine IP-Adresse als String Objekt zur�ck
	 */
	
	@Override
	public String chooseServer(Hashtable<String, ArrayList<TSInfo>> table) 
	{
		logger.info("Anfang der Methode Auswerten.save");
		String sIP = "";
		int iZufallszahl = 0;
		TSInfo tsInfoObjekt;
		String sTyp = String.valueOf(typ);
		double groesse = 0.0;
		double groesse100 = 0.0;
		int s20prozent = 0;
		ArrayList<TSInfo> StromSparListeTSInfo = new ArrayList<TSInfo>();

		if(typ < 0)
		{
			return ("Typ must be in the positive range");
		}
		if(!(table.containsKey(sTyp)))
		{
			return ("The server of " + sTyp + " is unavailable");
		}

		logger.debug("Liste wurde sortiert");

		Collections.sort(table.get(sTyp));

		for(TSInfo akt : table.get(sTyp))
		{
			if(akt.getAuslastung() < maxLimit)
			{
				logger.debug("Ein Server wird in die neue Liste hinzugefuegt");
				StromSparListeTSInfo.add(new TSInfo(akt.getAuslastung(),akt.getTyp(),akt.getIP(),akt.getName()));
			}
		}


		// hier nimmt er die untersten 3 weil keine server in der neuen liste da sind
		if(StromSparListeTSInfo.size() == 0)
		{
			logger.debug("kein Server in der neuen Liste");

			iZufallszahl = random.nextInt(3);
			tsInfoObjekt = table.get(sTyp).get((table.get(sTyp).size()-1)-iZufallszahl);// nochmal nachgucken
			sIP = tsInfoObjekt.getIP();
		}
		else
		{
			// wenn weniger als 3 server drin sind nimmt er einen zuf�lligen von den dreien
			if(StromSparListeTSInfo.size() < 3)
			{	
				logger.warn("kein Loadbalancing stattgefunden!");

				Collections.sort(StromSparListeTSInfo);

				iZufallszahl = random.nextInt(StromSparListeTSInfo.size());

				logger.debug("zufallszahl bei weniger als 3 Objekten: " + iZufallszahl);

				tsInfoObjekt = StromSparListeTSInfo.get(iZufallszahl);
				sIP = tsInfoObjekt.getIP();
			}
			else
			{
				Collections.sort(StromSparListeTSInfo);
				logger.debug("Neue Liste wurde sortiert:");
				logger.debug("\n");

				int i = 1;
				for(TSInfo akt : StromSparListeTSInfo)
				{
					logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP() + " ;Auslastung: " + akt.getAuslastung() + " ;Typ: " + akt.getTyp() + " ;Name: " + akt.getName());
					i++;
				}

				groesse = StromSparListeTSInfo.size()*1.0;
				groesse100 = groesse * 0.20;
				s20prozent = (int) Math.round(groesse100);

				if(s20prozent < 3) s20prozent = 3;

				iZufallszahl = random.nextInt(s20prozent);
				logger.debug("zufallszahl bei mehr als 3 Objekten: " + iZufallszahl);

				tsInfoObjekt = StromSparListeTSInfo.get((StromSparListeTSInfo.size()-1)-iZufallszahl);
				sIP = tsInfoObjekt.getIP();
			}
		}
		logger.debug("\n\n");
		logger.debug("Objekt welches ausgew�hlt wurde..." + sIP);
		logger.info("Ende der Mehtode Auswerten.save");
		logger.info("");
		return sIP;
	}

	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public int getTyp() {
		// TODO Auto-generated method stub
		return this.typ;
	}

}
