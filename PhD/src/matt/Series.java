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

import java.awt.*;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 *
 * @author Bryan
 */
public class Series {    
    private Graph parent;
    
    private float[] data;
    private float[] scaledData;
    private float min;
    private float max;
    private float yScale;
    private Vector<Float> horizontalLines;
    private Vector<Float> verticalLines;
    private Vector<Float> verticalLines2;
    
    public static final int LINE_GRAPH = 0;
    public static final int BAR_GRAPH = 1;
    public static final int POINT_GRAPH = 2;
    
    private boolean scale = true;
    
    private int graphType;
    
    private Color seriesColour;
    
    private boolean plotPoints;
    
    
    /** Creates a new instance of Graph */
    public Series(Graph parent) {
        this.parent = parent;
        horizontalLines = new Vector();
        verticalLines = new Vector();       
        verticalLines2 = new Vector();       
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
    
    public void addVerticalLine2(float value)
    {
        verticalLines2.addElement(new Float(value));
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
        calculateScale();
    }

    public float getMin() {
        return min;
    }
    
    private void calculateScale()
    {
        Dimension bounds = parent.getSize();         
        if (scale)
        {
            GraphPanel panel = parent.getGraphPanel();
            if (bounds.width > data.length *  parent.getScalingFactor())
            {
                // No scroll bars necessary
                parent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                
            }
            else
            {
                // Scroll bars necessary
                parent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                bounds.height = parent.getViewport().getHeight();
                bounds.width = (int) ((float) data.length *  parent.getScalingFactor());
            }
            parent.getGraphPanel().setPreferredSize(bounds);
            parent.getGraphPanel().setSize(bounds);
        }
        yScale = (float) (bounds.height - 1.0f) / (max - min);
    }

    public void setMin(float min) {
        this.min = min;
        calculateScale();
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        calculateScale();
    }
    
    public void paint (Graphics g) 
    {
        if (data == null)
        {
            return;
        }
        
        if (data.length < 2)
        {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;           
        Dimension bounds = parent.getGraphPanel().getSize();              
        calculateScale();
        float xScale = (float) data.length / (float) bounds.width;      
        Rectangle clipBounds = g.getClipBounds();
        g2.setColor(seriesColour);
        // What part of the series do we need to draw?
        int start = (int) Math.floor((float) clipBounds.x * xScale);
        int end = (int) Math.ceil(((float) clipBounds.x + clipBounds.width) * xScale); 

        switch (graphType)
        {
            case LINE_GRAPH:     
                for (int i = start ; i < end - 1 ; i ++)
                {                    
                    if ((data == null) || (i >= data.length))
                    {
                        break;
                    }
                    int x1 = Math.round(  ((float) i) / xScale);
                    int y1 = (int) Math.floor(calculateY(data[i]));

                    int x2 = Math.round(  ((float) i + 1) / xScale);                   
                    int y2 = (int) Math.floor(calculateY(data[i + 1]));
                    
                    //int x1 = (int) ((float) i + 1.0f / xScale);                    
                    g2.drawLine(x1, y1, x2, y2);

                }
                break;                
            case BAR_GRAPH:    
                for (int i = start ; i < end; i ++)
                {
                    float x1 = Math.round(  ((float) i) / xScale);
                    float x2 = Math.round(  ((float) i + 1) / xScale);
                    if ((data == null) || (i >= data.length - 1))
                    {
                        break;
                    }
                    int y = calculateY(data[i]);
                    g2.drawRect((int) x1, y , (int)x2 - (int) x1, bounds.height);
                }
                break;
            case POINT_GRAPH:
            {
                for (int i = clipBounds.x ; i < clipBounds.x + clipBounds.width ; i ++)
                {
                    int x = (int) ((float) i * xScale);
                    if  ((x >= data.length))
                    {
                        System.out.println("Too long!!");
                        return;
                    }
                    g2.drawLine(i, calculateY(data[x]), i, calculateY(data[x]));
                }               
                break;
            }
        }

        if (plotPoints)
        {            
            if ((data != null))
            {
                try
                {
                    for (int i = 0 ; i < data.length ; i ++)
                    {
                        int y = (int) calculateY(data[i]);
                        g2.drawOval((int)(i / xScale) - 3, y - 3, 6, 6);
                    }
                }
                catch (Exception e)
                {
                    // e.printStackTrace();
                }
            }
        }
        
        g2.setColor(Color.PINK);
        for (int i = 0 ; i < verticalLines2.size(); i ++)    
        {            
            int x = (int) (verticalLines2.get(i) / xScale);
            g2.drawLine(x, 0, x, bounds.height);
        }
        
        g2.setColor(Color.RED);
        for (int i = 0 ; i < horizontalLines.size(); i ++)           
        {
            int y = (int) calculateY(horizontalLines.get(i));            
            g2.drawLine(0, y, bounds.width - 1, y);
        }
                
        for (int i = 0 ; i < verticalLines.size(); i ++)    
        {            
            int x = (int) (verticalLines.get(i) / xScale);
            g2.drawLine(x, 0, x, bounds.height);
        }
        
        

    }
    
    private int calculateY(float data)
    {
        return Math.round((float) parent.getGraphPanel().getSize().height - (data + - min) * yScale);
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
    
    public void clearLines()
    {
        horizontalLines.clear();
        verticalLines.clear();
        verticalLines2.clear();
    }
            
    public void clear()
    {
        horizontalLines.clear();
        verticalLines.clear();
        verticalLines2.clear();
        data = null;
        parent.repaint();
    }    

    public Color getSeriesColour() {
        return seriesColour;
    }

    public void setSeriesColour(Color seriesColour) {
        this.seriesColour = seriesColour;
    }

    public boolean isScale()
    {
        return scale;
    }

    public void setScale(boolean scale)
    {
        this.scale = scale;
    }
}