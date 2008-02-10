/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.util.*;
import com.eaio.stringsearch.MismatchSearch;
import com.eaio.stringsearch.ShiftOrMismatches;
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
    
    public FinderThread(String searchIn, String toFind, PriorityQueue<ABCMatch> pq, Object lock)
    {
        this.searchIn = searchIn;
        this.toFind = toFind;
        this.pq = pq;
        this.lock = lock;    
    }
    
    public void run()
    {
        final CorpusIndex index = CorpusIndex.instance();
        edThreshold = Float.parseFloat("" + MattProperties.getP("editDistanceThreshold"));
        
        CorpusEntry current;
        setRunning(true);
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
        boolean logSearches = Boolean.parseBoolean("" + MattProperties.getP("logSearches"));
        boolean useSlidingWindows = Boolean.parseBoolean("" + MattProperties.getP("useSlidingWindows"));
        boolean expandShortTunes = Boolean.parseBoolean("" + MattProperties.getP("expandShortTunes"));
        if (logSearches)
        {
            MattGuiNB.log("Searching tune: " + entry.getX() + " " + entry.getTitle());
        }
        try
        {
            String searchIn = entry.getKey();

            if (logSearches)
            {
                MattGuiNB.instance().log("Source: " + searchIn);
            }
            // Edit distance search
            float bestEditdistance = Float.MAX_VALUE;
            String bestBit = "";        
            
            StringBuffer nlSearchIn = new StringBuffer(searchIn);
            boolean needsExpansion = false;        
            if (expandShortTunes)
            {
                 while (toFind.length() > nlSearchIn.length())
                {
                    needsExpansion = true;
                    nlSearchIn.append(searchIn);
                }
            }
            if (needsExpansion || ! useSlidingWindows)
            {
                nlSearchIn.setLength(toFind.length());
            }
            
            if (! useSlidingWindows)
            {
                float ed = EditDistance.getLevenshteinDistance(toFind, "" + nlSearchIn);
                if (ed < bestEditdistance)
                {
                    bestEditdistance = ed;
                    bestBit = "" + nlSearchIn;
                }
            }
            else
            {
                for (int i = 0 ; i < (nlSearchIn.length() + 1) - toFind.length() ; i ++)
                {
                    String tuneBit = nlSearchIn.substring(i, i + toFind.length());

                    float ed = EditDistance.editDistance(toFind, tuneBit);

                    if (ed < bestEditdistance)
                    {
                        bestEditdistance = ed;
                        bestBit = tuneBit;
                    }
                }
            }
            
            
            ABCMatch match = new ABCMatch();
            match.setLine(bestBit);
            match.setEditDistance(bestEditdistance);
            match.setFileName(entry.getFile());
            match.setX(entry.getX());
            match.setTitle(entry.getTitle());
            pq.add(match);
            ABCMatch best = pq.peek();
            MattGuiNB.instance().setBestSoFar(best);
            if (logSearches)
                MattGuiNB.instance().log("Best edit distance: " + bestEditdistance + " for search: " + bestBit);
        }
        catch (Exception e)
        {
            MattGuiNB.instance().log("Exception parsing tune: " + entry.getTitle());
            e.printStackTrace();
        }
    }
}
