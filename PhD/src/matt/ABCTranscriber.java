/*
 * ABCTranscriber.java
 *
 * Created on 06 July 2007, 12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;
import java.util.*;
/**
 *
 * @author Bryan
 */
public class ABCTranscriber {
    
    private static final float RANGE = 0.1f;    
    
    TranscribedNote[] transcribedNotes;
    Transcriber transcriber;
    
    public static final int REEL = 0;
    public static final int JIG = 1;
    public static final int HORNPIPE = 2;
    
    private static int[] notesPerBar = { 8, 6, 8 };
    private int tuneType = REEL;
           
    public static String [] noteNames = {"D", "E", "F", "G", "A", "B", "c", "d", "e", "f", "g", "a", "b", "c'", "d'" }; 
    public static float[] knownFrequencies = new float[15];
    
    public static final float D4 = 293.66f;
    
    private float standardNoteLength = 0.0f;
    
    private float maxEnergy;
    private float averageEnergy;
    
    void testScale()
    {
        for (int i = 0 ; i < knownFrequencies.length ; i ++)
        {
            TonePlayer.playTone(knownFrequencies[i], 0.25f, 0.1f);
        }
    }
    
    boolean isWholeToneInterval(int n, int[] intervals) 
    {
        for (int i = 0 ; i < intervals.length; i ++)
        {
            if (n == intervals[i])
            {
                return true;
            }
        }
        return false;
    }

    void makeScale(String inKey, String mode) 
    {
        float ratio = 1.05946309436f;
        int[] majorKeyIntervals = {1, 2, 4, 5, 6, 8, 9, 11, 12, 13};
        if (mode.equals("Major"))
        {
            knownFrequencies[0] = D4;
            // W - W - H - W - W - W - H
            for (int i = 1 ; i < knownFrequencies.length ; i ++)
            {
               if (isWholeToneInterval(i, majorKeyIntervals))
               {
                       knownFrequencies[i] = knownFrequencies[i - 1] * ratio * ratio;
               }
               else
               {
                    knownFrequencies[i] = knownFrequencies[i - 1] * ratio;
               }               
            }
        }
        // W - W - H - W - W - W - H
    }
            
    /** Creates a new instance of ABCTranscriber */
    public ABCTranscriber() {
    }
        
    public ABCTranscriber(Transcriber transcriber) {
        this.transcribedNotes = transcriber.getTranscribedNotes();
        this.transcriber = transcriber;
    }
    
    public String convertToABC()
    {
        StringBuffer sb = new StringBuffer();
        int noteInBar = 0;

        float standardNote = calculateStandardNoteDuration();
        EnergyCalculator ec = new EnergyCalculator();
        ec.setSignal(transcriber.getSignal());
        setMaxEnergy(ec.calculateMaxEnergy());
        setAverageEnergy(ec.calculateAverageEnergy());
        MattGuiNB.instance().log("Max energy in signal: " + ec.formatEnergy(getMaxEnergy()));
        MattGuiNB.instance().log("Average energy in signal: " + ec.formatEnergy(getAverageEnergy()));
        for (int i = 0 ; i < transcribedNotes.length ; i ++)
        {
            boolean found = false;
            if (isBreath(transcribedNotes[i]))
            {
                MattGuiNB.instance().log("Breath detected at frame: " + i);
                transcribedNotes[i].setName("z");
                if (sb.length() != 0)
                {                    
                    found = true;
                }
                else
                {
                    MattGuiNB.instance().log("Ignoring breath before opening note");
                }
            }
            else
            {
                float distance[] = new float[knownFrequencies.length];
                for (int j = 0 ; j < knownFrequencies.length ; j ++)
                {
                    float difference = transcribedNotes[i].getFrequency() - knownFrequencies[j];
                    
                    distance[j] = difference * difference;
                }
                int minIndex = 0;
                float min = Float.MAX_VALUE; 
                for (int j = 0 ; j < knownFrequencies.length ; j ++)
                {
                    if (distance[j] < min)
                    {
                        minIndex = j;
                        min = distance[j];
                    }
                }
                found = true;                
                transcribedNotes[i].setName(noteNames[minIndex]);
            }
            if (found)
            {
                /*
                 if ((sb.length() > 0) && (sb.charAt(sb.length() - 1) == 'z') && (transcribedNotes[i].getName().equals("z")))
                {
                    continue;
                }
                 */ 
                sb.append(transcribedNotes[i].getName());
                // A breath should never be longer than a single note
                //if (!transcribedNotes[i].getName().equals("z"))
                {
                    int nearestMultiple = OnsetPostProcessor.calculateNearestMultiple(transcribedNotes[i].getDuration(), standardNote);
                    if (nearestMultiple > 1)
                    {
                        // Quantise at dottet crochets 
                        if (nearestMultiple > 3)
                        {
                            MattGuiNB.log("Note of length " + nearestMultiple + " detected. Quantising at 3");
                            nearestMultiple = 3;
                        }
                        sb.append("" + nearestMultiple);
                    }
                    noteInBar += nearestMultiple;
                    if (noteInBar % notesPerBar[tuneType] == 0)
                    {
                        sb.append("|" + System.getProperty("line.separator"));
                    }
                }
            }
            else
            {
                MattGuiNB.instance().log("Ignoring: " + transcribedNotes[i]);                
            }
        }
        return sb.toString();
    }
    
    private boolean isBreath(TranscribedNote note)
    {
        float threshold = averageEnergy * Float.parseFloat("" + MattProperties.instance().get("breathThreshold"));
        
        if (note.getEnergy() < threshold)
        {
           MattGuiNB.instance().log(note.getEnergy()  + " energy is less than the threshold of " + threshold);
           return true;
        }
        if (note.getFrequency() < 100.0f)
        {
            MattGuiNB.instance().log(note.getFrequency() + " frequency is less than 100hz");
           return true;
        }
        return false;
    }

    public int getTuneType() {
        return tuneType;
    }

    public void setTuneType(int tuneType) {
        this.tuneType = tuneType;
    }
    
    public void printScale()
    {
        for (int i = 0 ; i < noteNames.length; i ++)
        {
            MattGuiNB.instance().log(noteNames[i] + ": " + knownFrequencies[i]);
        }
    }
    
    float calculateStandardNoteDuration()
    {      
        float duration = 0.0f;
        float[] histData = new float[transcribedNotes.length];
        FuzzyHistogram fuzzyHistogram = new FuzzyHistogram();
        
        for (int i = 0 ; i < transcribedNotes.length ; i ++)
        {
            histData[i] = transcribedNotes[i].getDuration();
        }
        duration = fuzzyHistogram.calculatePeek(histData, 0.3f);
        return duration;
    }
    
    public float getMaxEnergy()
    {
        return maxEnergy;
    }

    public void setMaxEnergy(float maxEnergy)
    {
        this.maxEnergy = maxEnergy;
    }

    public float getAverageEnergy()
    {
        return averageEnergy;
    }

    public void setAverageEnergy(float averageEnergy)
    {
        this.averageEnergy = averageEnergy;
    }
}