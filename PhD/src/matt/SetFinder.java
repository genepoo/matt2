/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.awt.*;
import java.util.*;
import matt.dsp.*;
/**
 *
 * @author Bryan Duggan
 */
public class SetFinder extends Thread{
    
    private boolean running;
    private String toFind;
    private Object lock;
    private PriorityQueue<ABCMatch> pq;
    
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
        boolean oldDrawGraphs = Boolean.parseBoolean(MattProperties.getP("drawGraphs"));
        MattProperties.instance().setProperty("drawGraphs", "true");
        int typicalLength = ABCTranscriber.NOTES_PER_BAR[ABCTranscriber.JIG] * 16;
        Logger.log("Searching for a set");
        
        CorpusIndex index = CorpusIndex.instance();
        
        int startAt = 0;
        int whichTune = 1;
        while (running)
        {
            index.reset();
            float bestEd = Float.MAX_VALUE;
            int bestIndex = 0;
            int i = 0;
            // Just extract enough of the tune to indentify the tune
            if (startAt + typicalLength > toFind.length())
            {
                break;
            }            
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
                    // MattGuiNB.log("Expanding: " + searchIn);
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
                ABCMatch match = new ABCMatch();
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
                
                float range = max - min;
                for (int j = 0 ; j < fed.length ; j ++)
                {
                    fed[j] = (fed[j] - min) / range;
                }
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
                MattGuiNB.instance().addFFTGraph(edGraph, "UNFILT: " + firstTune.getTitle());

                edGraph = new Graph();
                edGraph.setBackground(Color.WHITE);
                edGraph.getDefaultSeries().setData(fedf);
                MattGuiNB.instance().addFFTGraph(edGraph, "FILT: " + firstTune.getTitle());
                
                int repeats = 0;
                int slope = 10;
                Vector<Integer> troughs = null;
                float threshold = 0.2f;
                while ((repeats == 0) || (repeats >= 4) && running)
                {
                    troughs = PeakCalculator.calculateTrough(fedf, slope, fed.length, threshold, edGraph, typicalLength);
                    repeats = troughs.size();
                    Logger.log(troughs.size() + " repeats found");
                    if (troughs.size() == 0)
                    {
                        threshold -= 0.05f;
                        Logger.log("Trying again with a threshold of " + threshold);                    
                    }
                    if (troughs.size() >= 4)
                    {
                        threshold += 0.05f;
                        Logger.log("Trying again with a threshold of " + threshold);                    
                    }
                }                     
                for (int j = 0 ; j < troughs.size() ; j ++)
                {
                    edGraph.getDefaultSeries().addVerticalLine(troughs.elementAt(j).floatValue());                            
                }
                match.setRepititions(repeats);
                MattGuiNB.instance().addMatch(match);     
                whichTune ++;
                pq.add(match);
                
                startAt = ((Integer)troughs.elementAt(troughs.size() -1)).intValue();
            }
            else
            {
                Logger.log("Set annotation algorithm interrupted");
            }            
        }
        MattProperties.instance().setProperty("drawGraphs", "" + oldDrawGraphs);
        Logger.log("No more tunes found");
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
}


