package com.Netzwerk.de;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Die Klasse CommandThread beschreibt einen Prozess von dem Server
 * um Ihn mit befehlen zu steuern<br><br>
 * 
 * Befehle:<br>
 * - setmaxlimit<br>
 * - getmaxlimit<br>
 * - setxmlpath<br>
 * - getxmlpath<br>
 * - setschemapath<br>
 * - getschemapath<br>
 * - exit (zum herunterfahren des Servers)<br>
 * 
 * @author Pascal Schäfer
 */

public class CommandThread extends Thread{

	/**
	 * Model Objekt 
	 */
	private Model model = null;
	
	/**
	 * MyServer Objekt	
	 */
	private MyServer myServer = null;
	
	/**
	 * Konstruktor
	 * 
	 * @param poModel als Model Objekt enthält Settingsdaten
	 */
	public CommandThread(Model poModel, MyServer poMyServer)
	{
		this.model = poModel;
		this.myServer = poMyServer;
	}
	
	
	/**
	 * setzt die obergrenze
	 * 
	 * @param pstTokenizer als StringTokenizer
	 * @throws IllegalLimitException
	 */
	private void setMaxLimit(StringTokenizer pstTokenizer) throws IllegalLimitException
	{
		String next = pstTokenizer.nextToken();
		try
		{
			int iLimit = Integer.parseInt(next);
			if(iLimit > 100)
			{
				throw new IllegalLimitException("limit is to High! must be under 100");
			}
			model.setMaxLimit(iLimit);
			System.out.println("new limit is now " + iLimit);
		}
		catch(NumberFormatException e)
		{
			System.out.println("setLimit <0|...|100>");
		}
	}
	
	/**
	 * setzt den Pfad der xml Datei wo die Server enthalten sind
	 * 
	 * @param pstTokenizer als StringTokenizer
	 */
	private void setXMLPath(StringTokenizer pstTokenizer)
	{
		String pfad = pstTokenizer.nextToken();
		model.setXMLPath(pfad);
		System.out.println("New XML path is " + pfad);
	}
	
	/**
	 * setzt den Pfad der Schema datei für die xml Datei
	 * 
	 * @param pstTokenizer als StringTokenizer
	 */
	private void setXSDPath(StringTokenizer pstTokenizer)
	{
		String pfad = pstTokenizer.nextToken();
		model.setXSDPath(pfad);
		System.out.println("New Schema path is " + pfad);
	}
	
	/**
	 * 
	 * setzt den Modus
	 * 
	 * @param pstTokenizer
	 */
	private void setModus(StringTokenizer pstTokenizer)
	{
		String modus = pstTokenizer.nextToken().toLowerCase();
		if(pstTokenizer.hasMoreTokens())
		{
			String typ = pstTokenizer.nextToken().toLowerCase();
			
			if(modus.equals("balance"))
			{
				int iTyp = Integer.parseInt(typ);
				model.setStrategy(new Balance(iTyp));
			}
			else if(modus.equals("save"))
			{
				int iTyp = Integer.parseInt(typ);
				model.setStrategy(new Save(iTyp,model.getMaxLimit()));
			}
			else
			{
				System.out.println("setmodus <balance|save> <0,1,2,3,...>");
			}
		}
		else
		{
			System.out.println("setmodus <balance|save> <0,1,2,3,...>");
		}
		
	}
	
	/**
	 * 
	 * setzt ob advancedClient aktiviert sein soll oder nicht
	 * 
	 * @param pstTokenizer
	 */
	private void setAdvancedClient(StringTokenizer pstTokenizer)
	{
		String enabled = pstTokenizer.nextToken().toLowerCase();
		model.setAdvancedClient(enabled.equals("true"));
	}
	
	/**
	 * Methode zum Auswählen der Methoden
	 * 
	 * @param pstTokenizer als StringTokenizer
	 * @throws IOException 
	 */
	private boolean chooseCommand(StringTokenizer pstTokenizer) throws IllegalLimitException, IOException
	{
		String command = pstTokenizer.nextToken();
		command = command.toLowerCase();
		
		if(command.equals("exit"))
		{
			System.out.println("Server shutdown...");
			MyServer.loopOff();
			myServer.destroySSLServerSocketAccept();

			return false;
			
		}
		else if(command.equals("setmaxlimit"))
		{
			this.setMaxLimit(pstTokenizer);
		}
		else if(command.equals("getmaxlimit"))
		{
			System.out.println(model.getMaxLimit());
		}
		else if(command.equals("setxmlpath"))
		{
			this.setXMLPath(pstTokenizer);
		}
		else if(command.equals("getxmlpath"))
		{
			System.out.println(model.getXMLPath());
		}
		else if(command.equals("setschemapath"))
		{
			this.setXSDPath(pstTokenizer);
		}
		else if(command.equals("getschemapath"))
		{
			System.out.println(model.getXSDPath());
		}
		else if(command.equals("setmodus"))
		{
			this.setModus(pstTokenizer);
		}
		else if(command.equals("getmodus"))
		{
			IStrategy strategy = model.getStrategy();
			System.out.println(strategy.getInfo() + " " + strategy.getTyp());
		}
		else if(command.equals("setadvancedclient"))
		{
			this.setAdvancedClient(pstTokenizer);
		}
		else if(command.equals("getadvancedclient"))
		{
			System.out.println(model.getAdvancedClient());
		}
		else 
		{
			System.out.println("\nHELP:\n");
			System.out.println("Command\t\tinformation to the command\n\n");
			System.out.println("setlimit\tto set the limit...\n");
			System.out.println("getlimit\tto get the limit...\n");
			System.out.println("setxmlpath\tto set the path from the xml file which contains the server\n");
			System.out.println("getxmlpath\tto get the path from the xml file which contains the server\n");
			System.out.println("setschemapath\tto set the path from the schema file which contains the schema for xml file\n");
			System.out.println("getschemapath\tto get the path from the schema file which contains the schema for xml file\n");
			System.out.println("setmodus\tto set the choose strategy/modus\n");
			System.out.println("getmodus\tto get the choose strategy/modus\n");
			System.out.println("setadvancedclient\tto allow the client, to use the getip save/balance 0/1/2/.../ command\n");
			System.out.println("getadvancedclient\tshow if the client allowed to call the special command getip <save|balance> <0|1|2>\n");
			System.out.println("exit\t\tto shutdown the server\n\n");
		}
		return true;
	}
	
	/**
	 * Diese Methode beschreibt den beginn eines eigenen Prozesses 
	 * der Befehle entgegen nimmt
	 */
	public void run()
	{
		Scanner scan = new Scanner(System.in);
		boolean weiter = true;
		do
		{
			String commands = scan.nextLine();
			
			StringTokenizer tokenizer = new StringTokenizer(commands);
			
			try 
			{
				weiter = this.chooseCommand(tokenizer);
			} 
			catch (IllegalLimitException | IOException e) 
			{
				System.out.println(e.getMessage());
			}
		}while(weiter);
		scan.close();
	}
	
}
