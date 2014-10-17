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
 * This class describe a interpreter of the XML file. The content of the XML
 * file would be sort and store informations of the server in a TSInfo object.
 *
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class XMLInterpreter extends Object
{

	/**
	 * A object of a chosen strategy to take a server
	 */
	private IStrategy strategy = null;

	/**
	 * A table to store the lists in order of types of the servers
	 */
	private Hashtable<String, ArrayList<TSInfo>> table = new Hashtable<String, ArrayList<TSInfo>>();

	/**
	 * Document for the XML file
	 */
	private Document doc = null;

	/**
	 * A object of a logger
	 */
	private static Logger logger = Logger.getLogger(XMLInterpreter.class);

	/**
	 * The public constructor of this class
	 */
	public XMLInterpreter()
	{
		logger.info("Anfang / Ende des Konstruktors Auswerten");
	}

	/**
	 * This method read a XML file and validate this file with a XSD file<br>
	 * and sort the TSInfo object in a list<br>
	 * The lists are sorted by type<br>
	 * 
	 * @param psXMLPath as String
	 * @param psXSDPath as String
	 */
	public void readXMLFileWithXSD(String psXMLPath, String psXSDPath)
	{
		logger.info("Beginn der Methode dateiAuslesen");

		try
		{
			logger.debug("Versuche XSD zu Laden");
			SchemaFactory schemafactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");

			File schemaLocation = new File(psXSDPath);
			Schema schema = schemafactory.newSchema(schemaLocation);

			Validator validator = schema.newValidator();

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new File(psXMLPath));

			DOMSource source = new DOMSource(doc);
			DOMResult result = new DOMResult();

			validator.validate(source, result);

			logger.debug("");
			logger.debug("Validation war erfolgreich");

			Node rootNode = doc.getDocumentElement();

			logger.debug("Root Knoten: " + rootNode.getNodeName());
			logger.debug("");
			logger.debug("\nhat Kinder Knoten: " + rootNode.hasChildNodes());

			NodeList children = rootNode.getChildNodes();

			for (int i = 0; i < children.getLength(); i++)
			{
				NamedNodeMap nnm = children.item(i).getAttributes();
				if (nnm != null)
				{
					NodeList childrensChildren = children.item(i)
							.getChildNodes();

					String sIp = childrensChildren.item(1).getTextContent();
					String sType = childrensChildren.item(3).getTextContent();
					int iTyp = Integer.parseInt(sType);
					String sUsageRate = childrensChildren.item(5)
							.getTextContent();
					int iAuslastung = Integer.parseInt(sUsageRate);

					String sName = childrensChildren.item(7).getTextContent();

					if (iTyp >= 0)
					{
						if (table.containsKey(sType))
						{
							table.get(sType).add(
									new TSInfo(iAuslastung, iTyp, sIp, sName));
						}
						else
						{
							table.put(sType, new ArrayList<TSInfo>());
							table.get(sType).add(
									new TSInfo(iAuslastung, iTyp, sIp, sName));
						}
					}
					else
					{
						logger.warn("Typ must be in the positive range Server: ");
						logger.warn("Server: ;Type: " + sType + "; IP: " + sIp
								+ ";usage rate: " + sUsageRate + "; Name: "
								+ sName);
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

		logger.debug("Lists of the server sorted by type");

		int i = 1;
		int iCount = 0;
		for (String elem : table.keySet())
		{
			ArrayList<TSInfo> list = table.get(elem);
			logger.debug("\n\n");
			logger.debug("Typ : " + elem + "\n\n");
			i = 1;
			if (table.get(elem).size() < 3)
			{
				logger.warn("< 3 Server of type " + elem);
			}
			for (TSInfo act : list)
			{
				logger.debug("TSInfo Object " + i + ": IP:" + act.getIP()
						+ " ;usage rate: " + act.getUsageRate() + " ;Type: "
						+ act.getType() + " ;Name: " + act.getName());
				i++;
				iCount++;
			}
		}

		logger.info("\n\n");
		logger.info("Number of TSInfo Objects: " + iCount);

		logger.info("End of this method to read the XML file");
		logger.info("");
	}

	/**
	 * Read the available types and return the types
	 * 
	 * @return the types as String array
	 */
	public String[] getKeys()
	{
		logger.info("Starts the method XMLInterpreter.getKeys");
		logger.debug("Size of the table: " + table.size());
		logger.debug("Keyset of the table: " + table.keySet());
		String[] tmp = null;
		if (table.size() != 0) tmp = new String[table.size()];
		if (table.size() == 0)
		{
			tmp = new String[1];
			tmp[0] = "type unavailable";
		}

		int i = 0;
		for (String elem : table.keySet())
		{
			tmp[i++] = elem;
			System.out.println(tmp[i++]);
		}
		logger.info("End of the method XMLInterpreter.getKeys");
		logger.info("");

		return tmp;
	}

	/**
	 * This method checks if the type available
	 * 
	 * @param psKey as String
	 * @return a boolean
	 */
	public boolean checkKey(String psKey)
	{
		logger.info("Anfang der Methode Auswerten.checkKey");
		logger.info("Ende der Methode Auswerten.checkKey");
		logger.info("");
		logger.debug("Uebergebener Key Wert: " + psKey);
		logger.debug("return: " + table.containsKey(psKey));
		return table.containsKey(psKey);
	}

	/**
	 * Search a server with the name of the server
	 * 
	 * @param psName as String
	 * @return sErgebnis as String
	 */
	public String getIPbyName(String psName)
	{
		logger.info("Start of the method XMLInterpreter.getIPbyName");
		logger.debug("Name: " + psName);
		String sErgebnis = "not found";
		for (String key : table.keySet())
		{
			ArrayList<TSInfo> list = table.get(key);
			for (TSInfo akt : list)
			{
				if (akt.getName().equals(psName))
				{
					sErgebnis = akt.getIP();
				}
			}
		}
		logger.debug(sErgebnis);
		logger.info("Ende of the method XMLInterpreter.getIPbyName");
		logger.info("");
		return sErgebnis;
	}

	/**
	 * Search a server by his name and his type
	 * 
	 * @param psName as String
	 * @param psType as String
	 * @return sErgebnis as String
	 */
	public String getIPbyNameTyp(String psName, String psType)
	{
		logger.info("Start of the method XMLInterpreter.getIPbyNameTyp");
		logger.debug("Name: " + psName);
		logger.debug("Type: " + psType);
		String sErgebnis = "not found";
		try
		{
			int piTyp = Integer.parseInt(psType);
			for (String key : table.keySet())
			{
				ArrayList<TSInfo> list = table.get(key);
				for (TSInfo akt : list)
				{
					if (akt.getName().equals(psName) && akt.getType() == piTyp)
					{
						sErgebnis = akt.getIP();
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			logger.debug("cant parse from String to int...");
			return ("the typ must be a number");
		}
		logger.debug(sErgebnis);
		logger.info("End of the method XMLInterpreter.getIPbyNameTyp");
		logger.info("");
		return sErgebnis;
	}

	/**
	 * Set the actual strategy algorithm
	 * 
	 * @param strategy as IStrategy
	 */
	public void setStrategy(IStrategy strategy)
	{
		this.strategy = strategy;
	}

	/**
	 * Choose a server with the actual strategy algorithm
	 * 
	 * @return the IPv4 address of the choosen server as String
	 */
	public String chooseServer()
	{
		return this.strategy.chooseServer(table);
	}
}