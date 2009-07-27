/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.util.*;
import java.io.*;
import java.sql.*;
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
    
    private boolean createMIDI = true;
    private boolean createParsons = true;
    
    
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
        if (MattProperties.getString("mode").equals("client"))
        {
            loadIndex();
        }
        else
        {
            loadDatabaseIndex();
        }
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
    
    public void loadDatabaseIndex()
    {
        index.clear();
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet r = null;
        Logger.log("Loading index from the database");        
        try
        {
            conn = DBHelper.getConnection();
            s = conn.prepareStatement("select * from tuneindex");
            r = s.executeQuery();

            while (r.next())                
            {
                CorpusEntry entry = new CorpusEntry();
                entry.setIndex(r.getInt("id"));
                entry.setFile(r.getString("file"));
                entry.setX(r.getInt("x"));
                entry.setKey(r.getString("key"));
                entry.setTitle(r.getString("name"));
                
                entry.setMidiFileName(r.getString("midiFileName"));
                index.add(entry);                
            }
            Logger.log("Loaded " + index.size() + " tunes into the index");
            ready = true;
        }
        catch (Exception e)
        {
            Logger.log("Could not read index");
            e.printStackTrace();
        }
        DBHelper.safeClose(conn, s, r);
    }

    public void loadIndex()
    {
        index.clear();
        String line = null;
        try
        {
            String curDir = System.getProperty("user.dir");
            String fileName = curDir + System.getProperty("file.separator") + MattProperties.getString("indexFile");
            File indexFile = new File(fileName);

            if (! indexFile.exists())
            {
                reindex();
            }
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            
            line = br.readLine();
            while (line != null)
            {
                CorpusEntry entry = new CorpusEntry(line);
                entry.setIndex(index.size());
                index.add(entry);                
                line = br.readLine();
            }
            br.close();
            Logger.log("Loaded " + index.size() + " tunes into the index");
            ready = true;
        }
        catch (Exception e)
        {
            Logger.log("Could not read index");
            Logger.log("Possible problem with line: " + line);

            e.printStackTrace();
        }
    }
    
    public void reindexDatabase()
    {
        Connection conn = null;
        try
        {
            Logger.log("Reindexing tunes in database...");
            String url = "" + MattProperties.getString("dburl");
            String user = "" + MattProperties.getString("dbuser");
            String password  = "" + MattProperties.getString("dbpassword");
            String driver  = "" + MattProperties.getString("dbdriver");
            
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            //Statement statement = conn.createStatement();
            //statement.execute("delete from tuneindex");
            MattGuiNB.instance().getProgressBar().setValue(0);
            MattGuiNB.instance().getProgressBar().setMaximum(index.size());
            for (int i = 0 ; i < index.size(); i ++)
            {
                MattGuiNB.instance().getProgressBar().setValue(i);
                String curDir = System.getProperty("user.dir");

                String fName = curDir + System.getProperty("file.separator") + MattProperties.getString("SearchCorpus") + System.getProperty("file.separator") + index.get(i).getFile();
                File f = new File(fName);
                TuneBook book = new TuneBook(f);
                int x = index.get(i).getX();
                Tune tune = book.getTune(x);

                PreparedStatement ps = conn.prepareStatement("insert into tuneindex(`file`, `name`, `x`, `notation`, `key`, `midiFileName`, `source`, `type`) values(?, ?, ?, ?, ?,?, ?, ?)");
                ps.setString(1, index.get(i).getFile());
                ps.setString(2, tune.getTitles()[0]);
                ps.setInt(3, x);
                ps.setString(4, book.getTuneNotation(x));
                ps.setString(5, index.get(i).getKey());
                ps.setString(6, index.get(i).getMidiFileName());
                ps.setInt(7, 3);
                ps.setString(8, tune.getRhythm());
                ps.executeUpdate();
                ps.close();
            }
        }
        catch (Exception e)
        {
            Logger.log("Could not update database");
            e.printStackTrace();
        }
        finally
        {
            safeClose(conn, null, null);
        }
        Logger.log("Done...");
    }       
    
    public void reindex()
    {
        new Thread()
        {
            public void run()
            {
                ready = false;
                Logger.log("Reindexing files...");
                
                String folder = MattProperties.getString("MIDIIndex");
                /*
                 File midiDir = new File(folder);
                String[] children = midiDir.list();
                for (int ii = 0; ii < children.length; ii++) 
                {
                    new File(midiDir, children[ii]).delete();
                }
                */
                ABCFilter filter = new ABCFilter();
                index.clear();

                try
                {
                    String curDir = System.getProperty("user.dir");
                    File dir = new File(curDir + System.getProperty("file.separator")  + MattProperties.getString("SearchCorpus"));
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
                    //if (MattProperties.getString("mode").equals("server"))
                    {
                        reindexDatabase();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Could not find files");
                    e.printStackTrace();
                }
                ready = true;
                Logger.log("Indexing complete. " + index.size() + " tunes in the index");
            }            
        }.start();
    }
    
    private void addTunes(File f, FileWriter fw) throws IOException
    {
        Tune tune = null;
        Logger.log("Indexing tunebook: " + f.toString());
        TuneBook tuneBook = new TuneBook(f);        
        int numTunes = tuneBook.size();
        
        int[] tuneRefs = tuneBook.getReferenceNumbers();
        for (int i = 0 ; i < tuneRefs.length ; i ++)
        {
            try
            {
                tune = tuneBook.getTune(tuneRefs[i]);
                Logger.log("Indexing tune: " + tune.getReferenceNumber() + " " + tune.getTitles()[0]);                   
                String notation = tuneBook.getTuneNotation(tuneRefs[i]);

                int tuneStart = MattABCTools.skipHeaders(notation);
                String key = notation.substring(tuneStart);
                String head = notation.substring(0, tuneStart);
                int iVariation = key.toUpperCase().indexOf("\"V");
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
                        createCorpusEntry(fw, head, subKey, f.getName(), tune.getTitles()[0], tune.getReferenceNumber());                    
                        // Find the end of the comment
                        iVariation = key.indexOf("\"", iVariation + 1);
                        start = iVariation + 1;
                        // Now find the next variation
                        iVariation = key.indexOf("\"", start);
                        if (iVariation == -1)
                        {
                            endOfTune = true;
                            subKey = key.substring(start, key.length());                    
                            createCorpusEntry(fw, head, subKey, f.getName(), tune.getTitles()[0], tune.getReferenceNumber());                    
                        }
                    }
                }
                else
                {                
                    // Create an entry for the whole tune
                    createCorpusEntry(fw, head, key, f.getName(), tune.getTitles()[0], tune.getReferenceNumber());                    
                }
            }
            catch (Exception e)
            {
                if (tune != null)
                {
                        Logger.log("Problem indexing tune " + tune.getReferenceNumber() + " " + tune.getTitles()[0] + " or the one after it.");
                }
                else
                {
                    Logger.log("Problem indexing a tune");
                }
                e.printStackTrace();
            }
        }
    }
    
    private void createCorpusEntry(FileWriter fw, String head, String key, String fileName, String title, int x) throws Exception
    {
        String parsons = null;
        String midiFile = MIDITools.instance().createMIDI(head, key, fileName, title, x);
        int[] midiSequence = MIDITools.instance().toMIDISequence(midiFile);
        parsons = MIDITools.instance().toParsons(midiSequence);
        
        try
        {
            key = MattABCTools.stripComments(key);

            key = MattABCTools.stripWhiteSpace(key);
            key = MattABCTools.expandLongNotes(key);
            key = MattABCTools.expandParts(key);
            key = MattABCTools.stripBarDivisions(key);
            key = MattABCTools.removeTripletMarks(key);
            key = MattABCTools.removeExtraNotation(key);
            key = key.toUpperCase();
            key = key.replace("WWWFROMMUSICAVIVAHTTPWWW.MUSICAVIVA.COMWTHEINTERNETCENTERFORFREESHEETMUSICDOWNLOADS.", "");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            key = "";
        }
        if (key.length() == 0)
        {
            Logger.log("Could not index: " + title);
        }
        else
        {            
            CorpusEntry ce = new CorpusEntry();
            ce.setFile(fileName);
            ce.setTitle(title);
            ce.setX(x);
            ce.setKey(key);
            ce.setParsons(parsons);
            ce.setMidiSequence(midiSequence);
            ce.setMidiFileName(midiFile);
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
    
    public static void safeClose(Connection c, Statement s, ResultSet r) {
        if (r != null) {
            try {
                r.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (s != null) {
            try {
                s.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (c != null) {
            try {
                c.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }             
    }
}