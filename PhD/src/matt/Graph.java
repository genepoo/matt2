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
/**
 *
 * @author bduggan
 */
public class Graph extends javax.swing.JPanel {
    
    Vector series = new Vector();
    /** Creates a new instance of Graph */
    public Graph() {       
    }
    
    public void paint (Graphics g) 
    {        
       
        super.paint(g);
        if (MattProperties.instance().getP("mode").equals("server"))
        {
            return;
        }
        Enumeration en = series.elements();
        
        while (en.hasMoreElements())
        {
            Series series = (Series) en.nextElement();
            series.paint(g);
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

}
