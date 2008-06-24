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

