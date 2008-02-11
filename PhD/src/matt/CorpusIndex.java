/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.util.*;
import java.io.*;
import abc.notation.Tune;
import abc.parser.TuneBook;

/**
 *
 * @author Bryan Duggan
 */
public class CorpusIndex {
    Vector<CorpusEntry> index = new Vector();
    static CorpusIndex _instance = null;;
    int current;
    
    private boolean ready;
        
    public static CorpusIndex instance()
    {
        if (_instance == null)
        {
            _instance = new CorpusIndex();            
        }
        return _instance;
    }
    
    public int getCurrentIndex()
    {
        return current;
    }
    
    private CorpusIndex()
    {
        loadIndex();
    }
    
    public void reset()
    {
        current = 0;
    }
    
    public int size()
    {
        return index.size();
    }
     
    public CorpusEntry get(int i)
    {
        return index.get(i);
    }
    
    public synchronized CorpusEntry getNext()
    {
        if (current >= index.size())
        {
            return null;
        }
        else
        {
            return index.get(current ++);
            
        }
    }
    public void loadIndex()
    {
        index.clear();
        try
        {
            String fileName = "" + MattProperties.instance().get("indexFile");
            File indexFile = new File(fileName);

            if (! indexFile.exists())
            {
                reindex();
            }
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            String line;
            line = br.readLine();
            while (line != null)
            {
                CorpusEntry entry = new CorpusEntry(line);
                index.add(entry);
                line = br.readLine();
            }
            br.close();
            MattGuiNB.log("Loaded " + index.size() + " tunes into the index");
            ready = true;
        }
        catch (Exception e)
        {
            MattGuiNB.log("Could not read index");
            e.printStackTrace();
        }
    }
    
    public void reindex()
    {
        new Thread()
        {
            public void run()
            {
                ready = false;
                MattGuiNB.instance().log("Reindexing files...");
            
                ABCFilter filter = new ABCFilter();
                index.clear();

                try
                {
                    File dir = new File("" + MattProperties.instance().get("SearchCorpus"));
                    File indexFile = new File("" + MattProperties.instance().get("indexFile"));
                    if (indexFile.exists())
                    {
                        indexFile.delete();
                    }
                    FileWriter fw = new FileWriter(indexFile);           
                    File[] files = dir.listFiles(filter);

                    for (int i = 0 ; i < files.length ; i ++)
                    {
                        addTunes(files[i],  fw);
                    }
                    fw.close();
                }
                catch (Exception e)
                {
                    System.out.println("Could not find files");
                    e.printStackTrace();
                }
                ready = true;
                MattGuiNB.log("Indexing complete");
            }            
        }.start();
    }
    
    private void addTunes(File f, FileWriter fw) throws IOException
    {
        Tune tune = null;
        MattGuiNB.log("Indexing tunebook: " + f.toString());
        TuneBook tuneBook = new TuneBook(f);        
        int numTunes = tuneBook.size();
        
        int[] tuneRefs = tuneBook.getReferenceNumbers();
        for (int i = 0 ; i < tuneRefs.length ; i ++)
        {
            try
            {
                tune = tuneBook.getTune(tuneRefs[i]);
                MattGuiNB.log("Indexing tune: " + tune.getReferenceNumber() + " " + tune.getTitles()[0]);                   
                String notation = tuneBook.getTuneNotation(tuneRefs[i]);

                String key = notation;

                key = MattABCTools.skipHeaders(key);
                int iVariation = key.indexOf("\"");
                int start = 0;

                // The comment is at the start, so skip it
                if (iVariation == 0)
                {
                    iVariation = key.indexOf("\"", iVariation + 1);
                    key = key.substring(iVariation + 1);
                    iVariation = key.indexOf("\"");
                }
                if (iVariation!= -1)
                {
                    boolean endOfTune = false;
                    while (! endOfTune)
                    {
                        String subKey = key.substring(start, iVariation);                    
                        createCorpusEntry(fw, subKey, f.getName(), tune.getTitles()[0], tune.getReferenceNumber());                    
                        // Find the end of the comment
                        iVariation = key.indexOf("\"", iVariation + 1);
                        start = iVariation + 1;
                        // Now find the next variation
                        iVariation = key.indexOf("\"", start);
                        if (iVariation == -1)
                        {
                            endOfTune = true;
                            subKey = key.substring(start, key.length());                    
                            createCorpusEntry(fw, subKey, f.getName(), tune.getTitles()[0], tune.getReferenceNumber());                    
                        }
                    }
                }
                else
                {                
                    // Create an entry for the whole tune
                    createCorpusEntry(fw, key, f.getName(), tune.getTitles()[0], tune.getReferenceNumber());                    
                }
            }
            catch (Exception e)
            {
                if (tune != null)
                {
                        MattGuiNB.log("Problem indexing tune " + tune.getReferenceNumber() + " " + tune.getTitles()[0] + " or the one after it.");
                }
                else
                {
                    MattGuiNB.log("Problem indexing a tune");
                }
                e.printStackTrace();
            }
        }
    }
    
    private void createCorpusEntry(FileWriter fw, String key, String fileName, String title, int x) throws IOException
    {
        key = MattABCTools.stripComments(key);
        key = MattABCTools.stripWhiteSpace(key);
        key = MattABCTools.expandLongNotes(key);
        key = MattABCTools.expandParts(key);
        key = MattABCTools.stripBarDivisions(key);
        key = MattABCTools.removeTripletMarks(key);        
        key = key.toUpperCase();

        if (key.length() == 0)
        {
            MattGuiNB.log("Could not index: " + title);
        }
        else
        {            
            CorpusEntry ce = new CorpusEntry();
            ce.setFile(fileName);
            ce.setTitle(title);
            ce.setX(x);
            ce.setKey(key);
            fw.write(ce.toIndexFile());
            fw.flush();                
            index.add(ce);
        }
    }

    public boolean isReady()
    {
        return ready;
    }

    public void setReady(boolean ready)
    {
        this.ready = ready;
    }
}