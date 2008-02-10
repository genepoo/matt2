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
public class OnsetPostProcessor {
    private Vector<TranscribedNote> transcribedNotes;
    
    public OnsetPostProcessor(Vector<TranscribedNote> transcribedNotes)
    {
        this.transcribedNotes = transcribedNotes;
    }

    public Vector<TranscribedNote> getTranscribedNotes()
    {
        return transcribedNotes;
    }

    public void setTranscribedNotes(Vector<TranscribedNote> transcribedNotes)
    {
        this.transcribedNotes = transcribedNotes;
    }
    
    /**
     * Remove false positives
     * @return
     */
    TranscribedNote[] postProcess()
    {
        float quaver = 0.0f;
        float[] histData = new float[transcribedNotes.size()];
        FuzzyHistogram fuzzyHistogram = new FuzzyHistogram();
                
        for (int i = 0 ; i < transcribedNotes.size() ; i ++)
        {
            TranscribedNote note = (TranscribedNote) transcribedNotes.elementAt(i);
            histData[i] = note.getDuration();
        }
        // The average note duration, probably the length of a quaver
        quaver = fuzzyHistogram.calculatePeek(histData, 0.3f);
        
        // If the Note duration is short, 
        // its probaby a false positive
        for (int i = 1 ; i < transcribedNotes.size() ; i ++)
        {
            TranscribedNote previous = transcribedNotes.elementAt(i - 1);
            TranscribedNote current = transcribedNotes.elementAt(i);            
            if (calculateNearestMultiple(current.getDuration(), quaver) == 0)
            {
                // Merge it with the following note
                /*
                 MattGuiNB.instance().log("Merging note: " + i + " " + current + " with previous: " + (i -1) + " " + previous);
                previous.setDuration(previous.getDuration() + current.getDuration());
                transcribedNotes.remove(i);
                 **/
                if (i + 1 < transcribedNotes.size())
                {
                    TranscribedNote next = transcribedNotes.elementAt( + 1);
                    next.setDuration(next.getDuration() + current.getDuration());
                }
                transcribedNotes.remove(i);
            }
        }
        // Now check the very first note
        if (calculateNearestMultiple(transcribedNotes.elementAt(0).getDuration(), quaver) == 0)
        {
            transcribedNotes.elementAt(1).setDuration(transcribedNotes.elementAt(1).getDuration() + transcribedNotes.elementAt(0).getDuration());
            transcribedNotes.remove(0);
        }

              
        
        TranscribedNote[] notes = new TranscribedNote[transcribedNotes.size()];
        transcribedNotes.copyInto(notes);        
        return notes;
    }
    
    public static int calculateNearestMultiple(float duration, float standard)
    {
        return Math.round(duration / standard);
        /*
         int nearestMultiple = 1;
        float nearest = Float.MAX_VALUE;
        for (int i = 0; i < 10 ; i ++)
        {
            float diff = duration - (standard * i);
            diff *= diff;
            if (diff < nearest)
            {
                nearest = diff;
                nearestMultiple = i;
            }
        }
        return nearestMultiple;
         */
    }
}
