/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.awt.*;
import java.util.*;
import javax.swing.JPanel;


/**
 *
 * @author Bryan Duggan
 */
class GraphPanel extends JPanel
{
    Graph graph;
    
    public GraphPanel(Graph graph)
    {
        this.graph = graph;
        setDoubleBuffered(false);
    }
    
    
    public void paintComponent(Graphics g) 
    {               
        super.paintComponent(g);
        if (MattProperties.instance().getString("mode").equals("server") || MattProperties.instance().getString("drawGraphs").equals("false"))
        {
            return;
        }
        for (int i = 0 ; i < graph.countSeries(); i ++)
        {
            Series series = graph.getSeries(i);
            series.paint(g);
        }
    }
}