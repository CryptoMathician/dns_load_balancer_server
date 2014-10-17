package com.Netzwerk.de;

import java.io.File;
import java.io.IOException;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class describe a XMLInterpreter for the Server settings which read the
 * settings from a XML file
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
public class XMLSetting extends Object
{

	/**
	 * Document object for the XML Document
	 */
	private Document doc;

	/**
	 * Stores the limit
	 */
	private int limit;

	/**
	 * Stores the path to the XML file
	 */
	private String xmlPath;

	/**
	 * Stores the path to the XSD Path
	 */
	private String xsdPath;

	/**
	 * Stores the config path folder
	 */
	private String configPath;

	/**
	 * Stores the path to the database configuration XML file
	 */
	private String dbxmlPath;

	/**
	 * Stores the path to the database configuration XSD file
	 */
	private String dbxsdPath;

	/**
	 * Stores the port of the server
	 */
	private int serverPort;

	/**
	 * Stores the administration server port
	 */
	private int adminServerPort;

	/**
	 * Logger object of this class
	 */
	private static Logger logger = Logger.getLogger(XMLSetting.class);

	/**
	 * Standard constructor of this class
	 */
	public XMLSetting()
	{
		logger.info("constructor of the XMLSetting class");
	}

	/**
	 * Overloaded Constructor of this class to read the XML file and load the
	 * settings into the program/memory
	 * 
	 * @param psPathXML as String
	 * @param psPathXSD as String
	 * @throws SAXException as Exception
	 * @throws IOException as Exception
	 * @throws ParserConfigurationException as Exception
	 */
	public void XMLSettings(String psPathXML, String psPathXSD) throws SAXException, IOException, ParserConfigurationException
	{
		logger.info("Anfang der Methode XMLSetting.XMLSettings");
		SchemaFactory schemafactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		File schemaLocation = new File(psPathXSD);
		Schema schema = schemafactory.newSchema(schemaLocation);

		Validator validator = schema.newValidator();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(new File(psPathXML));

		DOMSource source = new DOMSource(doc);
		DOMResult result = new DOMResult();

		validator.validate(source, result);
		logger.debug("");
		logger.debug("Validation successful");

		Node rootNode = doc.getDocumentElement();
		NodeList children = rootNode.getChildNodes();

		try
		{
			limit = Integer.parseInt(children.item(1).getTextContent());
			logger.debug("Parsen from String to int was successful");
			configPath = children.item(3).getTextContent();
			xmlPath = children.item(5).getTextContent();
			xsdPath = children.item(7).getTextContent();
			dbxmlPath = children.item(9).getTextContent();
			dbxsdPath = children.item(11).getTextContent();
			serverPort = Integer.parseInt(children.item(13).getTextContent());
			adminServerPort = Integer.parseInt(children.item(15).getTextContent());
			logger.debug("Reading from the XML file was successful");
		}
		catch (NumberFormatException e)
		{
			logger.error("Error by parsing the String in the int format");
		}
		logger.info("End of the method XMLSetting.XMLSettings");
	}

	/**
	 * Returns the limit
	 * 
	 * @return the limit as int
	 */
	public int getLimit()
	{
		return this.limit;
	}

	/**
	 * Returns the path to the XML file
	 * 
	 * @return XMLPfad as String
	 */
	public String getXMLPfad()
	{
		return this.xmlPath;
	}

	/**
	 * Returns the XSD file path
	 * 
	 * @return XSDPfad as String
	 */
	public String getXSDPFad()
	{
		return this.xsdPath;
	}

	/**
	 * Returns the configuration path folder
	 * 
	 * @return the configPath as String
	 */
	public String getConfigPath()
	{
		return this.configPath;
	}

	/**
	 * Returns the database XML file path
	 * 
	 * @return the dbxmlPath as String
	 */
	public String getDbxmlPath()
	{
		return this.dbxmlPath;
	}

	/**
	 * Returns the database XSD file path
	 * 
	 * @return the dbxsdPath as String
	 */
	public String getDbxsdPath()
	{
		return this.dbxsdPath;
	}

	/**
	 * Returns the server port
	 * 
	 * @return the serverPort as int
	 */
	public int getServerPort()
	{
		return this.serverPort;
	}

	/**
	 * Returns the administration server port
	 * 
	 * @return the adminServerPort as int
	 */
	public int getAdminServerPort()
	{
		return this.adminServerPort;
	}
}
