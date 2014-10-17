package com.Netzwerk.de;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

/**
 * This class describe how the client would be handle and which commands can use
 * the client.<br>
 * This commands can the client use<br>
 * <br>
 * -getip by name type<br>
 * -getip by name<br>
 * -getip save type<br>
 * -getip balance type<br>
 * -gettyp all<br>
 * -gettyp type<br>
 * -help<br>
 * -exit (to close the connection)<br>
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class ServerThread extends Object implements Runnable
{
	/**
	 * Stores the connection to the client
	 */
	private SSLSocket client;

	/**
	 * Stores the IPv4 address
	 */
	private String ip;

	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger(ServerThread.class);

	/**
	 * Stores the XML file path
	 */
	private String xmlPath;

	/**
	 * Stores the XSD file path
	 */
	private String xsdPath;

	/**
	 * Stores the MyServer object
	 */
	private MyServer myServer;

	/**
	 * Stores the Model object
	 */
	private Model model;

	/**
	 * Overloaded constructor of this class
	 * 
	 * @param psslClientSocket as SSLSocket
	 * @param psXMLPath as String
	 * @param psXSDPath as String
	 * @param poMyServer as MyServer
	 * @param poModel as Model
	 */
	public ServerThread(SSLSocket psslClientSocket, String psXMLPath,
			String psXSDPath, MyServer poMyServer, Model poModel)
	{

		logger.info("Start of the constructor");
		this.client = psslClientSocket;
		this.xmlPath = psXMLPath;
		this.xsdPath = psXSDPath;
		this.myServer = poMyServer;
		this.model = poModel;

		logger.info("end of the constructor");
	}

	/**
	 * This method describe the use of the getip by name or getip by name type
	 * 
	 * @param poaAsw as XMLInterpreter
	 * @param pstTokenizer as StringTokenizer
	 * @param ppwOut as PrintWriter
	 */
	private void by(XMLInterpreter poaAsw, StringTokenizer pstTokenizer,
			PrintWriter ppwOut)
	{
		logger.info("Anfang Methode by");
		if (pstTokenizer.hasMoreTokens())
		{
			String serverName = pstTokenizer.nextToken();
			if (pstTokenizer.hasMoreTokens())
			{
				String serverTyp = pstTokenizer.nextToken();
				String sIP = poaAsw.getIPbyNameTyp(serverName, serverTyp);
				ppwOut.println(sIP);
			}
			else
			{
				String sIP = poaAsw.getIPbyName(serverName);
				ppwOut.println(sIP);
			}
		}
		else
		{
			ppwOut.println("getip by <server name> [<0|1|2|3|...>]");
		}
		logger.info("Ende Methode by");
	}

	/**
	 * This describe a method which use the save algorithm
	 * 
	 * @param poaAsw as XMLInterpreter
	 * @param pstTokenizer as StringTokenizer
	 * @param ppwOut as PrintWriter
	 */
	private void save(XMLInterpreter poaAsw, StringTokenizer pstTokenizer,
			PrintWriter ppwOut)
	{
		logger.info("Start method save");
		if (pstTokenizer.hasMoreTokens())
		{
			String typ = pstTokenizer.nextToken();
			try
			{
				int iTyp = Integer.parseInt(typ);
				poaAsw.setStrategy(new Save(iTyp, model.getMaxLimit()));
				ip = poaAsw.chooseServer();
				ppwOut.println(ip);
			}
			catch (NumberFormatException e)
			{
				logger.debug("Parse from String to int was not successful");
				ppwOut.println("the typ must be a number");
			}
		}
		else
		{
			ppwOut.println("getip save <0|1|2|3|..>");
		}
		logger.info("End method save");
	}

	/**
	 * This describe a method which use the balance algorithm
	 * 
	 * @param poaAsw as XMLInterpreter
	 * @param pstTokenizer as StringTokenizer
	 * @param ppwOut as PrintWriter
	 */
	private void balance(XMLInterpreter poaAsw, StringTokenizer pstTokenizer,
			PrintWriter ppwOut)
	{
		logger.info("Start method balance");
		if (pstTokenizer.hasMoreTokens())
		{
			try
			{
				String typ = pstTokenizer.nextToken();
				int iTyp = Integer.parseInt(typ);
				poaAsw.setStrategy(new Balance(iTyp));
				ip = poaAsw.chooseServer();
				ppwOut.println(ip);
			}
			catch (NumberFormatException e)
			{
				ppwOut.println("the typ must be a number");
			}
		}
		else
		{
			ppwOut.println("getip balance  <0|1|2|3|..>");
		}
		logger.info("End method balance");
	}

	/**
	 * This method choose a method from the getip command
	 *
	 * @param poaAsw as XMLInterpreter
	 * @param pstTokenizer as StringTokenizer
	 * @param ppwOut as PrintWriter
	 */
	private void ip(XMLInterpreter poaAsw, StringTokenizer pstTokenizer,
			PrintWriter ppwOut)
	{
		logger.info("Start method ip");
		if (pstTokenizer.hasMoreTokens())
		{
			String next = pstTokenizer.nextToken();
			next = next.toLowerCase();

			if (next.equals("by"))
			{
				this.by(poaAsw, pstTokenizer, ppwOut);
			}
			else if (next.equals("server"))
			{
				poaAsw.setStrategy(model.getStrategy());
				ip = poaAsw.chooseServer();
				ppwOut.println(ip);
			}
			else if (next.equals("balance")
					&& model.getAdvancedClient() == true)
			{
				this.balance(poaAsw, pstTokenizer, ppwOut);
			}
			else if (next.equals("save") && model.getAdvancedClient() == true)
			{
				this.save(poaAsw, pstTokenizer, ppwOut);
			}
			else
			{
				ppwOut.println("getip <by|server|if advanced client enabled then save|balance available>");
			}
		}
		else
		{
			ppwOut.println("getip <by|server|if advanced client enabled then save|balance available>");
		}
		logger.info("End method ip");
	}

	/**
	 * This method return all available server types or return if a specific
	 * type of server available
	 * 
	 * @param poaAsw as XMLInterpreter
	 * @param pstTokenizer as StringTokenizer
	 * @param ppwOut as PrinterWriter
	 */
	private void typ(XMLInterpreter poaAsw, StringTokenizer pstTokenizer,
			PrintWriter ppwOut)
	{
		logger.info("Start method type");
		if (pstTokenizer.hasMoreTokens())
		{
			String typ = pstTokenizer.nextToken();
			typ = typ.toLowerCase();
			if (typ.equals("all"))
			{
				String sAusgabe = "";
				String[] tmp = poaAsw.getKeys();
				for (int i = 0; i < tmp.length; i++)
				{
					sAusgabe += tmp[i] + " ";
				}
				ppwOut.println(sAusgabe);
			}
			else
			{
				boolean isAvailable = poaAsw.checkKey(typ);
				ppwOut.println(isAvailable);
			}
		}
		else
		{
			ppwOut.println("gettyp <all|0|1|2|3|...>");
		}
		logger.info("End method type");
	}

	/**
	 * This method handle the command of the client
	 * 
	 * @param poaAsw as XMLInterpreter
	 * @param pstTokenizer as StringTokenizer
	 * @param pbAgain as boolean
	 * @param ppwOut as PrintWriter
	 * @return pbAgain as boolean
	 * @throws IOException as Exception
	 */
	private boolean chooseMethod(XMLInterpreter poaAsw,
			StringTokenizer pstTokenizer, boolean pbAgain, PrintWriter ppwOut)
			throws IOException
	{
		pbAgain = true;
		logger.info("Starts method chooseMethod");
		if (pstTokenizer.hasMoreTokens())
		{
			String next = pstTokenizer.nextToken();
			if (next.equals("exit"))
			{
				ppwOut.println("closed");
				client.close();
				pbAgain = false;
				return pbAgain;
			}
			else if (next.equals("getip"))
			{
				this.ip(poaAsw, pstTokenizer, ppwOut);
			}
			else if (next.equals("gettyp"))
			{
				this.typ(poaAsw, pstTokenizer, ppwOut);
			}
			else if (next.equals("help"))
			{
				ppwOut.println("<getip|gettyp>");
			}
			else
			{
				ppwOut.println("cannot found this command");
			}
		}
		logger.info("End of the method chooseMethod");
		return pbAgain;

	}

	/**
	 * The thread which handle the client
	 */
	public void run()
	{
		logger.info("Start method run()");

		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			XMLInterpreter asw = new XMLInterpreter();

			asw.readXMLFileWithXSD(xmlPath, xsdPath);
			boolean again = true;

			while (again)
			{
				String nachricht = in.readLine();

				StringTokenizer tokenizer = new StringTokenizer(nachricht);

				again = this.chooseMethod(asw, tokenizer, again, out);
			}

			MyServer.decrementOpenSockets();

			if (MyServer.getOpenSockets() == 0 && MyServer.getLoop() == false)
			{
				synchronized (myServer)
				{
					System.out.println("\n\nnotify myServer\n\n");
					myServer.notify();
				}
			}

		}
		catch (IOException | MyServerException e)
		{
			e.printStackTrace();
			try
			{
				MyServer.decrementOpenSockets();
				if (MyServer.getOpenSockets() == 0)
				{
					synchronized (myServer)
					{
						logger.debug("notify MyServer");
						myServer.notify();
					}
				}
			}
			catch (MyServerException e1)
			{
				e1.printStackTrace();
			}
		}

		logger.info("End method run()");
	}

}
