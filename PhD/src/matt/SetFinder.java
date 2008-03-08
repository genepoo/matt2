/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.awt.*;
import java.util.*;
/**
 *
 * @author Bryan Duggan
 */
public class SetFinder extends Thread{
    
    private boolean running;
    private String toFind;
    
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
        int typicalLength = ABCTranscriber.NOTES_PER_BAR[ABCTranscriber.REEL] * 16;
        Logger.log("Searching for a set");
        
        CorpusIndex index = CorpusIndex.instance();
        
        int startAt = 0;
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
                match.setIndex(bestIndex);
                match.setTitle(firstTune.getTitle());                
                match.setFileName(firstTune.getFile());
                match.setX(firstTune.getX());
                MattGuiNB.instance().addMatch(match);
                Logger.log("Tune identified as: " + firstTune.getTitle());

                // Now attempt to find the repititions of this tune in the set
                printExpanded(toFind);
                printExpanded(firstTune.getKey());
                int ed[] = EditDistance.edSubString(firstTune.getKey(), toFind);
                float fed[] = new float[ed.length];
                for (int j = 0; j < ed.length ; j ++)
                {
                    fed[j] = ed[j];
                    System.out.print(fed[j] + "\t");
                }
                System.out.println();
                Graph edGraph = new Graph();
                edGraph.setBackground(Color.WHITE);
                edGraph.getDefaultSeries().setData(fed);
                MattGuiNB.instance().addFFTGraph(edGraph, "" + firstTune.getTitle());
                
                int repeats = 0;
                int slope = 10;
                Vector troughs = null;

                while ((repeats == 0) || (repeats >= 4) && running)
                {
                    troughs = PeakCalculator.calculateTrough(fed, slope, fed.length, 1.2f);
                    repeats = troughs.size();
                    Logger.log(troughs.size() + " repeats found");
                    if (troughs.size() == 0)
                    {
                        slope --;
                        Logger.log("Trying again with a slope of " + slope);                    
                    }
                    if (troughs.size() >= 4)
                    {
                        slope ++;
                        Logger.log("Trying again with a slope of " + slope);                    
                    }
                }
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
    }
    
    void printExpanded(String s)
    {
        for (int i = 0 ; i < s.length() ; i ++)
        {
            System.out.print(s.charAt(i) + "\t");
        }
        System.out.println();
    }
}
