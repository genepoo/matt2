/*
 * SimpleHistogram.java
 *
 * Created on 14 March 2007, 18:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;
import java.util.*;

/**
 *
 * @author bduggan
 */
public class SimpleHistogram {
    
    private float[] data;
    
    /** Creates a new instance of SimpleHistogram */
    public SimpleHistogram() {
    }
    
    public float calculatePeek()
    {
        Hashtable temp = new Hashtable();
        
        for (int i = 0 ; i < data.length ; i ++)
        {
            Integer key = new Integer(Math.round(data[i]));
            Integer count;
            if (temp.containsKey(key))
            {
                count = (Integer) temp.get(key);
            }
            else
            {
                count = new Integer(0);
            }
            count = new Integer(count.intValue() + 1);
            temp.put(key, count);          
        }
        float max = -1;
        float freqInterval = -1;
        Enumeration en = temp.keys();        
        while (en.hasMoreElements())
        {
            Integer key = (Integer) en.nextElement();
            Integer count = (Integer) temp.get(key);
            if (count.floatValue() > max)
            {
                max = count.floatValue();
                freqInterval = key.floatValue();
            }            
        }

        return freqInterval;
    }

    public float[] getData() {
        return data;
    }

    public void setData(float[] data) {
        this.data = data;
    }
    
}
