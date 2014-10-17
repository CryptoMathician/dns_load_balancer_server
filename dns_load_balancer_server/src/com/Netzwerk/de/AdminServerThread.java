package com.Netzwerk.de;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.StringTokenizer;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;


/**
 * This class describe a thread which handle the connected administrator client
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
public class AdminServerThread extends Object implements Runnable
{
	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger(AdminServerThread.class);
	
	/**
	 * Object of the class Model
	 */
	private Model model = Model.getInstance();
	
	/**
	 * Socket to the client
	 */
	private SSLSocket clientSocket = null;
	
	/**
	 * BufferedReader to read the incoming commands
	 */
	private BufferedReader bufin = null;
	
	/**
	 * BufferedWriter to write answers or messages to the client
	 */
	private BufferedWriter bufout = null;
	
	/**
	 * DataInputStream to read the bytes from the client message
	 */
	private DataInputStream datain = null;
	
	/**
	 * DataOutputStream to write bytes to the client
	 */
	private DataOutputStream dataout = null;
	
	/**
	 * The charset to coding the characters
	 */
	private String charset = "UTF-8";
	
	/**
	 * Object of the DBController class
	 */
	private DBController dbc = DBController.getInstance();
	
	/**
	 * Object of the SecureController class
	 */
	private SecureController sc = SecureController.getInstance();
	
	/**
	 * Object of the AdminServer class
	 */
	private AdminServer as = AdminServer.getInstance();
	
	/**
	 * Random generator object
	 */
	private Random random = new Random();
	
	/**
	 * The loop of the client
	 */
	private boolean again = true;
	
	/**
	 * The Public Constructor of this class
	 * 
	 * @param psslsoClientSocket as SSLSocket
	 */
	public AdminServerThread(SSLSocket psslsoClientSocket)
	{
		this.clientSocket = psslsoClientSocket;
	}
	
	/**
	 * Set the limit
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IOException as Exception
	 */
	private void setMaxLimit(StringTokenizer pstTokenizer) throws IOException
	{
		String next = pstTokenizer.nextToken();
		try
		{
			int iLimit = Integer.parseInt(next);
			if(iLimit > 100)
			{
				this.bufout.write("limit is to High! must be under 100\n");
				this.bufout.flush();
			}
			else
			{
				model.setMaxLimit(iLimit);
				this.bufout.write("new limit is now " + iLimit + "\n");
				this.bufout.flush();
			}
		}
		catch(NumberFormatException e)
		{
			this.bufout.write("setLimit <0|...|100>\n");
			this.bufout.flush();
		}
	}
	
	/**
	 * Set the path where is stored the server informations
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IOException as Exception
	 */
	private void setXMLPath(StringTokenizer pstTokenizer) throws IOException
	{
		String pfad = pstTokenizer.nextToken();
		model.setXMLPath(pfad);
		this.bufout.write("New XML path is " + pfad + "\n");
		this.bufout.flush();
	}
	
	/**
	 * Set the path to the XSD file
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IOException as Exception
	 */
	private void setXSDPath(StringTokenizer pstTokenizer) throws IOException
	{
		String pfad = pstTokenizer.nextToken();
		model.setXSDPath(pfad);
		this.bufout.write("New Schema path is " + pfad + "\n");
		this.bufout.flush();
	}
	
	/**
	 * Set the modus
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IOException as Exception
	 */
	private void setModus(StringTokenizer pstTokenizer) throws IOException
	{
		String modus = pstTokenizer.nextToken();
		if(pstTokenizer.hasMoreTokens())
		{
			String typ = pstTokenizer.nextToken();
			
			if(modus.equals("balance"))
			{
				try
				{
					int iTyp = Integer.parseInt(typ);
					model.setStrategy(new Balance(iTyp));
					this.bufout.write("ok\n");
					this.bufout.flush();
				}
				catch(NumberFormatException e)
				{
					this.bufout.write("Typ is not ok\n");
					this.bufout.flush();
				}
			}
			else if(modus.equals("save"))
			{
				try
				{
					int iTyp = Integer.parseInt(typ);
					model.setStrategy(new Save(iTyp,model.getMaxLimit()));
					this.bufout.write("ok\n");
					this.bufout.flush();
				}
				catch(NumberFormatException e)
				{
					this.bufout.write("Typ is not ok\n");
					this.bufout.flush();
				}
			}
			else
			{
				this.bufout.write("setmodus <balance|save> <0,1,2,3,...>\n");
				this.bufout.flush();
			}
		}
		else
		{
			this.bufout.write("setmodus <balance|save> <0,1,2,3,...>\n");
			this.bufout.flush();
		}
		
	}

	/**
	 * Set advanced client option on/true or off/false
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IOException as Exception
	 */
	private void setAdvancedClient(StringTokenizer pstTokenizer) throws IOException
	{
		String enabled = pstTokenizer.nextToken();
		model.setAdvancedClient(enabled.equals("true"));
		this.bufout.write("ok\n");
		this.bufout.flush();
	}
	
	/**
	 * This method choose the next method to call
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @throws IOException as Exception
	 */
	private void chooseCommand(StringTokenizer pstTokenizer) throws IOException
	{	
		if(pstTokenizer.hasMoreTokens())
		{
			String nextCommand = pstTokenizer.nextToken();
			
			if(nextCommand.equals("halt"))
			{
				this.bufout.write("closed\n");
				this.bufout.flush();
				this.clientSocket.close();
				AdminServer.getInstance().closed();
				this.again = false;
			}
			else if(nextCommand.equals("halt"))
			{
				this.bufout.write("Server shutdown...\n");
				this.bufout.flush();
				AdminServer.loopOff();
				as.destroySSLServerSocketAccept();
				as.closed();
				this.again = false;
			}
			else if(nextCommand.equals("broadcast"))
			{
				String message = "";
				String inCommand = "";
				boolean readText = true;
				do
				{
					inCommand = this.bufin.readLine();
					
					if(inCommand.equals("QUIT"))
					{
						readText = false;
					}
					else
					{
						message += inCommand;
					}
				}while(readText);
				
				this.as.broadcast(message);
			}
			else if(nextCommand.equals("setmaxlimit"))
			{
				this.setMaxLimit(pstTokenizer);
			}
			else if(nextCommand.equals("getmaxlimit"))
			{
				this.bufout.write(model.getMaxLimit() + "\n");
				this.bufout.flush();
			}
			else if(nextCommand.equals("setxmlpath"))
			{
				this.setXMLPath(pstTokenizer);
			}
			else if(nextCommand.equals("getxmlpath"))
			{
				this.bufout.write(model.getXMLPath() + "\n");
				this.bufout.flush();
			}
			else if(nextCommand.equals("setschemapath"))
			{
				this.setXSDPath(pstTokenizer);
			}
			else if(nextCommand.equals("getschemapath"))
			{
				this.bufout.write(model.getXSDPath()+"\n");
				this.bufout.flush();
			}
			else if(nextCommand.equals("setmodus"))
			{
				this.setModus(pstTokenizer);
			}
			else if(nextCommand.equals("getmodus"))
			{
				IStrategy strategy = model.getStrategy();
				this.bufout.write(strategy.getInfo() + " " + strategy.getTyp()+"\n");
				this.bufout.flush();
			}
			else if(nextCommand.equals("setadvancedclient"))
			{
				this.setAdvancedClient(pstTokenizer);
			}
			else if(nextCommand.equals("getadvancedclient"))
			{
				this.bufout.write(model.getAdvancedClient()+"\n");
				this.bufout.flush();
			}
			else if(nextCommand.equals("addadmin"))
			{
				this.bufout.write("Do you would like to add a new admin? (yes | no):\n");
				this.bufout.flush();
				String adding = this.bufin.readLine();
				adding = adding.toLowerCase();
				
				if(adding.equals("yes") || adding.equals("Yes") || adding.equals("YEs") || adding.equals("YES") || adding.equals("yEs") || adding.equals("yeS") || adding.equals("YeS") || adding.equals("Y") || adding.equals("y"))
				{
					byte[] salt;
					try 
					{
						this.bufout.write("Username: \n");
						this.bufout.flush();
						String username = this.bufin.readLine();
						
						this.bufout.write("Password: \n");
						this.bufout.flush();
						String password = this.bufin.readLine();
						
						this.bufout.write("Password: \n");
						this.bufout.flush();
						String password2 = this.bufin.readLine();
						
						if(password.equals(password2))
						{
							salt = sc.generateSalt();
							SecretKey SKPassword = sc.passwordToHash(password.toCharArray(), salt);
							
							/*
							 * Key (password as Hash) muss in base64 codiert werden damit es in der Datenbank gespeichert werden kann
							 * Salt muss auch in base64 codiert werden damit es gespeichert werden kann
							 */
							dbc.addAdmin(username, "", Base64.getEncoder().encodeToString(SKPassword.getEncoded()) , Base64.getEncoder().encodeToString(salt));
							
							this.bufout.write("admin would be added.\n");
							this.bufout.flush();
						}
						else
						{
							this.bufout.write("The second password is not equal to password one\n");
							this.bufout.flush();
						}
						
						
					} 
					catch (NoSuchAlgorithmException e) 
					{
						logger.debug("addadmin: " + e.getMessage());
					}
				}
				else
				{
					this.bufout.write("Sorry, could not add a new Administrator!\n");
					this.bufout.flush();
				}
			}
			else if(nextCommand.equals("chadmpw"))
			{
				this.bufout.write("Do you would like to change the password of an Administrator? (yes | no): \n");
				this.bufout.flush();
				
				String answer = this.bufin.readLine();
				
				if(answer.equals("yes") || answer.equals("Yes") || answer.equals("YEs") || answer.equals("YES") || answer.equals("yEs") || answer.equals("yeS") || answer.equals("YeS") || answer.equals("Y") || answer.equals("y"))
				{
					this.bufout.write("Username: \n");
					this.bufout.flush();
					String username = this.bufin.readLine();
					
					this.bufout.write("old password: \n");
					this.bufout.flush();
					String oldpw = this.bufin.readLine();
					
					this.bufout.write("new Password: \n");
					this.bufout.flush();
					String password1 = this.bufin.readLine();
					
					this.bufout.write("new password again: \n");
					this.bufout.flush();
					String password2 = this.bufin.readLine();
					
					username = username.toLowerCase();
					oldpw = oldpw.toLowerCase();
					password1 = password1.toLowerCase();
					password2 = password2.toLowerCase();
					
					byte[] oldsalt = Base64.getDecoder().decode(dbc.getSalt(username));
					String oldpwdb = dbc.getPassword(username);
					
					SecretKey oldSKPassword = sc.passwordToHash(oldpw.toCharArray(), oldsalt);
					/*
					 * prüft das alte passwort auf richtigkeit
					 */
					if(oldpwdb.equals(Base64.getDecoder().decode(oldSKPassword.getEncoded())))
					{
						/*
						 * prüft ob das neue 2 mal gleich eingegeben wurde
						 */
						if(password1.equals(password2))
						{
							try 
							{
								byte[] salt = sc.generateSalt();
								SecretKey SKPassword = sc.passwordToHash(password1.toCharArray(), salt);
								
								/*
								 * encode the password hash to base64 string
								 */
								dbc.setPassword(username, Base64.getEncoder().encodeToString(SKPassword.getEncoded()));
								
								this.bufout.write("the password from the user " + username + " would be change!\n");
								this.bufout.flush();
							} 
							catch (NoSuchAlgorithmException e) 
							{
								logger.debug("set password: " + e.getMessage());
							}
						}
						else
						{
							this.bufout.write("the passwords are not equal!\nTry it again.\n");
							this.bufout.flush();
						}
					}
					else
					{
						this.bufout.write("the old password is false!\nTry it again.\n");
						this.bufout.flush();
					}
				}
				else
				{
					this.bufout.write("Sorry, could not change the password!\n");
					this.bufout.flush();
				}
			}
			else
			{
				
			}
		}
	}
	
	/**
	 * This methode send a message to the client
	 * 
	 * @param psMessage as String
	 */
	public void send(String psMessage)
	{
		try 
		{
			this.bufout.write(psMessage + "\n");
			this.bufout.flush();
		} 
		catch(IOException e) 
		{
			logger.debug("Broadcast: " + e.getMessage());
		}
	}
	
	/**
	 * This methode close the connection to the client
	 */
	public void closed()
	{
		try
		{
			this.clientSocket.shutdownInput();
			this.bufout.write("close\n");
			this.bufout.flush();
			this.clientSocket.close();
			this.again = false;
		}
		catch(IOException e)
		{
			logger.debug("closed: " + e.getMessage());
		}
	}
	
	/**
	 * The login method to login in this server
	 * 
	 * @param pstTokenizer as StringTokenizer
	 * @return login as boolean
	 * @throws IOException as Exception
	 */
	private boolean login(StringTokenizer pstTokenizer) throws IOException
	{
		boolean login = false;
		
		if(pstTokenizer.hasMoreTokens())
		{
			String cLogin = pstTokenizer.nextToken();
			
			if(cLogin.equals("login"))
			{
				String username = pstTokenizer.nextToken();
				
				byte[] passwordHash = Base64.getDecoder().decode(this.dbc.getPassword(username));
				byte[] salt = Base64.getDecoder().decode(this.dbc.getSalt(username));
				
				String rNumber = String.valueOf(random.nextInt(1000));
				
				this.bufout.write(rNumber+"\n");
				this.bufout.flush();
				
				this.send(salt);
				
				byte[] encryptedRNumber = this.receive();
				byte[] iv = this.receive();
				
				String decryptRNumber = this.sc.decryptMessage(passwordHash, encryptedRNumber, iv, salt);
				
				if(!decryptRNumber.equals(""))
				{
					if(rNumber.equals(decryptRNumber))
					{
						this.bufout.write("true\n");
						this.bufout.flush();
						login = true;
					}
					else
					{
						this.bufout.write("false\n");
						this.bufout.flush();
						login = false;
					}
				}
				else
				{
					this.bufout.write("false\n");
					this.bufout.flush();
					login = false;
				}
			}
			else
			{
				this.bufout.write("EXIT\n");
				this.bufout.flush();
			}
		}
		return login;
	}
	
	/**
	 * This method sends bytes to the client
	 * 
	 * @param message as byte array
	 */
	private void send(byte[] message) 
	{
		try 
		{
			this.sendBytes(message,0,message.length);
		} 
		catch (IOException e) 
		{
			logger.fatal("send failed: " + e.getMessage());
		}
	}	
	
	/**
	 * This method sends bytes to the client
	 * 
	 * @param message as byte array
	 * @param start as int
	 * @param len as int
	 * @throws IOException as Exception
	 */
	private void sendBytes(byte[] message, int start, int len) throws IOException
	{
		/*
		 * check the length of the message
		 */
		if(len < 0)
		{
			throw new IllegalArgumentException("Negative length now allowed");
		}
		/*
		 * check the start point of the message to send
		 */
		if(start < 0 || start >= message.length)
		{
			throw new IndexOutOfBoundsException("Out of bounds: " + start);
		}
		
		/*
		 * send the length of the message to send
		 * and send the message trough the socket to the client
		 */
		this.dataout.writeInt(len);
		if(len > 0)
		{
			this.dataout.write(message,0,len);
		}
		
	}
	
	/**
	 * This method receive the bytes from the client
	 * 
	 * @return data as byte array
	 * @throws IOException as Exception
	 */
	private byte[] receive() throws IOException 
	{
		/*
		 * receive the length of the incoming data
		 */
		int len = this.datain.readInt();
		byte[] data = new byte[len];
		/*
		 * read the incoming data 
		 */
		if(len > 0)
		{
			this.datain.readFully(data);
		}
		return data;
	}
	
	/**
	 * This method handle the client
	 */
	@Override
	public void run() 
	{
		try 
		{
			this.bufin = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), Charset.forName(this.charset)));
			this.bufout = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream(), Charset.forName(this.charset)));
			
			this.datain = new DataInputStream(clientSocket.getInputStream());
			this.dataout = new DataOutputStream(clientSocket.getOutputStream());
			
			String login = this.bufin.readLine();
			login = login.toLowerCase();
			
			StringTokenizer stLogin = new StringTokenizer(login);
			
			boolean bLogin = this.login(stLogin);
			
			if(bLogin)
			{
				do
				{
					String command = this.bufin.readLine();
					command = command.toLowerCase();
					
					StringTokenizer tokenizer = new StringTokenizer(command);
					
					this.chooseCommand(tokenizer);
				}while(this.again);
				
				
				AdminServer.decrementOpenSockets();
				
				if(AdminServer.getOpenSockets() == 0 && AdminServer.getLoop() == false)
				{
					synchronized(as) 
					{
						as.notify();
					}
				}
			}
			else
			{
				this.bufout.write("Login failed\n");
				this.bufout.flush();
			}
		} 
		catch (IOException | MyServerException e) 
		{
			try
			{
				AdminServer.decrementOpenSockets();
				if(AdminServer.getOpenSockets() == 0)
				{
					synchronized(as) 
					{
						as.notify();
					}
				}
			}
			catch(MyServerException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
