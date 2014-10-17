package com.Netzwerk.de;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This interface describe the abstract methods which is to implement from the
 * strategy algorithm classes
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public interface IStrategy
{
	/**
	 * This methode describe a algorithm to choose a server
	 * 
	 * @param table as Hashtable
	 * @return String
	 */
	public String chooseServer(Hashtable<String, ArrayList<TSInfo>> table);

	/**
	 * Return the name of the algorithm
	 * 
	 * @return the name of the algorithm
	 */
	public String getInfo();

	/**
	 * Return the type of the servers
	 * 
	 * @return the type as int
	 */
	public int getTyp();
}
