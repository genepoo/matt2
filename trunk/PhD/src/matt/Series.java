/*
 * Series.java
 *
 * Created on 19 June 2007, 14:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import java.util.*;
import java.awt.*;
/**
 *
 * @author Bryan
 */
public class Series {    
    private Graph parent;
    
    private float[] data;
    private float min;
    private float max;
    private float yScale;
    private Vector horizontalLines;
    private Vector verticalLines;
    
    public static final int LINE_GRAPH = 0;
    public static final int BAR_GRAPH = 1;
    
    private int graphType;
    
    private Color seriesColour;
    
    private boolean plotPoints;
    
    /** Creates a new instance of Graph */
    public Series(Graph parent) {
        this.parent = parent;
        horizontalLines = new Vector();
        verticalLines = new Vector();       
        graphType = LINE_GRAPH;
        plotPoints = false;
        seriesColour = Color.BLACK;
    }
    
    public void addHorizontalLine(float value)
    {
        horizontalLines.addElement(new Float(value));
        parent.repaint();
    }
    
    public void addVerticalLine(float value)
    {
        verticalLines.addElement(new Float(value));
        parent.repaint();
    }

    public float[] getData() 
    {
        return data;
    }

    public void setData(float[] data) 
    {        
        assert((data != null) && (data.length != 0));
        
        this.data = data;
        min = data[0]; max = data[0];
        for (int i = 1 ; i < data.length; i ++)
        {
            if (data[i] > max)
            {
                max = data[i];
            }
            
            if (data[i] < min)
            {
                min = data[i];
            }
        }
        calculateYScale();
    }

    public float getMin() {
        return min;
    }
    
    private void calculateYScale()
    {
        yScale = (float) (parent.getSize().height - 1.0f) / (max - min);
    }

    public void setMin(float min) {
        this.min = min;
        calculateYScale();
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        calculateYScale();
    }
    
    public void paint (Graphics g) 
    {        
        if ((data == null) || (data.length < 2))
        {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;           
        Dimension bounds = parent.getSize();              
        float xScale = (float) bounds.width  / (float) data.length;
        calculateYScale();
        g2.setColor(seriesColour);
        switch (graphType)
        {
            case LINE_GRAPH:
                for (int i = 1 ; i < data.length ; i ++)
                {
                    g2.drawLine(Math.round((i-1) * xScale), calculateY(data[i-1]), Math.round(i * xScale), calculateY(data[i]));
                }
                break;
            case BAR_GRAPH:    
                for (int i = 0 ; i < data.length ; i ++)
                {
                    int y = calculateY(data[i]);
                    g2.drawRect(Math.round(i * xScale), y, Math.round(xScale), bounds.height - y);
                }
                break;
        }
        
        if (plotPoints)
        {
            for (int i = 0 ; i < data.length ; i ++)
            {
                int y = (int) calculateY(data[i]);
                g2.drawOval((int)(i * xScale) - 3, y - 3, 6, 6);
            }            
        }
        
        Enumeration en = horizontalLines.elements();
        g2.setColor(Color.RED);
        while (en.hasMoreElements())
        {
            int y = (int) calculateY(((Float)en.nextElement()).floatValue());            
            g2.drawLine(0, y, bounds.width - 1, y);
        }
                
        en = verticalLines.elements();
        while (en.hasMoreElements())
        {            
            int x = (int) (((Float) en.nextElement()).floatValue() * xScale);
            g2.drawLine(x, 0, x, bounds.height);
        }
    }
    
    private int calculateY(float data)
    {
        return Math.round((float) parent.getBounds().height - (data + - min) * yScale);
    }

    public int getGraphType() {
        return graphType;
    }

    public void setGraphType(int graphType) {
        this.graphType = graphType;
    }

    public boolean isPlotPoints() {
        return plotPoints;
    }

    public void setPlotPoints(boolean plotPoints) {
        this.plotPoints = plotPoints;
    }
    
    public void clear()
    {
        horizontalLines.clear();
        verticalLines.clear();
        data = null;
        parent.repaint();
    }    

    public Color getSeriesColour() {
        return seriesColour;
    }

    public void setSeriesColour(Color seriesColour) {
        this.seriesColour = seriesColour;
    }
}