package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * This class describe the save strategy algorithm
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class Save extends Object implements IStrategy
{
	/**
	 * Stores the type
	 */
	private int type;

	/**
	 * Stores the max limit
	 */
	private int maxLimit;

	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger(Save.class);

	/**
	 * Random object
	 */
	private Random random = new Random();

	/**
	 * Stores the name of the strategy algorithm
	 */
	private String info = "save";

	/**
	 * Public constructor of this class
	 * 
	 * @param piTyp as int
	 * @param piMaxLimit as int
	 */
	public Save(int piTyp, int piMaxLimit)
	{
		this.type = piTyp;
		this.maxLimit = piMaxLimit;
	}

	/**
	 * This method chose a server with the save algorithm and return a IPv4 address
	 * 
	 * @param table as Hashtable
	 * @return sIP as String
	 */
	@Override
	public String chooseServer(Hashtable<String, ArrayList<TSInfo>> table)
	{
		logger.info("Anfang der Methode Auswerten.save");
		String sIP = "";
		int iZufallszahl = 0;
		TSInfo tsInfoObjekt;
		String sTyp = String.valueOf(type);
		double groesse = 0.0;
		double groesse100 = 0.0;
		int s20prozent = 0;
		ArrayList<TSInfo> StromSparListeTSInfo = new ArrayList<TSInfo>();

		if (type < 0)
		{
			return ("Typ must be in the positive range");
		}
		if (!(table.containsKey(sTyp)))
		{
			return ("The server of " + sTyp + " is unavailable");
		}

		logger.debug("Liste wurde sortiert");

		Collections.sort(table.get(sTyp));

		for (TSInfo akt : table.get(sTyp))
		{
			if (akt.getUsageRate() < maxLimit)
			{
				logger.debug("Ein Server wird in die neue Liste hinzugefuegt");
				StromSparListeTSInfo.add(new TSInfo(akt.getUsageRate(), akt
						.getType(), akt.getIP(), akt.getName()));
			}
		}

		// hier nimmt er die untersten 3 weil keine server in der neuen liste da
		// sind
		if (StromSparListeTSInfo.size() == 0)
		{
			logger.debug("kein Server in der neuen Liste");

			iZufallszahl = random.nextInt(3);
			tsInfoObjekt = table.get(sTyp).get(
					(table.get(sTyp).size() - 1) - iZufallszahl);// nochmal
																	// nachgucken
			sIP = tsInfoObjekt.getIP();
		}
		else
		{
			// wenn weniger als 3 server drin sind nimmt er einen zufälligen von
			// den dreien
			if (StromSparListeTSInfo.size() < 3)
			{
				logger.warn("kein Loadbalancing stattgefunden!");

				Collections.sort(StromSparListeTSInfo);

				iZufallszahl = random.nextInt(StromSparListeTSInfo.size());

				logger.debug("zufallszahl bei weniger als 3 Objekten: "
						+ iZufallszahl);

				tsInfoObjekt = StromSparListeTSInfo.get(iZufallszahl);
				sIP = tsInfoObjekt.getIP();
			}
			else
			{
				Collections.sort(StromSparListeTSInfo);
				logger.debug("Neue Liste wurde sortiert:");
				logger.debug("\n");

				int i = 1;
				for (TSInfo akt : StromSparListeTSInfo)
				{
					logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP()
							+ " ;Auslastung: " + akt.getUsageRate()
							+ " ;Typ: " + akt.getType() + " ;Name: "
							+ akt.getName());
					i++;
				}

				groesse = StromSparListeTSInfo.size() * 1.0;
				groesse100 = groesse * 0.20;
				s20prozent = (int) Math.round(groesse100);

				if (s20prozent < 3) s20prozent = 3;

				iZufallszahl = random.nextInt(s20prozent);
				logger.debug("zufallszahl bei mehr als 3 Objekten: "
						+ iZufallszahl);

				tsInfoObjekt = StromSparListeTSInfo.get((StromSparListeTSInfo
						.size() - 1) - iZufallszahl);
				sIP = tsInfoObjekt.getIP();
			}
		}
		logger.debug("\n\n");
		logger.debug("Objekt welches ausgewaehlt wurde..." + sIP);
		logger.info("Ende der Mehtode Auswerten.save");
		logger.info("");
		return sIP;
	}

	/**
	 * Return the name of the strategy algorithm
	 * 
	 * @return info as String
	 */
	@Override
	public String getInfo()
	{
		return this.info;
	}

	/**
	 * Return the type of the server
	 * 
	 * @return typ as int
	 */
	@Override
	public int getTyp()
	{
		return this.type;
	}

}
