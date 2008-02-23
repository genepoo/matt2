/*
 * MattProperties.java
 *
 * Created on 17 July 2007, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import java.io.FileReader;
import java.util.*;
import java.io.*;

/**
 *
 * @author Bryan
 */
public class MattProperties extends Properties {    
    
    private static MattProperties instance = null;
    private static PropertiesLoader propertiesLoader = null; 
    /** Creates a new instance of MattProperties */
    private MattProperties() {
    }
    
    public static String getP(String key)
    {
        return "" + instance().get(key);
    }
    
    public static MattProperties instance()
    {
        if (instance == null)
        {
            instance = new MattProperties();
            propertiesLoader = new PropertiesLoader();
            propertiesLoader.start();
            try 
            {
                Thread.sleep(1000);
            }
            catch(Exception e) {}
        }
        return instance;
    }    
    
    public static void  save()
    {
        try
        {
            instance.store(new FileOutputStream("matt.properties"), null);
        }
        catch (Exception e)
        {
            Logger.log("Problem storing properties");
            e.printStackTrace();            
        }
    }
}

class PropertiesLoader extends Thread
{
    long lastModified = 0;
    public void run()
    {
        while (true)
        {
            File f = new File("matt.properties");
            long lastModified = f.lastModified();
            if (lastModified > this.lastModified)
            {
                try
                {
                    Logger.log("Reloading properties");
                    MattProperties.instance().load(new FileReader(f));
                    Logger.log(MattProperties.instance());
                }
                catch (Exception e)
                {
                    Logger.log("Could not load matt.properties file");
                    e.printStackTrace();
                }
                this.lastModified = lastModified;
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
        