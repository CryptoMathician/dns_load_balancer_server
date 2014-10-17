package com.Netzwerk.de;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * This class describe a Database handler to get from the database and set data to the database<br><br> 
 * 
 * @author Pascal Schäfer
 * @version 0.0.1
 */
public class DBController extends Object
{
	/**
	 * The logger object of this class
	 */
	private static Logger logger = Logger.getLogger(DBController.class);
	
	/**
	 * The singlton object of this class
	 */
	private static DBController dbc = new DBController();
	
	/**
	 * This object stores the database settings
	 */
	private DBSettings dbsettings = DBSettings.getInstance();
	
	/*
	 * MySQL attributes
	 */
	
	/**
	 * Object to store the connection to the database
	 */
	private Connection db_connection = null;
	
	/**
	 * With this object you can do SQL Querys
	 */
	private Statement statement = null;	
	
	/*
	 * End of MySQL attributes
	 */
	
	/**
	 * Private constructor of this class
	 */
	private DBController()
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.db_connection = DriverManager.getConnection("jdbc:mysql://" + dbsettings.getServer() + "/" + dbsettings.getDatabase(), dbsettings.getUsername(), dbsettings.getPassword());
			this.statement = this.db_connection.createStatement();
		} 
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) 
		{
			logger.fatal(e.getMessage());
		}
		/* ______________________________________________________________________________
		 * |			|			|			|		|			|					|
		 * |	Field	|	Type	|	Null	|	Key	|	Default	|	Extra			|
		 * |____________|___________|___________|_______|___________|___________________|
		 * |	id		|	int(11)	|	No		|	PRI	|	NULL	|	auto_increment	|
		 * |	username|	char(30)|	YES		|		|	NULL	|					|
		 * |identifier	|	char(50)|	YES		|		|	NULL	|					|
		 * |	password|	text	|	YES		|		|	NULL	|					|
		 * |	salt	|	text	|	YES		|		|	NULL	|					|
		 * |____________|___________|___________|_______|___________|___________________|
		 */
	}
	
	/**
	 * Return the singleton Object of this class
	 * 
	 * @return dbc as DBController
	 */
	public static synchronized DBController getInstance()
	{
		return dbc;
	}
	
	/**
	 * Add a new administrator to the database
	 * 
	 * @param psUsername as String
	 * @param psIdentifier as String
	 * @param psPassword as String
	 * @param psSalt as String
	 * @return db_ok as boolean
	 */
	public synchronized boolean addAdmin(String psUsername,String psIdentifier, String psPassword, String psSalt)
	{
		/*
		 * local variables begin
		 */
		
		boolean db_ok = false;
		boolean user_ok = true;
		ResultSet db_rs = null;
		int result = 0; 
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = false;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				/*
				 * result == 0, 0 Rows added
				 * result == 1, 1 Rows added
				 */
				result = this.statement.executeUpdate("insert into t_admins (username,identifier,password,salt) values ( '" + psUsername + "' , '" + psIdentifier + "' , '" + psPassword + "' , '" + psSalt + "' );"); 
				
				if(result == 1)
				{
					db_ok = true;
				}
				else
				{
					db_ok = false;
				}
			}
			else
			{
				db_ok = false;
			}
			
			logger.debug("addAdmin result: " + result);
		} 
		catch(SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
		}
		return db_ok;
	}
	
	/**
	 * Set the identifier from the user
	 * 
	 * @param psUsername as String
	 * @param psIdentifier as String
	 * @return db_ok as boolean
	 */
	public synchronized boolean setIdentifier(String psUsername,String psIdentifier)
	{
		/*
		 * local variables begin
		 */
		
		boolean db_ok = false;
		boolean user_ok = false;
		ResultSet db_rs = null;
		int result = 0; 
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = true;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				/*
				 * result == 0, 0 Rows added
				 * result == 1, 1 Rows added
				 */
				result = this.statement.executeUpdate("update t_admins set identifier = '" + psIdentifier + "' where username = '" + psUsername + "';"); 
				
				if(result == 1)
				{
					db_ok = true;
				}
				else
				{
					db_ok = false;
				}
			}
			else
			{
				db_ok = false;
			}
			
			logger.debug("set identifier result: " + result);
		} 
		catch(SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
		}
		return db_ok;
	}

	/**
	 * Set the password for the specific user
	 * 
	 * @param psUsername as String
	 * @param psPassword as String
	 * @return db_ok as boolean
	 */
	public synchronized boolean setPassword(String psUsername,String psPassword)
	{
		/*
		 * local variables begin
		 */
		
		boolean db_ok = false;
		boolean user_ok = false;
		ResultSet db_rs = null;
		int result = 0; // -1 ohne auswertung 0 ist schief gelaufen und 1 ist gut gelaufen
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = true;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				/*
				 * result == 0, 0 Rows added
				 * result == 1, 1 Rows added
				 */	
				result = this.statement.executeUpdate("update t_admins set password = '" + psPassword + "' where username = '" + psUsername + "';"); 
				
				if(result == 1)
				{
					db_ok = true;
				}
				else
				{
					db_ok = false;
				}
			}
			else
			{
				db_ok = false;
			}
			
			logger.debug("set password result: " + result);
		} 
		catch(SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
		}
		return db_ok;
	}
	
	/**
	 * Set the salt of the specific user
	 * 
	 * @param psUsername as String
	 * @param psSalt as String
	 * @return db_ok as boolean
	 */
	public synchronized boolean setSalt(String psUsername,String psSalt)
	{
		/*
		 * local variables begin
		 */
		
		boolean db_ok = false;
		boolean user_ok = false;
		ResultSet db_rs = null;
		int result = 0; 
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = true;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				/*
				 * result == 0, 0 Rows added
				 * result == 1, 1 Rows added
				 */
				result = this.statement.executeUpdate("update t_admins set salt = '" + psSalt + "' where username = '" + psUsername + "';"); 
				
				if(result == 1)
				{
					db_ok = true;
				}
				else
				{
					db_ok = false;
				}
			}
			else
			{
				db_ok = false;
			}
			
			logger.debug("set salt result: " + result);
		} 
		catch(SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
		}
		return db_ok;
	}
	
	/**
	 * Return the password as String
	 * 
	 * @param psUsername as String
	 * @return db_password as String
	 */
	public synchronized String getPassword(String psUsername)
	{
		/*
		 * local variables begin
		 */
		
		boolean user_ok = false;
		ResultSet db_rs_password = null;
		String db_password = null;
		ResultSet db_rs = null;
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = true;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				db_rs_password = this.statement.executeQuery("select password from t_admins where username = '" + psUsername + "';"); 
				
				while(db_rs_password.next())
				{
					db_password = db_rs_password.getString("password");
				}
			}
			else
			{
				db_password = null;
			}
			
			logger.debug("password result: " + db_password);
		} 
		catch (SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
			if(db_rs_password != null)
			{
				try
				{
					db_rs_password.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs_password = null;
			}
		}
		return db_password;
	}
	
	/**
	 * Return the salt of the specific user from the database
	 * 
	 * @param psUsername as String
	 * @return db_salt as String
	 */
	public synchronized String getSalt(String psUsername)
	{
		/*
		 * local variables begin
		 */
		
		boolean user_ok = false;
		ResultSet db_rs_salt = null;
		String db_salt = null;
		ResultSet db_rs = null;
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = true;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				db_rs_salt = this.statement.executeQuery("select salt from t_admins where username = '" + psUsername +"';"); 
				
				while(db_rs_salt.next())
				{
					db_salt = db_rs_salt.getString("salt");
				}
			}
			else
			{
				db_salt = null;
			}
			
			logger.debug("salt result: " + db_salt);
		} 
		catch (SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
			if(db_rs_salt != null)
			{
				try
				{
					db_rs_salt.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs_salt = null;
			}
		}
		
		return db_salt;
	}
	
	/**
	 * Return the identifier of a specific user from the database
	 * 
	 * @param psUsername as String
	 * @return db_identifier as String
	 */
	public synchronized String getIdentifier(String psUsername)
	{
		/*
		 * local variables begin
		 */
		
		boolean user_ok = false;
		ResultSet db_rs_identifier = null;
		String db_identifier = null;
		ResultSet db_rs = null;
		
		/*
		 * local variables end
		 */
		
		try 
		{
			/*
			 * get all the usernames in the table and store these result in db_rs
			 */
			db_rs = this.statement.executeQuery("select username from t_admins;");
			
			/*
			 * search the result to the specific username
			 */
			while(db_rs.next())
			{
				if(psUsername.equals(db_rs.getString("username")))
				{
					user_ok = true;
				}
			}
			
			/*
			 * If doesn't exists the user than you can add him
			 */
			if(user_ok)
			{
				db_rs_identifier = this.statement.executeQuery("select identifier from t_admins where username = '" + psUsername +"';"); 
				
				while(db_rs_identifier.next())
				{
					db_identifier = db_rs_identifier.getString("identifier");
				}
			}
			else
			{
				db_identifier = null;
			}
			
			logger.debug("identifier result: " + db_identifier);
		} 
		catch (SQLException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(db_rs != null)
			{
				try
				{
					db_rs.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs = null;
			}
			if(db_rs_identifier != null)
			{
				try
				{
					db_rs_identifier.close();
				}
				catch(SQLException e)
				{
					
				}
				db_rs_identifier = null;
			}
		}
		
		return db_identifier;
	}
	
	/**
	 * Close the database connection
	 */
	public synchronized void close()
	{
		if(this.statement != null)
		{
			try
			{
				this.statement.close();
			}
			catch(SQLException e)
			{
				
			}
			this.statement = null;
		}
		if(this.db_connection != null)
		{
			try
			{
				this.db_connection.close();
			}
			catch(SQLException e)
			{
				
			}
			this.db_connection = null;
		}
	}
}
