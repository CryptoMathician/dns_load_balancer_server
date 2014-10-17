package com.Netzwerk.de;

/**
 * This class describe a object that stores all the relevant values for the
 * server<br>
 * - maximum Limit<br>
 * - XML file path<br>
 * - XSD file path<br>
 * - Database XML file path<br>
 * - Database XSD file path<br>
 * - Strategy algorithm<br>
 * - The advanced client option<br>
 * - The config path<br>
 * - The server port<br>
 * - The administration server port<br>
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class Model extends Object
{
	/**
	 * The singleton object of this class
	 */
	private static Model model = new Model();

	/**
	 * Stores the limit
	 */
	private int maxLimit = 85;

	/**
	 * Stores the XML path
	 */
	private String xmlPath = "TSInfo.xml";

	/**
	 * Stores the XSD file path
	 */
	private String xsdPath = "TSInfo.xsd";

	/**
	 * Filename of the XML settings file for the database
	 */
	private String dbxmlPath = "db_conf.xml";

	/**
	 * Filename of the XSD schema file for the xml db settings
	 */
	private String dbxsdPath = "db_conf.xsd";

	/**
	 * Stores the actual strategy
	 */
	private IStrategy strategy = new Balance(0);

	/**
	 * Stores the status of the advanced option
	 */
	private boolean advancedClient = false;

	/**
	 * The path for the configuration path
	 */
	private String configPath = "/etc/dns_load_balancer/";

	/**
	 * The port of the server
	 */
	private int serverPort = 20500;

	/**
	 * The port of the administration server
	 */
	private int adminServerPort = 20510;

	/**
	 * Private constructor of this class
	 */
	private Model()
	{

	}

	/**
	 * Return the object of this class
	 * 
	 * @return model as Model
	 */
	public static Model getInstance()
	{
		return model;
	}

	/**
	 * Return the limi
	 * 
	 * @return limit as int
	 */
	public int getMaxLimit()
	{
		return this.maxLimit;
	}

	/**
	 * Set the limit
	 * 
	 * @param psLimit as int
	 */
	public void setMaxLimit(int psLimit)
	{
		this.maxLimit = psLimit;
	}

	/**
	 * Return the path to the XML file
	 * 
	 * @return xmlpath as String
	 */
	public String getXMLPath()
	{
		return this.xmlPath;
	}

	/**
	 * Set the XML file path
	 * 
	 * @param psXMLPath as String
	 */
	public void setXMLPath(String psXMLPath)
	{
		this.xmlPath = psXMLPath;
	}

	/**
	 * Return the XSD file path
	 * 
	 * @return xsdpath as String
	 */
	public String getXSDPath()
	{
		return this.xsdPath;
	}

	/**
	 * Set the XSD file path
	 * 
	 * @param psXSDPath as String
	 */
	public void setXSDPath(String psXSDPath)
	{
		this.xsdPath = psXSDPath;
	}

	/**
	 * Return the actual strategy
	 * 
	 * @return strategy as IStrategy
	 */
	public IStrategy getStrategy()
	{
		return this.strategy;
	}

	/**
	 * Set the actual strategy
	 * 
	 * @param strategy as IStrategy
	 */
	public void setStrategy(IStrategy strategy)
	{
		this.strategy = strategy;
	}

	/**
	 * Return the status of the advanced client option
	 * 
	 * @return advancedClient as boolean
	 */
	public boolean getAdvancedClient()
	{
		return this.advancedClient;
	}

	/**
	 * Set the value of the advanced client option
	 * 
	 * @param advancedClient as boolean
	 */
	public void setAdvancedClient(boolean advancedClient)
	{
		this.advancedClient = advancedClient;
	}

	/**
	 * Return the path to the configuration folder
	 * 
	 * @return the configPath as String
	 */
	public String getConfigPath()
	{
		return this.configPath;
	}

	/**
	 * Set the path to the configuration folder
	 * 
	 * @param psConfigPath as String
	 */
	public void setConfigPath(String psConfigPath)
	{
		this.configPath = psConfigPath;
	}

	/**
	 * Return the database configuration file path
	 * 
	 * @return the dbxmlPath as String
	 */
	public String getDBConfigXml()
	{
		return this.dbxmlPath;
	}

	/**
	 * Set the database configuration file path
	 * 
	 * @param psDBXmlPath as String
	 */
	public void setDBConfigXml(String psDBXmlPath)
	{
		this.dbxmlPath = psDBXmlPath;
	}

	/**
	 * Return the path of the database XSD file path
	 * 
	 * @return the dbxsdPath as String
	 */
	public String getDBConfigXsd()
	{
		return this.dbxsdPath;
	}

	/**
	 * Set the path to the XSD file
	 * 
	 * @param psDBXsdPath as String
	 */
	public void setDBConfigXsd(String psDBXsdPath)
	{
		this.dbxsdPath = psDBXsdPath;
	}

	/**
	 * Return the server port
	 * 
	 * @return the serverPort as int
	 */
	public int getServerPort()
	{
		return serverPort;
	}

	/**
	 * Set the port of the server
	 * 
	 * @param serverPort as int
	 */
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	/**
	 * Return the administration server port
	 * 
	 * @return the adminServerPort as int
	 */
	public int getAdminServerPort()
	{
		return adminServerPort;
	}

	/**
	 * Set the administration server port
	 * 
	 * @param adminServerPort as int
	 */
	public void setAdminServerPort(int adminServerPort)
	{
		this.adminServerPort = adminServerPort;
	}

}
