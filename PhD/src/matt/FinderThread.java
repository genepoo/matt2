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
public class FinderThread extends Thread{
    private boolean running;
    String searchIn;
    String toFind;
    private PriorityQueue<ABCMatch> pq;
    private float edThreshold;
    Object lock;
    
    TranscribedNote[] notes;
    
    public FinderThread(String searchIn, String toFind, TranscribedNote[] notes, PriorityQueue<ABCMatch> pq, Object lock)
    {
        this.searchIn = searchIn;
        this.toFind = toFind;
        this.pq = pq;
        this.lock = lock;    
        this.notes = notes;
    }
    
    public void run()
    {
        running = true;
        synchronized(lock)
        {
            lock.notify();
        }
        CorpusIndex index = CorpusIndex.instance();
        edThreshold = Float.parseFloat("" + MattProperties.getString("editDistanceThreshold"));
        
        CorpusEntry current;
        
        while (running)
        {
            current = index.getNext();
            MattGuiNB.instance().getProgressBar().setValue(index.getCurrentIndex());
            if (current == null)
            {       
                running = false;
                synchronized(lock)
                {                    
                    lock.notify();
                }
            }
            else
            {
                searchIndexEntry(current);
            }            
        }   
        Logger.log("Thread finished");
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }
    
    private void searchIndexEntry(CorpusEntry entry)
    {
        boolean logSearches = Boolean.parseBoolean("" + MattProperties.getString("logSearches"));
        boolean expandShortTunes = Boolean.parseBoolean("" + MattProperties.getString("expandShortTunes"));
        if (logSearches)
        {
            Logger.log("Searching tune: " + entry.getX() + " " + entry.getTitle());
        }
        try
        {
            String searchIn = entry.getKey();

            if (logSearches)
            {
                Logger.log("Source: " + searchIn);
            }
            // Edit distance search
            float bestEditdistance = Float.MAX_VALUE;
            String bestBit = "";        
            
            StringBuffer nlSearchIn = new StringBuffer(searchIn);
            boolean needsExpansion = false;        
            if (expandShortTunes &&  MattProperties.getString("searchMethod").equalsIgnoreCase("bryan"))
            {
                while (toFind.length() > nlSearchIn.length())
                {
                    MattGuiNB.log("Expanding: " + nlSearchIn);
                    needsExpansion = true;
                    nlSearchIn.append(searchIn);
                }
            }
            if (needsExpansion)
            {
                nlSearchIn.setLength(toFind.length());
            }
            
            float ed;
            if (MattProperties.getString("searchMethod").equalsIgnoreCase("semex"))
            {
                ed = EditDistance.minEdSemex(MIDITools.instance().toMIDISequence(notes), entry.getMidiSequence());
            }
            else
            {
                ed = EditDistance.minEdSubString(toFind, "" + nlSearchIn);
            }
            if (ed < bestEditdistance)
            {
                bestEditdistance = ed;
                bestBit = "" + nlSearchIn;
            }                       
            ABCMatch match = new ABCMatch();
            match.setLine(bestBit);
            match.setEditDistance(bestEditdistance);
            match.setFileName(entry.getFile());
            match.setX(entry.getX());
            match.setTitle(entry.getTitle());
            match.setIndex(entry.getIndex());
            match.setCorpusEntry(entry);
            pq.add(match);
            ABCMatch best = pq.peek();
            MattGuiNB.instance().setBestSoFar(best);
            if (logSearches)
                Logger.log("Best edit distance: " + bestEditdistance + " for search: " + bestBit);
        }
        catch (Exception e)
        {
            Logger.log("Exception parsing tune: " + entry.getTitle());
            e.printStackTrace();
        }
        
    }
}
