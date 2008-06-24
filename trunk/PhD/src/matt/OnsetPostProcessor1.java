/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.util.*;
/**
 *
 * @author Bryan Duggan
 */
public class OnsetPostProcessor1 {
    
    private Vector<Integer> onsets;
    
    public OnsetPostProcessor1(Vector<Integer> onsets)
    {
        this.onsets = onsets;
    }

    public Vector<Integer> postProcess()
    {
        int quaver = 0;
        float[] histData = new float[onsets.size()];
        FuzzyHistogram fuzzyHistogram = new FuzzyHistogram();
                
        for (int i = 1 ; i < onsets.size() ; i ++)
        {
            histData[i] = onsets.get(i) - onsets.get(i -1);
        }
        quaver = (int) fuzzyHistogram.calculatePeek(histData, 0.3f);
        
        // If the Note duration is short, 
        // its probaby a false positive
        for (int i = 1 ; i < onsets.size() ; i ++)
        {
            int previous = onsets.get(i - 1);
            int current  = onsets.elementAt(i);            
            if (calculateNearestMultiple(current - previous, quaver) == 0)
            {
                // Merge it with the following note
                
                if (i + 1 < onsets.size())
                {
                    
                }
                  
                onsets.remove(i);
                i --;                
            }
        }
        // Now check the very first note
        //if (calculateNearestMultiple(onsets.elementAt(0).getDuration(), quaver) == 0)
        {
            //onsets.elementAt(1).setDuration(onsets.elementAt(1).getDuration() + onsets.elementAt(0).getDuration());
            //onsets.remove(0);
        }

        return onsets;
    }

    Vector<Integer> getOnsets()
    {
        return onsets;
    }

    public void setOnsets(Vector<Integer> onsets)
    {
        this.onsets = onsets;
    }
    
    public static int calculateNearestMultiple(int duration, int standard)
    {
        return Math.round((float) duration / (float) standard);
    }
}
