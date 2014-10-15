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
 * Diese Klasse beschreibt das auslesen der startparameter oder settingsdatei 
 * und startet für jeden Client der sich mit dem Server verbinden will
 * einen eigenen Thread
 * 
 * @author Pascal Schäfer
 */

public class MyServer extends Thread
{

	/**
	 * Logger Objekt um das was passiert mit zu loggen
	 */
	private static Logger logger = Logger.getRootLogger();
	
	/**
	 * Zähler zum zählen von den verbundenen Clients zum Server
	 */
	private static int openSockets = 0;
	
	/**
	 * Diese Boolean Variable zeigt an ob der Server noch weitere Verbindungen entgegennehmen kann
	 */
	private static boolean loop = true;
	
	/**
	 * String Array welches die Startparameter enthält
	 */
	private String[] args = null;
	
	/**
	 * Objekt der den Client behandelt
	 */
	private ServerThread serverThread = null;
	
	/**
	 * Objekt der Klasse Model
	 */
	private Model model;
	
	/**
	 * Objekt der die Verbindung zum Client aufbaut
	 */
	private SSLSocket clientSocket = null;
	
	/**
	 * Objekt welches die Verbindung vom Client entgegennimmt
	 */
	private SSLServerSocket sslServerSocket = null;
	
	/**
	 * Diese Boolean Variable zeigt an ob der Client geschlossen werden muss
	 */
	private boolean clientMustClosed = false;

	
	/**
	 * Konstruktor der Klasse MyServer
	 * 
	 * @param pasArgs als String[]
	 * @param pmModel als Model
	 */
	public MyServer(String[] pasArgs, Model pmModel) 
	{
		this.args = pasArgs;
		this.model = pmModel;
	}

	/**
	 * Startpunkt des Programms die Main methode wo die Startparameter oder die Settingsdatei ausgelesen wird
	 * und anfragen von den Clients angenommen wird
	 * 
	 * @param args als String array für die startparameter übergabe
	 */
	public static void main(String[] args) 
	{
		Model model = new Model();
		MyServer start = new MyServer(args,model);
		start.start();
	}
	
	/**
	 * startet den Server und aus diesem Prozess werden auch alle anderen Prozesse gestartet
	 */
	public void run()
	{
		
		CommandThread commandthread = new CommandThread(model, this);
		commandthread.start();
		
		try
		{
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			sslServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(20500);
			
			
			PatternLayout layout = new PatternLayout();
			HTMLLayout layout2 = new HTMLLayout();
			FileAppender fileAppender = new FileAppender(layout, "logs\\ServerLOG.log", false); // true = anhängen | false = datei überschreiben
			FileAppender fileAppender2 = new FileAppender(layout2, "logs\\ServerHTML.log", false);
			logger.addAppender(fileAppender2);
			logger.addAppender(fileAppender);
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
			logger.setLevel(Level.ALL);
			
			XMLSetting xml = new XMLSetting();
			


			if(args.length > 0)
			{
				for(int i = 0; i < args.length;i++)
				{
					System.out.println("Startparameter " + i + " :" +args[i]);
				}
				logger.info("hat Startparameter");
				if((args[0].equals("-f") && args[1].equals("n")) && (args[0] != null && args[1] != null))
				{
					System.out.println("Keine Settings Datei");
					if(args[2].equals("-L") && (args[2] != null && args[3] != null))
					{
						model.setMaxLimit(Integer.parseInt(args[3]));
						System.out.println("Limit: " + model.getMaxLimit());
					}
					if((args[4].equals("-xml") && (args[5] != null) && (args[6].equals("-xsd") && args[7] != null)))
					{
						model.setXMLPath(args[5]);
						model.setXSDPath(args[7]);
					}
				}
				else if((args[0].equals("-f") && ((args[1].equals("y") || args[1].equals("Y")) || (args[1].equals("j") || args[1].equals("J")))) && (args[0] != null && args[1] != null))
				{
					if(args[2].equals("-XML") && (args[2] != null && args[3] != null) && (args[4].equals("-XSD") && ( args[4] != null && args[5] != null)))
					{
						xml.XMLSettings(args[3], args[5]);
						model.setMaxLimit(xml.getLimit());
						model.setXMLPath(xml.getXMLPfad());
						model.setXSDPath(xml.getXSDPFad());
					}
				}
			}
			else
			{
				logger.info("hat keine Startparameter");
			}

			System.out.println("Server run...");
			if(model.getMaxLimit() > 100)
			{
				throw new IllegalLimitException("Limit is to High! must be under 100");
			}
			
			
			
			do
			{
				System.out.println("Loop: " + loop);
				if(loop)
				{
					try
					{
						clientSocket = (SSLSocket) sslServerSocket.accept();
						clientMustClosed=true;
						serverThread = new ServerThread(clientSocket, model.getXMLPath(), model.getXSDPath(),this, model);
						serverThread.start();
						openSockets++;
						System.out.println("aktuelle Verbindungen: " + openSockets);
					}
					catch(Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
				else
				{
					synchronized (this) 
					{
						try 
						{
							logger.debug("MyServer Thread Wartet nun...");
							this.wait();
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
				}
				
			}while(loop || openSockets > 0);
			
			if(clientMustClosed) clientSocket.close();
		}
		catch (IOException | SAXException | ParserConfigurationException | IllegalLimitException e) 
		{
			logger.fatal(e.getMessage());
		}
		System.out.println("Server is heruntergefahren!...");
	}

	/**
	 * Zerstört das SSLServerSocket Objekt
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
	 * mindert das Attribute openSockets um 1
	 * 
	 * @throws MyServerException
	 */
	public static void decrementOpenSockets() throws MyServerException
	{
		openSockets--;
		if(openSockets < 0)throw new MyServerException("openSockets is under 0");
	}
	
	/**
	 * setzt das Attribute loop auf false
	 */
	public static void loopOff()
	{
		loop = false;
	}
	
	/**
	 * gibt den inhalt von der statischen variable openSockets zurück
	 * 
	 * @return openSockets als Integer
	 */
	public static int getOpenSockets()
	{
		return openSockets;
	}
	
	/**
	 * gibt den status der schleife zurück
	 * 
	 * @return loop als boolean
	 */
	public static boolean getLoop()
	{
		return loop;
	}

}
