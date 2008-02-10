/*
 * ABCFinder.java
 *
 * Created on 17 July 2007, 15:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;
import java.io.*;
import java.util.*;
import abc.notation.Tune;
import abc.parser.TuneBook;

/**
 *
 * @author Bryan
 */
public class ABCFinder extends Thread{
    FinderThread[] finderThreads;
    private boolean running = false;
    private String startIn;
    private String searchString;
    private String toFind;    
    private boolean threadFinished;
    private PriorityQueue<ABCMatch> pq = new PriorityQueue<ABCMatch>(1000, new ABCMatch());
        
    /** Creates a new instance of ABCFinder */
    public ABCFinder() {
        running = false;
    }
    
    public String getStartIn() {
        return startIn;
    }

    public void setStartIn(String startIn) {
        this.startIn = startIn;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
       
    public void finda()
    {
        start();
    }
    
    public void run()
    {
        // find();
        findFromIndex();
    }
    
    public Tune[] findFromIndex()
    {
        running = true;
        if (searchString.length() == 0)
        {
            MattGuiNB.instance().log("Nothing to find");
            return null;
        }
        this.toFind = searchString;
        toFind = MattABCTools.expandLongNotes(toFind);
        toFind = MattABCTools.stripWhiteSpace(toFind);
        toFind = MattABCTools.stripBarDivisions(toFind);
        toFind = toFind.toUpperCase();
        MattGuiNB.log("Target: " + toFind);
        
        MattGuiNB.instance().clearMatches();
        final CorpusIndex index = CorpusIndex.instance();
        index.reset();
        int numThreads = Integer.parseInt("" + MattProperties.getP("numThreads"));
        
        finderThreads = new FinderThread[numThreads];
        Object lock = new Object();
        MattGuiNB.log("Staring " + numThreads + " finder threads");
        MattGuiNB.instance().getProgressBar().setValue(0);
        MattGuiNB.instance().getProgressBar().setMaximum(CorpusIndex.instance().size());
        for (int i = 0 ; i < numThreads; i ++)
        {
            finderThreads[i] = new FinderThread(startIn, toFind, pq, lock);
            finderThreads[i].start();
        }
        
        boolean stillRunning = true;
        while (stillRunning)
        {
            synchronized(lock)
            {      
                stillRunning = false;
                for (int i = 0 ; i < numThreads ; i ++)
                {
                    if (finderThreads[i].isRunning())
                    {
                        stillRunning = true;
                    }
                }
                try
                {
                    lock.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        printTop(pq.size());
        running = false;
        MattGuiNB.instance().log("Finished searching");
        return null;
    }
    
    
    
        public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        
        this.running = running;
        
        if (finderThreads != null)
        {
            for (int i = 0 ; i < finderThreads.length ; i ++)
            {
                if (finderThreads[i] != null)
                {
                    finderThreads[i].setRunning(running);
                }
            }
        }
    }

    public String getToFind()
    {
        return toFind;
    }

    public void setToFind(String toFind)
    {
        this.toFind = toFind;
    }

    
    private void printTop(int howMany)
    {
        MattGuiNB.instance().log("Printing top " + howMany + " matches");
        ABCMatch[] pushBack = new ABCMatch[howMany];  
        for (int i = 0 ; i < howMany ; i ++)
        {
            ABCMatch match = pq.poll();
            MattGuiNB.instance().log(match);
            MattGuiNB.instance().addMatch(match);   
            pushBack[i] = match;
        }
        // Put them back again
        for (int i = 0 ; i < howMany ; i ++)
        {
            pq.add(pushBack[i]);
        }
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
class ABCFilter implements FilenameFilter
{
      public boolean accept(File dir, String name) 
      {
            return (name.contains(".abc") || name.contains(".ABC"));
      }        
}

    /*
     public Tune[] find()
    {
        running = true;
        if (searchString.length() == 0)
        {
            MattGuiNB.instance().log("Nothing to find");
            return null;
        }
        this.toFind = searchString;
        toFind = MattABCTools.expandLongNotes(toFind);
        toFind = MattABCTools.stripWhiteSpace(toFind);
        toFind = MattABCTools.stripBarDivisions(toFind);
        toFind = toFind.toUpperCase();
        MattGuiNB.instance().log("Target: " + toFind);
        
        MattGuiNB.instance().clearMatches();
        ABCFilter filter = new ABCFilter();
        try
        {
            File dir = new File(MattProperties.instance().get("SearchCorpus").toString());
            File[] files = dir.listFiles(filter);

            for (int i = 0 ; i < files.length ; i ++)
            {
                if (!running)
                {
                    break;
                }
                searchFile(files[i]);
            }
        }
        catch (Exception e)
        {
            System.out.println("Could not find files");
            e.printStackTrace();
        }
        printTop(10);
        
        MattGuiNB.instance().log("Finished searching");
        return null;
    }
    */
    
    /*
     public void searchFile(File f) throws IOException
    {
        MattGuiNB.log("Searching tunebook: " + f.toString());
        TuneBook tuneBook = new TuneBook(f);        
        int numTunes = tuneBook.size();
        
        int[] tuneRefs = tuneBook.getReferenceNumbers();
        for (int i = 0 ; i < tuneRefs.length ; i ++)
        {
            Tune tune = tuneBook.getTune(tuneRefs[i]);
            String notation = tuneBook.getTuneNotation(tuneRefs[i]);
            searchTune(f, tune, notation);   
            if (!running)
            {
                MattGuiNB.instance().log("Search interrupted");
                break;
            }
        }
    }
    */
    /*
    private void searchTune(File f, Tune tune, String notation)
    {
        MattGuiNB.log("Searching tune: " + tune.getReferenceNumber() + " " + tune.getTitles()[0]);
        try
        {
            String searchIn = notation;
            searchIn = MattABCTools.skipHeaders(searchIn);
            searchIn = MattABCTools.stripComments(searchIn);
            searchIn = MattABCTools.stripWhiteSpace(searchIn);
            searchIn = MattABCTools.expandLongNotes(searchIn);
            searchIn = MattABCTools.expandParts(searchIn);
            searchIn = MattABCTools.stripBarDivisions(searchIn);
            searchIn = MattABCTools.removeTripletMarks(searchIn);
            searchIn = searchIn.toUpperCase();        

            MattGuiNB.instance().log("Source: " + searchIn);
            if (searchIn.length() == 0)
            {
                MattGuiNB.instance().log("Unhandled ABC in tune: " + f + " so i'm skipping it");
                return;
            }
            
            // Edit distance search
            float bestEditdistance = Float.MAX_VALUE;
            String bestBit = "";        
            String matchingToFind = this.toFind;
            while (matchingToFind.length() > searchIn.length())
            {
                //matchingToFind = matchingToFind.substring(0, searchIn.length());
                searchIn += searchIn;
            }
            for (int i = 0 ; i < (searchIn.length() + 1) - matchingToFind.length() ; i ++)
            {
                String tuneBit = searchIn.substring(i, i + matchingToFind.length());

                        float ed = EditDistance.editDistance(matchingToFind, tuneBit);
                if (ed < bestEditdistance)
                {
                    bestEditdistance = ed;
                    bestBit = tuneBit;
                }
                if (ed <= edThreshold)
                {
                    ABCMatch match = new ABCMatch();
                    match.setLine(tuneBit);
                    match.setEditDistance(ed);
                    match.setFileName(f.toString());
                    match.setTune(tune);
                    
                    match.setNotation(notation);            
                    matches.add(tune);
                    MattGuiNB.instance().log("Matched " + tuneBit + " with " + matchingToFind + " with edit diatance of " + ed);                
                    MattGuiNB.instance().addMatch(match);
                    break;
                }
            }
            ABCMatch match = new ABCMatch();
            match.setLine(bestBit);
            match.setEditDistance(bestEditdistance);
            match.setFileName(f.toString());
            match.setTune(tune);
            match.setNotation(notation);            
            pq.add(match);
            ABCMatch best = pq.peek();
            MattGuiNB.instance().setBestSoFar(best);
            MattGuiNB.instance().log("Best edit distance: " + bestEditdistance + " for search: " + bestBit);
        }
        catch (Exception e)
        {
            MattGuiNB.instance().log("Exception parsing tune: " + tune.getTitles()[0]);
            e.printStackTrace();
        }
    }
    */
    

