package com.Netzwerk.de;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

/**
 * Die Klasse ServerThread beschreibt das Verhalten des Servers auf eine Aufgebaute Verbindung mit einem Client,
 * dieser Client kann befehle zum Server Schicken wie:<br>
 * 
 * -getip by name typ<br>
 * -getip by name<br>
 * -getip save typ<br>
 * -getip balance typ<br>
 * -gettyp all<br>
 * -gettyp typ<br>
 * -help<br>
 * -exit (zum Schließen der Verbindung)<br>
 * 
 * @author Pascal Schäfer
 *
 */

public class ServerThread extends Thread
{
	/**
	 * Socket vom Client
	 */
	private SSLSocket client;
	/**
	 * IP Adresse welche zurück geschickt wird wenn die Anfrage des Clients erfoglreich lief ansonsten kommt eine andere Antowrt zurück
	 */
	private String ip;
	/**
	 * Gibt das Limit an welches gesetzt wurde zum Aussortieren der Server
	 */
//	private int limit;
	/**
	 * Logger Objekt zum Loggen von Informationen
	 */
	private static Logger logger = Logger.getLogger(ServerThread.class);
	/**
	 * speichert den übergebenen XML Pfad
	 */
	private String xmlPath;
	/**
	 * speichert den übergebeben XSD Pfad
	 */
	private String xsdPath;
	
	/**
	 * speichert das übergebene Objekt MyServer
	 */
	private MyServer myServer;
	
	private Model model;
	

	
	/**
	 * Konstruktor der Klasse ServerThread setzt die Attribute auf die Übergebenen Werte
	 * 
	 * @param psslClientSocket als Socket
	 * @param psLimit als Int
	 * @param psXMLPath 
	 */
	public ServerThread(SSLSocket psslClientSocket, String psXMLPath, String psXSDPath, MyServer poMyServer, Model poModel)
	{
		
		logger.info("Anfang von ServerThread2 Konstruktor");
		this.client = psslClientSocket;
		this.xmlPath = psXMLPath;
		this.xsdPath = psXSDPath;
		this.myServer = poMyServer;
		this.model = poModel;

		logger.info("Ende von ServerThread2 Konstruktor");
	}
	
	/**
	 * Versucht die Methode getIPbyName oder getIPbyNameTyp aus Auswerten aufzurufen um eine IP Adresse zu erhalten und schickt diese zum Client zurück
	 * 
	 * @param poaAsw als Auswerten
	 * @param pstTokenizer als StringTokenizer
	 * @param ppwOut als PrintWriter
	 */
	private void by(Auswerten poaAsw, StringTokenizer pstTokenizer, PrintWriter ppwOut)
	{
		logger.info("Anfang Methode by");
		if(pstTokenizer.hasMoreTokens())
		{
				String serverName = pstTokenizer.nextToken();
				if(pstTokenizer.hasMoreTokens())
				{
					String serverTyp = pstTokenizer.nextToken();			
					String sIP = poaAsw.getIPbyNameTyp(serverName,serverTyp);
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
	 * Versucht die Methode save aus Auswerten aufzurufen um eine IP Adresse zu erhalten und schickt diese zum Client zurück
	 * 
	 * @param poaAsw als Auswerten
	 * @param pstTokenizer als StringTokenizer
	 * @param ppwOut als PrintWriter
	 */
	private void save(Auswerten poaAsw, StringTokenizer pstTokenizer, PrintWriter ppwOut)
	{
		logger.info("Anfang Methode save");
		if(pstTokenizer.hasMoreTokens())
		{
				String typ = pstTokenizer.nextToken();
			try 
			{
				int iTyp = Integer.parseInt(typ);
				poaAsw.setStrategy(new Save(iTyp,model.getMaxLimit()));
				ip = poaAsw.chooseServer();
				ppwOut.println(ip);
			} 
			catch ( NumberFormatException e)
			{
				logger.debug("Parse vom String zum int war nicht erfolgreich");
				ppwOut.println("the typ must be a number");
			}
		}
		else
		{
			ppwOut.println("getip save <0|1|2|3|..>");
		}
		logger.info("Ende Methode save");
	}
	
	/**
	 * Versucht die Methode balance aus Auswerten aufzurufen um eine IP Adresse zu erhalten und schickt diese zum Client zurück
	 * 
	 * @param poaAsw als Auswerten
	 * @param pstTokenizer als StringTokenizer
	 * @param ppwOut als PrintWriter
	 */
	private void balance(Auswerten poaAsw, StringTokenizer pstTokenizer, PrintWriter ppwOut)
	{
		logger.info("Anfang Methode balance");
		if(pstTokenizer.hasMoreTokens())
		{
			try 
			{
				String typ = pstTokenizer.nextToken();
				int iTyp = Integer.parseInt(typ);
				poaAsw.setStrategy(new Balance(iTyp));
				ip = poaAsw.chooseServer();
				ppwOut.println(ip);
			} 
			catch ( NumberFormatException e)
			{
				ppwOut.println("the typ must be a number");
			}
		}
		else
		{
			ppwOut.println("getip balance  <0|1|2|3|..>");
		}
		logger.info("Ende Methode balance");
	}
	
	/**
	 * Wählt je nach entegegen genommen Befehls eine Methode aus 
	 *
	 * @param poaAsw als Auswerten
	 * @param pstTokenizer als StringTokenizer
	 * @param ppwOut als PrintWriter
	 */
	private void ip(Auswerten poaAsw, StringTokenizer pstTokenizer, PrintWriter ppwOut)
	{
		logger.info("Anfang Methode ip");
		if(pstTokenizer.hasMoreTokens())
		{
			String next = pstTokenizer.nextToken();
			next = next.toLowerCase();
			
				if(next.equals("by"))
				{ 
					this.by(poaAsw,pstTokenizer,ppwOut);
				}
				else if(next.equals("server"))
				{
					poaAsw.setStrategy(model.getStrategy());
					ip = poaAsw.chooseServer();
					ppwOut.println(ip);
				}
				else if(next.equals("balance") && model.getAdvancedClient() == true)
				{	
					this.balance(poaAsw,pstTokenizer,ppwOut);
				}
				else if(next.equals("save") && model.getAdvancedClient() == true)
				{
					this.save(poaAsw,pstTokenizer,ppwOut);
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
		logger.info("Ende Methode ip");
	}
	
	/**
	 * gibt den Client alle vorhandenen Server Typen zurück 
	 * oder guckt ob es eines von denen gibt je nach Befehl und schickt eine Antwort zum Client
	 * 
	 * @param poaAsw als Auswerten
	 * @param pstTokenizer als StringTokenizer
	 * @param ppwOut als PrinterWriter
	 */
	private void typ(Auswerten poaAsw, StringTokenizer pstTokenizer, PrintWriter ppwOut)
	{
		logger.info("Anfang Methoden typ");
		if(pstTokenizer.hasMoreTokens())
		{
			String typ = pstTokenizer.nextToken();
			typ = typ.toLowerCase();
			if(typ.equals("all"))
			{
				String sAusgabe = "";
				String[] tmp = poaAsw.getKeys();
				for(int i = 0; i < tmp.length;i++)
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
		logger.info("Ende Methode typ");
	}
	
	/**
	 * Diese Methode wählt je nach eingekommer Nachricht eine Methode aus, um den entgegen genommen Befehl zu bearbeiten.
	 * 
	 * @param poaAsw als Auswerten
	 * @param pstTokenizer als StringTokenizer
	 * @param pbWeiter als boolean
	 * @param ppwOut als PrintWriter
	 * @return weiter als boolean
	 * @throws IOException durch schließen des Clients Sockets
	 */
	private boolean chooseMethod(Auswerten poaAsw, StringTokenizer pstTokenizer, boolean pbWeiter, PrintWriter ppwOut) throws IOException
	{
		pbWeiter = true;
		logger.info("Anfang Methode waehleMethode");
		if(pstTokenizer.hasMoreTokens())
		{
			String next = pstTokenizer.nextToken();
			if(next.equals("exit"))
			{
				ppwOut.println("closed");
				client.close();
				pbWeiter = false;
				return pbWeiter;
			}
			else if(next.equals("getip"))
			{
				this.ip(poaAsw,pstTokenizer,ppwOut);
			}
			else if(next.equals("gettyp"))
			{
				this.typ(poaAsw,pstTokenizer,ppwOut);
			}
			else if(next.equals("help"))
			{
				ppwOut.println("<getip|gettyp>");
			}
			else
			{
				ppwOut.println("cannot found this command");
			}
		}
		logger.info("Ende Methode waehleMethode");
		return pbWeiter;
		
	}
	
	/**
	 * Nachricht vom Client wird gelesen und weiterverarbeitet um den richtigen Befehl auszuführen
	 */
	public void run()
	{
		logger.info("Anfang Methode run()");

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(),true);
			
			Auswerten asw = new Auswerten();
			
			asw.readXMLFileWithXSD(xmlPath, xsdPath);
			boolean weiter = true;
			
			while(weiter)
			{
				String nachricht = in.readLine();
				
				StringTokenizer tokenizer = new StringTokenizer(nachricht);

				weiter = this.chooseMethod(asw,tokenizer,weiter,out);
			}
			
			MyServer.decrementOpenSockets();
			
			if(MyServer.getOpenSockets() == 0 && MyServer.getLoop() == false)
			{
				synchronized (myServer) 
				{
					System.out.println("\n\nweckt myServer\n\n");
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
				if(MyServer.getOpenSockets() == 0)
				{
					synchronized (myServer) 
					{
						logger.debug("weckt MyServer auf");
						myServer.notify();
					}
				}
			} catch (MyServerException e1) 
			{
				e1.printStackTrace();
			}
		}
		
		logger.info("Ende Methode run()");
	}


}
