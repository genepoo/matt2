/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.sql.*;
import java.net.*;
import java.io.*;

/**
 *
 * @author Bryan Duggan
 */
public class MattServer extends Thread {
    private boolean running;
        
    public void run()
    {
        long serverThreadSleepTime = Long.parseLong(MattProperties.getP("serverThreadSleepTime"));
        Logger.log("Server staring...");
        running = true;
        while (running)
        {
            Logger.log("Waking up");        
            try 
            {
        
                Connection conn = DBHelper.getConnection();
                if (conn == null)
                {
                    break;
                }
                PreparedStatement s = conn.prepareStatement("select * from job where status = ?");
                s.setInt(1, BatchJob.NEW);
                ResultSet results = s.executeQuery();
                while (results.next())
                {
                    BatchJob job = new BatchJob();
                    job.setJobId(results.getLong("id"));
                    job.setServerMode(true);
                    String fileName = MattProperties.getP("tunometerPath") + System.getProperty("file.separator") + job.getJobId() + ".zip";
                    job.setFolder(new File(fileName));
                    job.start();
                }
                Logger.log("Going to sleep");                    
                try 
                {
                    Thread.sleep(serverThreadSleepTime);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();                    
                }
                DBHelper.safeClose(conn, s, results);                     
            }
            catch (SQLException e)
            {
                Logger.log("An SQL exception occured. Server shutting down");
                e.printStackTrace();
                running = false;
            }
            
        }
    }
    
    
    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }
}
