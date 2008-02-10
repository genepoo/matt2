/*
 * TFLog.java
 *
 * Created on 17 January 2007, 17:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import java.io.*;
/**
 *
 * @author bduggan
 */
public class TFLog {
    
    /** Creates a new instance of TFLog */
    public TFLog() {
    }
    
    public static void deleteLog(String fileName)
    {
        File f = new File(fileName);
        f.delete();
    }
    
    public static void TFLog(String fileName, String txt)
    {
        try
        {          
            FileWriter fw = new FileWriter(new File(fileName), true);
            fw.write(txt);
            fw.write(System.getProperty("line.separator"));
            fw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
}
