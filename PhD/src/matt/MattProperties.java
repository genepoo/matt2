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
    
    public static Object getP(String key)
    {
        return instance().get(key);
    }
    
    public static MattProperties instance()
    {
        if (instance == null)
        {
            instance = new MattProperties();
            propertiesLoader = new PropertiesLoader();
            propertiesLoader.start();
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
            MattGuiNB.log("Problem storing properties");
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
                    MattGuiNB.instance().log("Reloading properties");
                    MattProperties.instance().load(new FileReader(f));
                    MattGuiNB.instance().log(MattProperties.instance());
                }
                catch (Exception e)
                {
                    MattGuiNB.instance().log("Could not load matt.properties file");
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
        