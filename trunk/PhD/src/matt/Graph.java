/*
 * Graph.java
 *
 * Created on 24 January 2007, 18:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JScrollPane;
/**
 *
 * @author bduggan
 */
public class Graph extends javax.swing.JScrollPane {
    
    public void setBackground(Color c)
    {
        super.setBackground(c);
        if (panel != null)
        {
            panel.setBackground(c);
        }
    }
    
    GraphPanel panel;
    
    private float scalingFactor = 0.0f;
    
    
    Vector series = new Vector();
    /** Creates a new instance of Graph */
    public Graph() { 
        panel = new GraphPanel(this);
        panel.setDoubleBuffered(false);
        getViewport().add(panel, null);
        scalingFactor = MattProperties.getFloat("scaleGraphFactor");
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    }
    
    public void save() 
    {
        String fileName = null;
        try
        {
            
            while (true)
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat();
            
                long start;
                dateFormat.applyPattern("dd-MM-yyyy HHmmssSS");            
                start = System.currentTimeMillis();
                Date now = new Date();
                now.setTime(start);

                fileName = MattProperties.instance().getProperty("resultsFolder") + System.getProperty("file.separator") + "graph "  + dateFormat.format(now) + ".txt";

                if (new File(fileName).exists())
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
                else
                {
                    break;                
                }
            }
            
            FileWriter fw = new FileWriter(fileName);
            Enumeration en = series.elements();

            while (en.hasMoreElements())
            {
                Series series = (Series) en.nextElement();
                float[] data = series.getData();
                for (int i = 0 ; i < data.length; i ++)
                {
                    fw.write("" + data[i]);
                    if (i < data.length - 1)
                    {
                        fw.write(",");
                    }
                }
                fw.write(System.getProperty("line.separator"));
            }
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
        
    public void clear()
    {
        Enumeration en = series.elements();
        
        while (en.hasMoreElements())
        {
            Series series = (Series) en.nextElement();
            series.clear();
        }
    }
    
    public Series getSeries(int series)
    {
        return (Series) this.series.elementAt(series);
    }
    
    public Series getDefaultSeries()
    {
        if (series.size() == 0)
        {
            Series series = new Series(this);
            this.series.add(series);
        }
        return  (Series) this.series.elementAt(0);
    }
    
    public void addSeries(Series series)
    {
        this.series.add(series);
        
    }
    
    public void removeAllSeries()
    {
        series.removeAllElements();
    }
    
    public int countSeries()
    {
        return series.size();
    }
    
    public GraphPanel getGraphPanel()
    {
        return panel;
    }

    public

    float getScalingFactor()
    {
        return scalingFactor;
    }

    public void setScalingFactor(float scalingFactor)
    {
        this.scalingFactor = scalingFactor;
    }
}

