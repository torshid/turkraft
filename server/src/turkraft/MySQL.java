package turkraft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import turkraft.common.Logs;

public class MySQL
{
	private Connection sqlConnection = null;
    private Statement  sqlStatement  = null;
    
    private String connectionState;
    
    private Logs log;

    static
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (InstantiationException ex)
        {
            ex.printStackTrace();
        }
        catch (IllegalAccessException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }

    public MySQL(Logs log, String host, String base, String login, String password)
    {
    	this.log = log;
    	
    	this.connectionState = "jdbc:mysql://" + host + "/" + base + "?" + "user=" + login + "&password=" + password + "&zeroDateTimeBehavior=convertToNull";
    	
    	this.connect();
    }
    
    private void connect()
    {
        try
        {
	    	this.sqlConnection = DriverManager.getConnection(this.connectionState);

	    	this.sqlStatement = sqlConnection.createStatement();
        }
        catch (SQLException ex)
        {
        	Turkraft.noError = false;
        	
            this.log.error("Impossible to connect MySQL server with configuration: " + this.connectionState + ", stack trace: " + ex);
        }
    }

    public boolean ExecuteInsert(String Query)
    {
    	this.CheckConnection("INSERT error with query: " + Query);
    	
        try
        {
            return this.sqlStatement.execute(Query);
        }
        catch (SQLException ex)
        {
            this.log.error("INSERT error with query: " + Query + ", stack trace: " + ex);
            
            return false;
        }
    }

    public int ExecuteUpdate(String Query)
    {
    	this.CheckConnection("UPDATE error with query: " + Query);

        try
        {
            return this.sqlStatement.executeUpdate(Query);
        }
        catch (SQLException ex)
        {
            this.log.error("UPDATE error with query: " + Query + ", stack trace: " + ex);
            
            return -1;
        }
    }

    public ResultSet ExecuteSelect(String Query)
    {
    	this.CheckConnection("SELECT error with query: " + Query);
    	
        try
        {
            return this.sqlStatement.executeQuery(Query);
        }
        catch (SQLException ex)
        {
            this.log.error("SELECT error with query: " + Query + ", stack trace: " + ex);
            
            return null;
        }
    }

    public int ExecuteDelete(String Query)
    {
    	this.CheckConnection("DELETE error with query: " + Query);
    	
        try
        {
            return this.sqlStatement.executeUpdate(Query);
        }
        catch (SQLException ex)
        {
            this.log.error("DELETE error with query: " + Query + ", stack trace: " + ex);
            
            return -1;
        }
    }

    private void CheckConnection(String Error)
    {
    	try
    	{
    		if (this.sqlConnection == null || this.sqlConnection.isClosed() || this.sqlStatement.isClosed())
			{
				this.connect();
				
				return;
			}
		}
    	catch (SQLException ex)
		{
            this.log.error(Error + ", stack trace: " + ex);
		}
    }
    
    public int GetLastId(String table)
    {
    	try
    	{
    		ResultSet result = this.ExecuteSelect("SELECT id FROM " + table + " ORDER BY id DESC LIMIT 0,1");

    		result.next();
    		
    		return result.getInt("id");
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		
    		return 0;
    	}
    }
}