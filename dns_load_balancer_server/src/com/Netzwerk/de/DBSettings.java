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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class describe the database settings
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class DBSettings extends Object 
{
	/**
	 * Instance of this class
	 */
	private static DBSettings dbsettings = new DBSettings();
	
	/**
	 * Model object
	 */
	private Model model = Model.getInstance();
	
	/*
	 * Document attributes
	 */
	
	/**
	 * The xml document
	 */
	private Document doc = null;
	
	/*
	 *  Setting attributes
	 */
	
	/**
	 * The server name of mysql server
	 */
	private String server = null;
	
	/**
	 * The database to use
	 */
	private String database = null;
	
	/**
	 * Username to use the database
	 */
	private String username = null;
	
	/**
	 * Password to use the database
	 */
	private String password = null;
	
	/**
	 * Private constructor of this class
	 */
	private DBSettings()
	{
		this.loadXMLSettings();
	}
	
	/**
	 * Returns the object of this DBSettings class
	 * 
	 * @return the dbsettings object as DBSettings
	 */
	public static DBSettings getInstance()
	{
		return dbsettings;
	}
	
	/**
	 * Load the database settings from a XML file
	 * @return load_xml_ok as boolean
	 */
	public boolean loadXMLSettings()
	{
		boolean load_xml_ok = true;
		
		try
		{
		
			/*
			 * load schema from the w3c
			 */
			SchemaFactory schemafactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			
			/*
			 * load the schema file
			 */
			File schemaLocation = new File(model.getConfigPath() + model.getDBConfigXsd());
			Schema schema = schemafactory.newSchema(schemaLocation);
			
			/*
			 * build a validator from the schema
			 */
			Validator validator = schema.newValidator();
			
			/*
			 * build a DOM Document from the XML file
			 */
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder  = factory.newDocumentBuilder();
			this.doc = builder.parse( new File(model.getConfigPath()+model.getDBConfigXml()) );

			/*
			 * validation of the xml file and the xsd file
			 */
			DOMSource source = new DOMSource(this.doc);
			DOMResult result = new DOMResult();
			
			validator.validate(source,result);
			
			/*
			 * the way through the xml tree
			 */
			Node rootNode = this.doc.getDocumentElement();
			NodeList children = rootNode.getChildNodes();
			
			/*
			 * the values in the xml file
			 */
			this.setServer(children.item(1).getTextContent());
			this.setDatabase(children.item(3).getTextContent());
			this.setUsername(children.item(5).getTextContent());
			this.setPassword(children.item(7).getTextContent());
		}
		catch (IOException | SAXException | ParserConfigurationException e)
		{
			load_xml_ok = false;
			System.out.println("load database settings failed: " + e.getMessage());
		}
		
		return load_xml_ok;
	}

	/**
	 * Return the Server with the Database
	 * 
	 * @return the server as String
	 */
	public String getServer() 
	{
		return this.server;
	}

	/**
	 * Set the Server with the Database
	 * 
	 * @param server as String
	 */
	public void setServer(String server) 
	{
		this.server = server;
	}

	/**
	 * Return the database name
	 * 
	 * @return the database as String
	 */
	public String getDatabase() 
	{
		return this.database;
	}

	/**
	 * Set the database name
	 * 
	 * @param database as String
	 */
	public void setDatabase(String database) 
	{
		this.database = database;
	}

	/**
	 * Return the username of database user
	 * 
	 * @return the username as String
	 */
	public String getUsername() 
	{
		return this.username;
	}

	/**
	 * Set the username of the database user
	 * 
	 * @param username as String
	 */
	public void setUsername(String username) 
	{
		this.username = username;
	}

	/**
	 * Return the password from the database user
	 * 
	 * @return the password as String
	 */
	public String getPassword() 
	{
		return this.password;
	}

	/**
	 * Set the password of the database user
	 * 
	 * @param password as String
	 */
	public void setPassword(String password) 
	{
		this.password = password;
	}
}
