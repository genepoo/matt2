/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.sql.*;
/**
 *
 * @author Bryan Duggan
 */
public class DBHelper {
    static
    {
        try
        {
            Class.forName(MattProperties.getString("dbdriver"));
        }
        catch(Exception e)
        {
            Logger.log("Could not load driver");
            e.printStackTrace();
        }
                
    }
    
    public static Connection getConnection()
    {         
        try
        {
            Connection conn = DriverManager.getConnection(MattProperties.getString("dburl"), MattProperties.getString("dbuser"), MattProperties.getString("dbpassword"));
            return conn;            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.log("Caould not get a database connection");
        }        
        return null;
    }
    
    public static void safeClose(Connection c, Statement s, ResultSet r) {
        if (r != null) {
            try {
                r.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (s != null) {
            try {
                s.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (c != null) {
            try {
                c.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }             
    }
}
