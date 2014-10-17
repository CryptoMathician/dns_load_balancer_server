package com.Netzwerk.de;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

/**
 * This class describe a thread of the server to control the server with
 * commands<br>
 * <br>
 * Commands:<br>
 * - setmaxlimit<br>
 * - getmaxlimit<br>
 * - setxmlpath<br>
 * - getxmlpath<br>
 * - setschemapath<br>
 * - getschemapath<br>
 * - halt (to shutdown the server)<br>
 * - chadmpw<br>
 * - addadmin<br>
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */

public class CommandThread extends Object implements Runnable
{
	/**
	 * Singleton object of this class
	 */
	private static CommandThread ct = new CommandThread();

	/**
	 * Stores the Model object
	 */
	private Model model = Model.getInstance();

	/**
	 * Stores MyServer object
	 */
	private MyServer myServer = MyServer.getInstance();

	/**
	 * Logger object of the class
	 */
	private static Logger logger = Logger.getLogger(CommandThread.class);

	/**
	 * SecureController to handel the security data
	 */
	private SecureController sc = SecureController.getInstance();

	/**
	 * DBController to handle the database data
	 */
	private DBController dbc = DBController.getInstance();

	/**
	 * Constructor of this class
	 */
	private CommandThread()
	{
	}

	/**
	 * Returns the object of this class
	 * 
	 * @return ct as CommandThread
	 */
	public static CommandThread getInstance()
	{
		return ct;
	}

	/**
	 * Set the maximum limit
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IllegalLimitException as Exception
	 */
	private void setMaxLimit(StringTokenizer pstTokenizer)
			throws IllegalLimitException
	{
		String next = pstTokenizer.nextToken();
		try
		{
			int iLimit = Integer.parseInt(next);
			if (iLimit > 100)
			{
				throw new IllegalLimitException(
						"limit is to High! must be under 100");
			}
			model.setMaxLimit(iLimit);
			System.out.println("new limit is now " + iLimit);
		}
		catch (NumberFormatException e)
		{
			System.out.println("setLimit <0|...|100>");
		}
	}

	/**
	 * Set the path to the XML file path
	 * 
	 * @param pstTokenizer as StringTokenizer
	 */
	private void setXMLPath(StringTokenizer pstTokenizer)
	{
		String pfad = pstTokenizer.nextToken();
		model.setXMLPath(pfad);
		System.out.println("New XML path is " + pfad);
	}

	/**
	 * Set the XSD file path
	 * 
	 * @param pstTokenizer as StringTokenizer
	 */
	private void setXSDPath(StringTokenizer pstTokenizer)
	{
		String pfad = pstTokenizer.nextToken();
		model.setXSDPath(pfad);
		System.out.println("New Schema path is " + pfad);
	}

	/**
	 * Set the modus
	 * 
	 * @param pstTokenizer as StringTokenizer
	 */
	private void setModus(StringTokenizer pstTokenizer)
	{
		String modus = pstTokenizer.nextToken().toLowerCase();
		if (pstTokenizer.hasMoreTokens())
		{
			String typ = pstTokenizer.nextToken().toLowerCase();

			if (modus.equals("balance"))
			{
				int iTyp = Integer.parseInt(typ);
				model.setStrategy(new Balance(iTyp));
			}
			else if (modus.equals("save"))
			{
				int iTyp = Integer.parseInt(typ);
				model.setStrategy(new Save(iTyp, model.getMaxLimit()));
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
	 * Set the status of advanced client active or disabled
	 * 
	 * @param pstTokenizer as StringTokenizer
	 */
	private void setAdvancedClient(StringTokenizer pstTokenizer)
	{
		String enabled = pstTokenizer.nextToken().toLowerCase();
		model.setAdvancedClient(enabled.equals("true"));
	}

	/**
	 * Method to choose a method
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @param posScan as Scanner
	 * @throws IOException as Exception
	 * @throws IllegalLimitException as Exception
	 * @return as boolean
	 */
	private boolean chooseCommand(StringTokenizer pstTokenizer, Scanner posScan) throws IllegalLimitException, IOException
	{
		if (pstTokenizer.hasMoreTokens())
		{
			String command = pstTokenizer.nextToken();
			command = command.toLowerCase();

			if (command.equals("halt"))
			{
				// System.out.println("Server shutdown...");
				// MyServer.loopOff();
				// AdminServer.getInstance().closed();
				// AdminServer.loopOff();
				// myServer.destroySSLServerSocketAccept();
				// AdminServer.getInstance().destroySSLServerSocketAccept();

				System.out.println("Server will be shutdown... ");
				AdminServer.getInstance().closed();
				AdminServer.loopOff();
				AdminServer.getInstance().destroySSLServerSocketAccept();

				synchronized (this)
				{
					try
					{
						this.wait();
					}
					catch (InterruptedException e)
					{

					}
				}

				MyServer.loopOff();
				myServer.destroySSLServerSocketAccept();

				System.out.println("Server shutdown now...");

				return false;

			}
			else if (command.equals("setmaxlimit"))
			{
				this.setMaxLimit(pstTokenizer);
			}
			else if (command.equals("getmaxlimit"))
			{
				System.out.println(model.getMaxLimit());
			}
			else if (command.equals("setxmlpath"))
			{
				this.setXMLPath(pstTokenizer);
			}
			else if (command.equals("getxmlpath"))
			{
				System.out.println(model.getXMLPath());
			}
			else if (command.equals("setschemapath"))
			{
				this.setXSDPath(pstTokenizer);
			}
			else if (command.equals("getschemapath"))
			{
				System.out.println(model.getXSDPath());
			}
			else if (command.equals("setmodus"))
			{
				this.setModus(pstTokenizer);
			}
			else if (command.equals("getmodus"))
			{
				IStrategy strategy = model.getStrategy();
				System.out
						.println(strategy.getInfo() + " " + strategy.getTyp());
			}
			else if (command.equals("setadvancedclient"))
			{
				this.setAdvancedClient(pstTokenizer);
			}
			else if (command.equals("getadvancedclient"))
			{
				System.out.println(model.getAdvancedClient());
			}
			else if (command.equals("addadmin"))
			{
				System.out
						.println("Do you would like to add a new admin? (yes | no): ");
				String adding = posScan.nextLine();
				adding = adding.toLowerCase();

				if (adding.equals("yes") || adding.equals("Yes")
						|| adding.equals("YEs") || adding.equals("YES")
						|| adding.equals("yEs") || adding.equals("yeS")
						|| adding.equals("YeS") || adding.equals("Y")
						|| adding.equals("y"))
				{
					byte[] salt;
					try
					{
						System.out.println("Username: ");
						String username = posScan.nextLine();

						System.out.println("Password: ");
						String password = new String(System.console()
								.readPassword());

						/*
						 * the admin don't see the password if he typed it in
						 * the console
						 */
						salt = sc.generateSalt();
						SecretKey SKPassword = sc.passwordToHash(
								password.toCharArray(), salt);

						/*
						 * Key (password as Hash) muss in base64 codiert werden
						 * damit es in der Datenbank gespeichert werden kann
						 * Salt muss auch in base64 codiert werden damit es
						 * gespeichert werden kann
						 */
						dbc.addAdmin(username, "", Base64.getEncoder().encodeToString(SKPassword.getEncoded()), Base64.getEncoder().encodeToString(salt));

						System.out.println("admin would be added.");

					}
					catch (NoSuchAlgorithmException e)
					{
						logger.debug("addadmin: " + e.getMessage());
					}
				}
				else
				{
					System.out
							.println("Sorry, could not add a new Administrator!");
				}
			}
			else if (command.equals("chadmpw"))
			{
				System.out
						.println("Do you would like to change the password of an Administrator? (yes | no): ");
				String answer = posScan.nextLine();

				if (answer.equals("yes") || answer.equals("Yes")
						|| answer.equals("YEs") || answer.equals("YES")
						|| answer.equals("yEs") || answer.equals("yeS")
						|| answer.equals("YeS") || answer.equals("Y")
						|| answer.equals("y"))
				{
					System.out.println("Username: ");
					String username = posScan.nextLine();

					System.out.println("old password: ");
					String oldpw = new String(System.console().readPassword());

					System.out.println("new Password: ");
					String password1 = new String(System.console()
							.readPassword());

					System.out.println("new password again: ");
					String password2 = new String(System.console()
							.readPassword());

					password1 = password1.toLowerCase();
					password2 = password2.toLowerCase();

					byte[] oldsalt = Base64.getDecoder().decode(
							dbc.getSalt(username));
					String oldpwdb = dbc.getPassword(username);

					SecretKey oldSKPassword = sc.passwordToHash(
							oldpw.toCharArray(), oldsalt);
					/*
					 * prüft das alte passwort auf richtigkeit
					 */
					if (oldpwdb.equals(Base64.getEncoder().encodeToString(
							oldSKPassword.getEncoded())))
					{
						/*
						 * prüft ob das neue 2 mal gleich eingegeben wurde
						 */
						if (password1.equals(password2))
						{
							try
							{
								byte[] salt = sc.generateSalt();
								SecretKey SKPassword = sc.passwordToHash(
										password1.toCharArray(), salt);

								/*
								 * encode the password hash to base64 string
								 */
								dbc.setPassword(
										username,
										Base64.getEncoder().encodeToString(
												SKPassword.getEncoded()));
								dbc.setSalt(username, Base64.getEncoder()
										.encodeToString(salt));

								System.out
										.println("the password from the user "
												+ username
												+ " would be change!");
							}
							catch (NoSuchAlgorithmException e)
							{
								logger.debug("set password: " + e.getMessage());
							}
						}
						else
						{
							System.out
									.println("the passwords are not equal!\nTry it again.");
						}
					}
					else
					{
						System.out
								.println("the old password is false!\nTry it again.");
					}
				}
				else
				{
					System.out.println("Sorry, could not change the password!");
				}
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
				System.out.println("addadmin\tadd a new Admin to the server\n");
				System.out.println("chadmpw\tchange the administrator account from a admin\n");
				System.out.println("halt\t\tto shutdown the server\n\n");
			}
		}

		return true;
	}

	/**
	 * this method handle the commands from the administrator
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
				weiter = this.chooseCommand(tokenizer, scan);
			}
			catch (IllegalLimitException | IOException e)
			{
				System.out.println("CommandThread run: " + e.getMessage());
			}
		} while (weiter);
		scan.close();
	}

}
