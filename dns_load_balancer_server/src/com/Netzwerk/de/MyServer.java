package com.Netzwerk.de;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.xml.sax.SAXException;

/**
 * This class describe the start of the server and load the parameter.<br>
 * For every client would be start a new thread.
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class MyServer extends Object implements Runnable
{
	/**
	 * MyServer object of this class
	 */
	private static MyServer myServer = new MyServer();

	/**
	 * Logger object of this class
	 */
	private static Logger logger = Logger.getRootLogger();

	/**
	 * Stores the number of actual connected clients
	 */
	private static int openSockets = 0;

	/**
	 * Stores the status of the main loop
	 */
	private static boolean loop = true;

	/**
	 * Stores the start parameters
	 */
	private String[] args = null;

	/**
	 * Stores the thread for the clients
	 */
	private Thread serverThread = null;

	/**
	 * Stores the model object
	 */
	private Model model = Model.getInstance();

	/**
	 * Stores the connection to the client
	 */
	private SSLSocket clientSocket = null;

	/**
	 * SSLServerSocket
	 */
	private SSLServerSocket sslServerSocket = null;

	/**
	 * Stores the status, if clientSocket must be closed
	 */
	private boolean clientMustClosed = false;

	/**
	 * Stores the status of the administration servers
	 */
	private boolean adminServerOn = false;

	/**
	 * Private constructor of this class
	 */
	private MyServer()
	{

	}

	/**
	 * Return the object of this class
	 * 
	 * @return myServer as MyServer
	 */
	public static MyServer getInstance()
	{
		return myServer;
	}

	/**
	 * Initialize the server
	 * 
	 * @param pasArgs as String Array
	 */
	public void initialize(String[] pasArgs)
	{
		this.args = pasArgs;
	}

	/**
	 * This method is the start point of this program
	 * 
	 * @param args as String array
	 */
	public static void main(String[] args)
	{
		Runnable myServer = MyServer.getInstance();
		Thread start = new Thread(myServer);
		((MyServer) myServer).initialize(args);
		start.start();
	}

	/**
	 * The thread of the server which handle the incoming clients and starts new
	 * threads for every client and starts other necessary parts of this program
	 */
	public void run()
	{
		/*
		 * start the command line interface
		 */
		Runnable commandthread = CommandThread.getInstance();
		Thread commandThread = new Thread(commandthread);
		commandThread.start();

		/*
		 * start the administration Server to control the Server over the
		 * Network
		 */
		Runnable adminserver = AdminServer.getInstance();
		Thread adminServer = new Thread(adminserver);
		adminServer.start();

		try
		{
			PatternLayout layout = new PatternLayout();
			HTMLLayout layout2 = new HTMLLayout();
			FileAppender fileAppender = new FileAppender(layout,"logs/ServerLOG.log", false);
			FileAppender fileAppender2 = new FileAppender(layout2,"logs/ServerHTML.html", false);
			logger.addAppender(fileAppender2);
			logger.addAppender(fileAppender);
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
			logger.setLevel(Level.ALL);

			XMLSetting xml = new XMLSetting();

			if (args.length > 0)
			{
				for (int i = 0; i < args.length; i++)
				{
					System.out.println("Startparameter " + i + " :" + args[i]);
				}
				logger.info("hat Startparameter");
				if ((args[0].equals("-f") && args[1].equals("n"))
						&& (args[0] != null && args[1] != null))
				{
					System.out.println("Keine Settings Datei");
					if (args[2].equals("-L")
							&& (args[2] != null && args[3] != null))
					{
						model.setMaxLimit(Integer.parseInt(args[3]));
						System.out.println("Limit: " + model.getMaxLimit());
					}
					if ((args[4].equals("-xml") && (args[5] != null) && (args[6]
							.equals("-xsd") && args[7] != null)))
					{
						model.setXMLPath(args[5]);
						model.setXSDPath(args[7]);
					}
				}
				else if ((args[0].equals("-f") && ((args[1].equals("y") || args[1]
						.equals("Y")) || (args[1].equals("j") || args[1]
						.equals("J"))))
						&& (args[0] != null && args[1] != null))
				{
					if (args[2].equals("-XML")
							&& (args[2] != null && args[3] != null)
							&& (args[4].equals("-XSD") && (args[4] != null && args[5] != null)))
					{
						xml.XMLSettings(args[3], args[5]);
						model.setMaxLimit(xml.getLimit());
						model.setXMLPath(xml.getXMLPfad());
						model.setXSDPath(xml.getXSDPFad());
						model.setConfigPath(xml.getConfigPath());
						model.setDBConfigXml(xml.getDbxmlPath());
						model.setDBConfigXsd(xml.getDbxsdPath());
						model.setServerPort(xml.getServerPort());
						model.setAdminServerPort(xml.getAdminServerPort());
					}
				}
			}
			else
			{
				logger.info("hat keine Startparameter");
			}

			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			sslServerSocket = (SSLServerSocket) sslserversocketfactory
					.createServerSocket(model.getServerPort());

			System.out.println("Server run...");
			if (model.getMaxLimit() > 100)
			{
				throw new IllegalLimitException(
						"Limit is to High! must be under 100");
			}

			xml = null;

			do
			{
				System.out.println("Loop: " + loop);
				if (loop)
				{
					try
					{
						clientSocket = (SSLSocket) sslServerSocket.accept();
						clientMustClosed = true;
						serverThread = new Thread(new ServerThread(
								clientSocket, model.getXMLPath(),
								model.getXSDPath(), this, model));
						serverThread.start();
						openSockets++;
						System.out.println("aktuelle Verbindungen: "
								+ openSockets);
					}
					catch (Exception e)
					{
						System.out.println("MyServer: " + e.getMessage());
					}
				}
				else
				{
					synchronized (this)
					{
						try
						{
							logger.debug("MyServer Thread Wartet nun...");
							System.out.println("MyServer Thread Wartet nun...");
							this.wait();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}

			} while (loop || openSockets > 0 || adminServerOn);

			if (clientMustClosed) clientSocket.close();

		}
		catch (IOException | SAXException | ParserConfigurationException
				| IllegalLimitException e)
		{
			logger.fatal(e.getMessage());
		}
		System.out.println("Server is heruntergefahren!...");
	}

	/**
	 * Destroy the SSLServerSocket object
	 */
	public void destroySSLServerSocketAccept()
	{
		try
		{
			sslServerSocket.close();
			logger.debug("accept wurde geschlossen...");
		}
		catch (NullPointerException | IOException e)
		{
			logger.fatal(e.getMessage());
		}
	}

	/**
	 * Decrement the actual connected clients with 1
	 * 
	 * @throws MyServerException as Exception
	 */
	public static void decrementOpenSockets() throws MyServerException
	{
		openSockets--;
		if (openSockets < 0)throw new MyServerException("openSockets is under 0");
	}

	/**
	 * Set the loop to false
	 */
	public static void loopOff()
	{
		loop = false;
	}

	/**
	 * Return the number of actual connected clients
	 * 
	 * @return openSockets as Integer
	 */
	public static int getOpenSockets()
	{
		return openSockets;
	}

	/**
	 * Return the status of the loop
	 * 
	 * @return loop as boolean
	 */
	public static boolean getLoop()
	{
		return loop;
	}

	/**
	 * Return the status of the administration server
	 * 
	 * @return the adminServerOn as boolean
	 */
	public boolean isAdminServerOn()
	{
		return this.adminServerOn;
	}

	/**
	 * Set the status of the administration server
	 */
	public void setAdminServerOff()
	{
		this.adminServerOn = false;
	}

}
