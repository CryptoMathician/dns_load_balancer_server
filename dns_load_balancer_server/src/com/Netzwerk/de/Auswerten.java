package com.Netzwerk.de;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Eine Klasse die das auswerten einer XML Datei beschreibt. 
 * So wie das Sortieren und Informationen raus geben von Server Objekten die in einer XML Datei stehen
 *
 * @version 1.0
 * @author Pascal Schäfer
 */

public class Auswerten 
{
	
	/**
	 * Objekt einer Auswahlstrategie zum Auswählen eines Servers
	 */
	
	private IStrategy strategy = null;
	
	/**
	 * Eine Tabelle worin die Listen nach Typ gespeichert werden
	 */
	private Hashtable<String,ArrayList<TSInfo>> table = new Hashtable<String,ArrayList<TSInfo>>();
	/**
	 * Document für die XML Datei
	 */
	private Document doc = null;
	/**
	 * Objekt eines Loggers zum Loggen von informationen
	 */
	private static Logger logger = Logger.getLogger( Auswerten.class);

	/**
	 * Standard Konstruktor der Klasse
	 */
	public Auswerten() 
	{
		logger.info("Anfang / Ende des Konstruktors Auswerten");
	}

	
	/**
	 * Liest ein XML Datei ein und überprüft es mit einer XML Schema datei auf richtigkeit<br>
	 * und sortiert die TSInfo Objekte in eine Liste. Die Listen sind je nach Typ geordnet.<br>
	 * Und loggt mit wenn es weniger als 3 Server von einem gewissen Typ gibt
	 * 
	 * @param psXMLPath dort steht der Pfad zur XML Datei drin
	 * @param psXSDPath dort steht der Pfad zur XSD Datei drin
	 */
	public void readXMLFileWithXSD(String psXMLPath, String psXSDPath) {
		logger.info("Beginn der Methode dateiAuslesen");

		try {
			logger.debug("Versuche XSD zu Laden");
			//Guck auf die factory für die W3C XML Schema Sprache
			SchemaFactory schemafactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			// 2. Kompiliere das Schema. 
			// Das Schema wird geladen von dem java.io.File
			File schemaLocation = new File(psXSDPath);
			Schema schema = schemafactory.newSchema(schemaLocation);

			//bekomm einen validator von dem Schema
			Validator validator = schema.newValidator();
			
			// Mit DocumentBuilderFactory und DocumentBuilder ein XML Document einlesen
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			// für die Validierung von der XML durch die XSD
			DocumentBuilder builder  = factory.newDocumentBuilder();
			doc = builder.parse( new File( psXMLPath ) );

			DOMSource source = new DOMSource(doc);
			DOMResult result = new DOMResult();

			// prüft die richtigkeit der Datei
			validator.validate(source,result);

			logger.debug("");
			logger.debug("Validation war erfolgreich");
			
			// Root Knoten 
			Node rootNode = doc.getDocumentElement();

			logger.debug("Root Knoten: " + rootNode.getNodeName());
			logger.debug("");
			logger.debug("\nhat Kinder Knoten: " + rootNode.hasChildNodes());


			// in children stehen die unter knoten von dem root knoten
			NodeList children = rootNode.getChildNodes();

			// mit der schleife die knoten von children durchlaufen 
			for(int i = 0; i < children.getLength();i++)
			{
				// in der nnm werden die Attribute gespeichert
				NamedNodeMap nnm = children.item(i).getAttributes();
				if(nnm!=null)
				{
					// eine Knoten Liste erstelen von den unter Knoten des root knotens
					NodeList childrensChildren = children.item(i).getChildNodes();

					String sIp = childrensChildren.item(1).getTextContent();
					String sTyp = childrensChildren.item(3).getTextContent();
					int iTyp = Integer.parseInt(sTyp);
					String sAuslastung = childrensChildren.item(5).getTextContent();
					int iAuslastung = Integer.parseInt(sAuslastung);

					String sName = childrensChildren.item(7).getTextContent();

					
					if(iTyp >= 0)
					{
						if(table.containsKey(sTyp))
						{
							table.get(sTyp).add(new TSInfo(iAuslastung,iTyp,sIp,sName));
						}
						else
						{
							table.put(sTyp, new ArrayList<TSInfo>());
							table.get(sTyp).add(new TSInfo(iAuslastung,iTyp,sIp,sName));
						}
					}
					else
					{
						logger.warn("Typ must be in the positive range Server: ");
						logger.warn("Server: ;Typ: " +  sTyp + "; IP: " + sIp + "; Auslastung: " + sAuslastung + "; Name: " + sName);
					}
				}
			}
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		logger.debug("Listen der Server nach dessen Typ sortiert");

		int i = 1;
		int iAnzahl = 0;
		for (String elem : table.keySet()) 
		{
			ArrayList<TSInfo> liste = table.get(elem); 
			logger.debug("\n\n");
			logger.debug("Typ : " + elem + "\n\n");
			i = 1;
			if(table.get(elem).size() < 3)
			{
				logger.warn("Weniger als 3 Server vom Typ " + elem);
			}
			for(TSInfo akt : liste)
			{
				logger.debug("TSInfo Objekt " + i + ": IP:" + akt.getIP() + " ;Auslastung: " + akt.getAuslastung() + " ;Typ: " + akt.getTyp() + " ;Name: " + akt.getName());
				i++;
				iAnzahl++;
			}
		}
		

		logger.info("\n\n");
		logger.info("Anzahl der TSInfo Objekte: " + iAnzahl);

		logger.info("Ende der Methode dateiAuslesen");
		logger.info("");
	}
	
	/**
	 * Liest die verfügbaren Typen aus und gibt diese als String Array zurück
	 * 
	 * @return gibt einen String Array zurück mit den Typen von Servern die vorhanden sind
	 */
	
	public String[] getKeys()
	{
		logger.info("Anfang der Methode Auswerten.getKeys");
		logger.debug("Groesse der Tabelle: " + table.size());
		logger.debug("Keyset der Tabelle: " + table.keySet());
		String[] tmp = null;
		if(table.size() != 0) tmp = new String[table.size()];
		if(table.size() == 0)
			{
				tmp = new String[1];
				tmp[0] = "typ unavailable";
			}

		int i = 0;
		for(String elem : table.keySet())
		{
			tmp[i++] = elem;
			System.out.println(tmp[i++]);
		}
		logger.info("Ende der Methode Auswerten.getKeys");
		logger.info("");
		
		return tmp;
	}

	/**
	 * Checkt ob dieser Server Typ vorhanden ist und gibt ein boolean zurück
	 * 
	 * @param psKey
	 * @return gibt ein True oder False zurück
	 */
	
	public boolean checkKey(String psKey)
	{
		logger.info("Anfang der Methode Auswerten.checkKey");
		logger.info("Ende der Methode Auswerten.checkKey");
		logger.info("");
		logger.debug("übergebener Key Wert: " + psKey);
		logger.debug("return: " + table.containsKey(psKey));
		return table.containsKey(psKey);
	}

	/**
	 * Sucht nach einem Server in den Listen nach den Namen des Servers und gibt die IP-Adresse zurück wenn er gefunden wurde
	 * und wenn er nicht gefunden wurde dann gibt er den String Nicht gefunden zurück
	 * 
	 * @param psName gibt den Namen des Servers an
	 * @return gibt einen String mit einer IP-Adresse zurück oder Nicht gefunden als String
	 */
	
	public String getIPbyName(String psName)
	{
		logger.info("Anfang der Methode Auswerten.getIPbyName");
		logger.debug("übergebener Name: " + psName);
		String sErgebnis = "not found";
		for(String key : table.keySet())
		{
			ArrayList<TSInfo> list = table.get(key);
			for(TSInfo akt : list)
			{
				if(akt.getName().equals(psName))
				{
					sErgebnis = akt.getIP();
				}
			}
		}
		logger.debug(sErgebnis);
		logger.info("Ende der Methode Auswerten.getIPbyName");
		logger.info("");
		return sErgebnis;
	}

	/**
	 * Sucht einen Server nach dem Namen und dem Typ und gibt desen IP-Adresse zurück wenn er diesen Server gefunden hat, 
	 * wenn nicht dann gibt er ein not found als String zurück
	 * 
	 * @param psName gibt den Server Namen an 
	 * @param psTyp gibt den Server Typ an
	 * @return gibt einen String mit einer IP-Adresse zurück oder ein Nicht gefunden als String
	 */
	
	public String getIPbyNameTyp(String psName, String psTyp)
	{
		logger.info("Anfang der Methode Auswerten.getIPbyNameTyp");
		logger.debug("Übergebener Name: " + psName);
		logger.debug("Übergebener Typ: " + psTyp);
		String sErgebnis = "not found";
		try
		{
			int piTyp = Integer.parseInt(psTyp);
			for(String key : table.keySet())
			{
				ArrayList<TSInfo> list = table.get(key);
				for(TSInfo akt : list)
				{
					if(akt.getName().equals(psName) && akt.getTyp() == piTyp)
					{
						sErgebnis = akt.getIP();
					}
				}
			}
		}
		catch(NumberFormatException e)
		{
			logger.debug("Wert konnte nicht in int geparst werden...");
			return ("the typ must be a number");
		}
		logger.debug(sErgebnis);
		logger.info("Ende der Methode Auswerten.getIPbyNameTyp");
		logger.info("");
		return sErgebnis;
	}
	/**
	 * Setzt die Aktuelle Auswahlstrategie fest
	 * 
	 * @param strategy als Objekt einer Klasse mit Implementierter Strategy Schnittstelle
	 */
	public void setStrategy(IStrategy strategy)
	{
		this.strategy = strategy;
	}
	
	/**
	 * wählt einen Server aus nach der momentan festgelegten Auswahlstrategie 
	 * aus dem Strategy Objekt
	 * 
	 * @return gibt eine IP-Adresse als String Objekt zurück
	 */
	
	public String chooseServer()
	{
		return this.strategy.chooseServer(table);
	}
} 