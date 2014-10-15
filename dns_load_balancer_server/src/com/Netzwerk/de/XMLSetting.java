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
 * Diese Klasse beschreibt das Auslesen der XML datei mit den Settings für den Server
 * 
 * @author Pascal Schäfer
 *
 */
public class XMLSetting 
{

	/**
	 * Document Objekt für das XML Document
	 */
	private Document doc;
	/**
	 * Limit zum speichern von dem ausgelesenen Limit
	 */
	private int limit;
	/**
	 * XMLPfad zum speichern von dem ausgelesenen XMLPfad
	 */
	private String xmlPath;
	/**
	 * XSDPfad zum speichern von dem ausgelesenen XSDPfad
	 */
	private String xsdPath;
	
	

	/**
	 * Logger Objekt zum Loggen von Ereignissen
	 */
	private static Logger logger = Logger.getLogger( XMLSetting.class);

	/**
	 * Standard Konstruktor der Klasse XMLSetting
	 */
	public XMLSetting()
	{
		logger.info("Konstruktor XMLSetting");
	}

	/**
	 * Auslesen der XML Datei mit überprüfung von einer XSD Datei und Speichern der Settings in den Klassenattributen
	 * der Pfad von der XML und XSD Datei wird übergeben
	 * 
	 * @param psPathXML als String
	 * @param psPathXSD als String
	 * @throws SAXException 
	 * @throws IOException
	 * @throws ParserConfigurationException
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
		DocumentBuilder builder  = factory.newDocumentBuilder();
		doc = builder.parse( new File( psPathXML ) );

		DOMSource source = new DOMSource(doc);
		DOMResult result = new DOMResult();
		
		validator.validate(source,result);
		logger.debug("");
		logger.debug("Validation erfolgreich");
		
		Node rootNode = doc.getDocumentElement();
		NodeList children = rootNode.getChildNodes();

		try
		{
			limit = Integer.parseInt(children.item(1).getTextContent());
			logger.debug("Parsen von String nach Int für Limit war erfolgreich");
			xmlPath = children.item(3).getTextContent();
			xsdPath = children.item(5).getTextContent();
			logger.debug("auslesen aus der XML Settings Datei war erfolgreich");
		}
		catch(NumberFormatException e)
		{
			logger.error("Fehler beim Umwandeln des Limits in einen String: Falsche Angabe es muss eine Ganzzahl sein!");
		}
		logger.info("Ende der Methode XMLSetting.XMLSettings");
	}

	/**
	 * gibt den Wert von Limit zurück
	 * 
	 * @return Limit als Int
	 */

	public int getLimit() 
	{
		return limit;
	}
	/**
	 * gibt den Wert von XMLPfad zurück
	 * 
	 * @return XMLPfad als String
	 */
	public String getXMLPfad() {
		return xmlPath;
	}
	/**
	 * gibt den Wert von XSDPfad zurück
	 * 
	 * @return XSDPfad als String
	 */
	public String getXSDPFad() {
		return xsdPath;
	}
	
}
