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
    
    private static MattProperties _instance = null;
    private static PropertiesLoader propertiesLoader = null; 
    /** Creates a new instance of MattProperties */
    private MattProperties() {
    }
    
    public static float getFloat(String key)
    {
        return Float.parseFloat(getString(key));
    }
    
    public static boolean getBoolean(String key)
    {
        return Boolean.parseBoolean(getString(key));
    }
    
    public static String getString(String key)
    {
        return "" + instance().get(key);
    }
    
    public static MattProperties instance()
    {
        if (_instance == null)
        {
            _instance = new MattProperties();
            propertiesLoader = new PropertiesLoader();
            propertiesLoader.load();
            propertiesLoader.start();
        }
        return _instance;
    }    
    
    public static void  save()
    {
        try
        {
            _instance.store(new FileOutputStream("matt.properties"), null);
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
    File f = new File("matt.properties");
    long lastModified = 0;
    
    public void load()
    {
        this.lastModified = f.lastModified();
        try
        {
            MattProperties.instance().load(new FileReader(f));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void run()
    {
        while (true)
        {
            long lastModified = f.lastModified();
            if (lastModified > this.lastModified)
            {
                try
                {
                    Logger.log("Reloading properties");
                    load();
                    
                    Logger.log(MattProperties.instance());
                }
                catch (Exception e)
                {
                    Logger.log("Could not load matt.properties file");
                    e.printStackTrace();
                }
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
        