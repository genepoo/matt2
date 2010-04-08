/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matt;

import java.sql.*;

/**
 *
 * @author Bryan
 */
public class DatabaseFixer
{

    public static void main(String[] args)
    {
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet r = null;
        Logger.log("Fixing database");
        try
        {
            conn = DBHelper.getConnection();
            s = conn.prepareStatement("select * from tunequeries order by id");
            r = s.executeQuery();
            while (r.next())
            {
                int id = r.getInt("id");
                System.out.println("Fixing record: "+ id);
                String query = r.getString("query");
                if (query.length() > 300)
                {
                    System.out.println("Truncaing...");
                    query = query.substring(0, 300);
                }

                // Is it definitely wrong?
                if (r.getInt("correct") == -2)
                {
                    continue;
                }
                ABCMatch match = null;
                // Is the correct one not set
                int correct = r.getInt("correct");
                if (correct == -1)
                {
                    ABCFinder finder = new ABCFinder();
                    finder.setSearchString(query);                    
                    finder.findFromIndex();
                    match = finder.getPq().peek();
                }
                if (correct >= 0)
                {
                    ABCFinder finder = new ABCFinder();
                    finder.setSearchString(query);                   
                    finder.findFromIndex();
                    for (int i = 0 ; i < correct ; i ++)
                    {
                        finder.getPq().poll();
                    }
                    match = finder.getPq().peek();                                        
                }
                PreparedStatement us = conn.prepareStatement("update tunequeries set tunepalid = ?, client = ?, ed = ?, normalEd = ? where id = ?");
                us.setString(1, match.getTunepalid());
                us.setString(2, "tunepal.org");
                us.setFloat(3, match.getEditDistance());
                us.setFloat(4, match.getEditDistance() / (float) query.length());
                us.setInt(5, id);
                us.execute();
                us.close();
            }
        }
        catch (Exception e)
        {
            Logger.log("Could not update the database ");
            e.printStackTrace();
        }
        DBHelper.safeClose(conn, s, r);
        System.out.println("Finished!");
    }
}
