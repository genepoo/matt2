/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import matt.dsp.PeakCalculator;
import java.awt.*;
import java.util.*;
import matt.dsp.*;
/**
 *
 * @author Bryan Duggan
 */
public class SetFinder extends Thread{
    // Amount to compensate by in the trough detector, because of filtering
    private static final int FILTER_COMP = 15;
    private boolean running;
    private String toFind;
    private Object lock;
    private PriorityQueue<ABCMatch> pq;
    private TranscribedNote[] transcribedNotes;
    
    public static boolean isSet(String transcription)
    {
        int notesInReel = 320;
        
        return (transcription.length() >= notesInReel);
    }


    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public String getToFind()
    {
        return toFind;
    }

    public void setToFind(String toFind)
    {
        this.toFind = toFind;
    }
    
    public void run()
    {
        running = true;
        Vector<Float> turns = new Vector();
        boolean oldDrawGraphs = Boolean.parseBoolean(MattProperties.getString("drawGraphs"));
        MattProperties.instance().setProperty("drawGraphs", "true");
        int typicalLength = ABCTranscriber.NOTES_PER_BAR[ABCTranscriber.JIG] * 16;
        Logger.log("Searching for a set");
        
        CorpusIndex index = CorpusIndex.instance();
        
        int startAt = 0;
        int whichTune = 1;
        ABCMatch match = null;
        while (running)
        {
            index.reset();
            float bestEd = Float.MAX_VALUE;
            int bestIndex = 0;
            int i = 0;
                      
            String searchString = toFind.substring(startAt, startAt + typicalLength);            
            
            while (running)
            {
                CorpusEntry current = index.getNext();
                if (current == null)
                {
                    break;
                }
                StringBuffer searchIn = new StringBuffer(current.getKey());
                boolean needsExpansion = false;
                while (searchString.length() > searchIn.length())
                {
                    searchIn.append(searchIn);
                    needsExpansion  = true;                        
                }
                if (needsExpansion)
                {
                    searchIn.setLength(searchString.length());
                }
                float ed = EditDistance.minEdSubString(searchString, "" + searchIn);
                if (ed < bestEd)
                {
                    bestEd = ed;
                    bestIndex = i;
                }
                i ++;
            }
            
            if (running)
            {
                CorpusEntry firstTune = index.get(bestIndex);
                match = new ABCMatch();
                match.setCorpusEntry(firstTune);
                match.setEditDistance(bestEd);
                match.setWhich(whichTune);
                match.setIndex(bestIndex);
                match.setNotation(firstTune.getKey());
                match.setTitle(firstTune.getTitle());                
                match.setFileName(firstTune.getFile());
                match.setX(firstTune.getX());
                
                Logger.log("Tune identified as: " + firstTune.getTitle());

                // Now attempt to find the repititions of this tune in the set
                printExpanded(toFind);
                printExpanded(firstTune.getKey());
                int ed[] = EditDistance.edSubString(firstTune.getKey(), toFind);
                float fed[] = new float[ed.length];
                float fedf[] = new float[ed.length];
                
                float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
                for (int j = 0; j < ed.length ; j ++)
                {
                    fed[j] = ed[j] ;
                    if (fed[j] < min)
                    {
                        min = fed[j];
                    }
                    else if (fed[j] > max)
                    {
                        max = fed[j];
                    }
                    
                }
                // Normalise
                /*float range = max - min;
                for (int j = 0 ; j < fed.length ; j ++)
                {
                    fed[j] = (fed[j] - min) / range;
                }
                 */ 
                FIRFilter filter = new FIRFilter();
                filter.setFilterType(FIRFilter.LP);
                fedf = filter.filter(fed);
                for (int j = 0 ; j < fedf.length ; j ++)
                {
                    System.out.print(fedf[j] + "\t");
                }
                System.out.println();
                Graph edGraph = new Graph();
                edGraph.setBackground(Color.WHITE);
                edGraph.getDefaultSeries().setData(fed);
                // edGraph.save();                
                MattGuiNB.instance().addFFTGraph(edGraph, "UNFILT: " + firstTune.getTitle());

                Graph edGraphf = new Graph();
                edGraphf.setBackground(Color.WHITE);
                edGraphf.getDefaultSeries().setData(fedf);
                // edGraphf.save();
                MattGuiNB.instance().addFFTGraph(edGraphf, "FILT: " + firstTune.getTitle());
                
                int repeats = 0;
                int slope = 10;
                Vector<Integer> troughs = null;
                float threshold = 0.3f;
                while ((repeats == 0) || (repeats >= 5) && running)
                {
                    troughs = PeakCalculator.calculateTrough(fedf, slope, fedf.length, threshold, edGraphf, typicalLength);
                    repeats = troughs.size();
                    Logger.log(troughs.size() + " repeats found");
                    if (troughs.size() == 0)
                    {
                        threshold -= 0.05f;
                        Logger.log("Trying again with a threshold of " + threshold);                    
                    }
                    if (troughs.size() >= 5)
                    {
                        threshold += 0.05f;
                        Logger.log("Trying again with a threshold of " + threshold);                    
                    }
                }                     
                Logger.log("Troughs:");
                for (int j = 0 ; j < troughs.size() ; j ++)
                {
                    edGraph.getDefaultSeries().addVerticalLine(troughs.elementAt(j).floatValue() - FILTER_COMP);
                    edGraphf.getDefaultSeries().addVerticalLine(troughs.elementAt(j).floatValue());
                    Logger.log(troughs.elementAt(j).floatValue());
                }
                if ((pq.peek() != null) && (pq.peek().getX() == match.getX()))
                {
                    // Found the same tune twice, so the threshold is too low
                    pq.peek().setRepititions(pq.peek().getRepititions() + 1);
                    Logger.log("Threshold is too low, so I'm quitting");
                    break;
                }
                match.setRepititions(repeats);
                MattGuiNB.instance().addMatch(match);
                whichTune ++;
                pq.add(match);
                // Just extract enough of the tune to indentify the tune
                startAt = ((Integer)troughs.elementAt(troughs.size() -1)).intValue();
                if (startAt + typicalLength > toFind.length())
                {
                    troughsToTime(turns, troughs, fedf, firstTune.getTitle(), FILTER_COMP, true);
                    break;
                }  
                else
                {
                    troughsToTime(turns, troughs, fedf, firstTune.getTitle(), FILTER_COMP, false);
                }
            }
            else
            {
                Logger.log("Set annotation algorithm interrupted");
            }            
        }
        MattProperties.instance().setProperty("drawGraphs", "" + oldDrawGraphs);
        Logger.log("No more tunes found");        
        printTurns(turns);
        BatchJob.results.log(printTurns(turns));
        running = false;
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void printExpanded(String s)
    {
        for (int i = 0 ; i < s.length() ; i ++)
        {
            System.out.print(s.charAt(i) + "\t");
        }
        System.out.println();
    }
    
    String printTurns(Vector<Float> turns)
    {
        StringBuffer sb = new StringBuffer();
        Logger.log("Turns:");
        for (int i = 0 ; i < turns.size() ; i ++)
        {
            sb.append(turns.elementAt(i).floatValue() + "\t");
        }
        Logger.log("" + sb);
        return "" + sb;
    }
    
    void troughsToTime(Vector<Float> turns, Vector<Integer> v, float[] swEd, String title, int filterComp, boolean lastTune)
    {        
        if (transcribedNotes == null)
        {
            return;
        }
        // Match the abc to the transcribed notes
        for (int i = 0 ; i < v.size() ; i ++)
        {
            int troughIndex = v.elementAt(i);
            if (!lastTune ||  (i != (v.size() - 1)))
            {
                troughIndex -= filterComp;
            }
            
            // Find the start of the trough
            int sti = troughIndex;
            while (swEd[sti - 1] == swEd[sti])
            {
                sti --;
            }            
            int j;            
            for (j = 0 ; j < transcribedNotes.length; j ++)
            {
                if (sti <= transcribedNotes[j].getQuaverQ())
                {
                    // Logger.log(toFind.charAt(troughIndex - 1) + " " + transcribedNotes[j].getName());
                    break;
                }
            }
            if (j >=transcribedNotes.length)
            {
                j = transcribedNotes.length - 1;
            }
            turns.add(new Float(transcribedNotes[j].getStart()));
            Logger.log(title + "\t" + (sti) + "\t" + transcribedNotes[j].getStart());
        }
    }

    public Object getLock()
    {
        return lock;
    }

    public void setLock(Object lock)
    {
        this.lock = lock;
    }

    public PriorityQueue<ABCMatch> getPq()
    {
        return pq;
    }

    public void setPq(PriorityQueue<ABCMatch> pq)
    {
        this.pq = pq;
    }

    public TranscribedNote[] getTranscribedNotes()
    {
        return transcribedNotes;
    }

    public void setTranscribedNotes(TranscribedNote[] transcribedNotes)
    {
        this.transcribedNotes = transcribedNotes;
    }
}


