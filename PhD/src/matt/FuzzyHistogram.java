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
public class FuzzyHistogram {
    float[] data;
    
    float calculatePeek(float[] data, float fuzz)
    {
        float duration = 0.0f;
        Vector candidateLengths = new Vector();
        for (int i = 0 ; i < data.length ; i ++)
        {
            // See if we already have a note this value
            boolean found = false;
            for (int j = 0 ; j < candidateLengths.size(); j ++)
            {
                float upper, lower;
                Candidate current = (Candidate) candidateLengths.elementAt(j);
                upper = current.value * ( 1.0f + fuzz);
                lower = current.value * ( 1.0f - fuzz);
                if ((data[i] >= lower) && (data[i] <= upper))
                {
                    found = true;
                    current.value = (current.value + data[i]) / 2.0f;
                    current.count ++;
                    break;
                }
            }
            if (!found)
            {
                Candidate newCandidate = new Candidate();
                newCandidate.value = data[i];
                newCandidate.count = 1;           
                candidateLengths.add(newCandidate);
            }
        }
        int maxIndex = 0;
        int maxValue = 0;
        for (int i= 0 ; i < candidateLengths.size(); i ++)
        {
            Candidate candidate = (Candidate) candidateLengths.elementAt(i);
            MattGuiNB.instance().log(candidate);
            if (candidate.count > maxValue)
            {
                maxIndex = i;
                maxValue = candidate.count;
            }
            candidate = (Candidate) candidateLengths.elementAt(maxIndex);
            duration = candidate.value;
        }
        return duration;
    }
}

class Candidate
{
    public float value = 0.0f;
    public int count = 0;
    
    public String toString()
    {
        return "Candidate: " + value + " Count: " + count;
    }
}