/*
 * PeekCalculator.java
 *
 * Created on 26 March 2007, 00:02
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
public class PeekCalculator {
    
    /** Creates a new instance of PeekCalculator */
    public PeekCalculator() {
    }
    
    static Vector calculatePeeks(float[] data, int border, int howFar, float thresholdNormal)
    {
        float thresholdValue = 0;
        // First calculate the threshold
        if (thresholdNormal > 0)
        {
            for (int i = 0 ; i < howFar ; i ++)
            {
                if (data[i] > thresholdValue)
                {
                    thresholdValue = data[i];
                }
            }
        }        
        
        thresholdValue = thresholdValue * thresholdNormal;        
        Vector peeks = new Vector();
        
        if (howFar >= border)
        {
            for (int i = border ; i < howFar - border ; i ++)
            {
                boolean addPeek = true;
                if (data[i] >= thresholdValue)
                {
                    for (int j = 0 ; j < border ; j ++)
                    {
                        if ((data[i] < data[i - j]) || (data[i] < data[i + j]))
                        {
                            addPeek = false;
                            break;
                        }                    
                    }
                }
                else
                {
                    addPeek = false;              
                }
                if (addPeek)
                {
                    peeks.add(new Integer(i));
                }
            }
        }
        return peeks;
    }
}
