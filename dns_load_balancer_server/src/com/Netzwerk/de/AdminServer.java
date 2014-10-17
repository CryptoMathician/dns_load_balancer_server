package com.Netzwerk.de;

import java.io.IOException;
import java.util.Vector;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

/**
 * This class describe a administration server which accept administrator clients
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
public class AdminServer extends Object implements Runnable
{
	/**
	 * The singleton object of this class
	 */
	private static AdminServer adminServer = new AdminServer();

	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger(AdminServer.class);
	
	/**
	 * The model object of the Model class
	 */
	private Model model = Model.getInstance();
	
	/**
	 * SSLServerSocket 
	 */
	private SSLServerSocket sslServerSocket = null;
	
	/**
	 * SSLSocket for the client connection
	 */
	private SSLSocket clientSocket = null;
	
	/**
	 * Object of a AdminServerThread
	 */
	private AdminServerThread adminServerThread = null;
	
	/**
	 * Store a thread
	 */
	private Thread thread = null;
	
	/**
	 * The main loop of the server
	 */
	private static boolean loop = true;
	
	/**
	 * The actual number of the connected clients
	 */
	private static int openSockets = 0;
	
	/**
	 * Status of the client, must close the client or not
	 */
	private boolean clientMustClosed = false;
	
	/**
	 * This stores the client threads
	 */
	private Vector<AdminServerThread> connections = new Vector<AdminServerThread>();
	
	/**
	 * the private constructor of this class
	 */
	private AdminServer()
	{
		
	}
	
	/**
	 * Return the object of this class
	 * 
	 * @return adminServer as AdminServer
	 */
	public static AdminServer getInstance()
	{
		return adminServer;
	}

	/**
	 * Send a broadcast message to the other administrators
	 * 
	 * @param psMessage as String
	 */
	public synchronized void broadcast(String psMessage)
	{
		for(int i = 0; i < connections.size(); i++)
		{
			connections.elementAt(i).send(psMessage);
		}
	}
	
	/**
	 * Close the connection with the administrators of this server
	 */
	public synchronized void closed()
	{
		for(int i = 0; i < connections.size(); i++)
		{
			connections.elementAt(i).closed();
		}
	}
	
	/**
	 * The thread starts here and handle the incoming connections and build a new thread for every client
	 */
	@Override
	public void run() 
	{
		try 
		{
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			sslServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(model.getAdminServerPort());
			
			do
			{
				if(loop)
				{
					try
					{
						clientSocket = (SSLSocket) sslServerSocket.accept();
						clientMustClosed = true;
						adminServerThread = new AdminServerThread(clientSocket);
						thread = new Thread(adminServerThread);
						this.connections.addElement(adminServerThread); 
						thread.start();
						openSockets++;
						
					}
					catch(Exception e)
					{
						
					}
				}
				else
				{
					synchronized(this)
					{
						try
						{
							this.wait();
						}
						catch(InterruptedException e)
						{
							
						}
					}
				}
			}while(loop || openSockets > 0);
			
			if(clientMustClosed) clientSocket.close();
		} 
		catch (IOException e) 
		{
			
		}
		System.out.println("Administrations Server is heruntergefahren!...");
	
		/*
		 * sagt dem Server das er aufwachen soll
		 */
		synchronized (CommandThread.getInstance()) 
		{
			CommandThread.getInstance().notify();
		}
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
		catch(NullPointerException | IOException e)
		{
			logger.fatal(e.getMessage());
		}
	}
	
	/**
	 * If a client disconnect this server, then it calls this method to say he is disconnected
	 * 
	 * @throws MyServerException as Exception
	 */
	public static void decrementOpenSockets() throws MyServerException
	{
		openSockets--;
		if(openSockets < 0)throw new MyServerException("openSockets is under 0");
	}
	
	/**
	 * Set the loop attribute to false
	 */
	public static void loopOff()
	{
		loop = false;
	}
	
	/**
	 * Return the actual number of connected clients to the server
	 * 
	 * @return openSockets as int
	 */
	public static int getOpenSockets()
	{
		return openSockets;
	}
	
	/**
	 * Return the status of the main loop
	 * 
	 * @return the loop as boolean
	 */
	public static boolean getLoop()
	{
		return loop;
	}
}
