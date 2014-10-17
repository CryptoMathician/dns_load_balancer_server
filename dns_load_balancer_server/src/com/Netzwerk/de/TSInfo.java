package com.Netzwerk.de;

import org.apache.log4j.Logger;

/**
 * This class describe a terminal server with the usage rate, the name and his
 * type, with his IPv4 address
 *
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class TSInfo extends Object implements Comparable<TSInfo>
{
	/*
	 *  Begin of attributes
	 */
	
	/**
	 * Stores the usage rate of the server
	 */
	private int usageRate;

	/**
	 * Stores the type of the server
	 */
	private int type;

	/**
	 * Stores the IPv4 address of the server
	 */
	private String ip;

	/**
	 * Stores the server name of the server
	 */
	private String name;

	/**
	 * Logger object of this class
	 */
	private static Logger logger = Logger.getLogger(TSInfo.class);

	/*
	 *  End of attributes
	 */

	/**
	 * Constructof of this class
	 * 
	 * @param piUsageRate as int
	 * @param piType as int
	 * @param psIP as String
	 * @param psName as String
	 */
	public TSInfo(int piUsageRate, int piType, String psIP, String psName)
	{
		logger.info("Start constructor");
		this.usageRate = piUsageRate;
		this.type = piType;
		this.ip = psIP;
		this.name = psName;
		logger.info("End constructor");
	}

	/**
	 * Returns the usage rate of the server
	 * 
	 * @return the usageRate as int
	 */
	// Anfang Methoden
	public int getUsageRate()
	{
		return this.usageRate;
	}

	/**
	 * Returns the type of the server
	 * 
	 * @return the type as int
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Returns the IPv4 address
	 * 
	 * @return the ip as String
	 */
	public String getIP()
	{
		return this.ip;
	}

	/**
	 * Returns the name of the server
	 * 
	 * @return Name as String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Overloaded compareTo method to sort the TSInfo objects
	 */
	@Override
	public int compareTo(TSInfo poTSInfo)
	{
		return this.usageRate - poTSInfo.getUsageRate();
	}

	// End of methods
} // end of TSInfo