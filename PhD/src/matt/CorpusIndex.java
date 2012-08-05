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

    private String lastUniqueId = "IGNOREME";
    private int  lastTuneId = -1;

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
        loadDatabaseIndex();       
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
            s = conn.prepareStatement("select tuneindex.id as id, notation, tunepalid, source, file_name, x, key_sig, title, alt_title, midi_file_name, search_key, parsons, tune_type, midi_sequence from tuneindex, tunekeys where tunekeys.tuneid = tuneindex.id");
            r = s.executeQuery();

            while (r.next())                
            {
                CorpusEntry entry = new CorpusEntry(r);
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

    /*
     public void loadIndex()
    {
        index.clear();
        String line = null;
        try
        {
            String curDir = System.getProperty("user.dir");
            String fileName = curDir + System.getProperty("file.separator") + MattProperties.getString("indexFile");
            File indexFile = new File(fileName);

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
     */

    public void makeSQLiteDatabase()
    {
        Connection conn = null;

        lastUniqueId = "DUMMY";
        int tuneid = -1;
        try
        {
            Class.forName("org.sqlite.JDBC");
            Logger.log("Creating iTunePal index...");
            conn = DriverManager.getConnection("jdbc:sqlite:itunepal.sqlite");          
            MattGuiNB.instance().getProgressBar().setValue(0);
            MattGuiNB.instance().getProgressBar().setMaximum(index.size());
            for (int i = 0 ; i < index.size(); i ++)
            {
                System.out.println(i);
                MattGuiNB.instance().getProgressBar().setValue(i);
                CorpusEntry ce = index.get(i);
                if ((ce.getSource() == 2) && ("reel".equalsIgnoreCase(ce.getType()) || "jig".equalsIgnoreCase(ce.getType() )))
                {

                    if (!ce.getTunePalID().equals(lastUniqueId) )
                    {
                        PreparedStatement ps = conn.prepareStatement("insert into tuneindex(title, alt_title, x, notation, source, tunepalid) values(?, ?, ?, ?, ?,?)");
                        ps.setString(1, ce.getTitle());
                        ps.setString(2, ce.getAltTitle());
                        ps.setInt(3, ce.getX());
                        ps.setString(4, ce.getNotation());
                        ps.setInt(5, ce.getSource());
                        ps.setString(6, ce.getTunePalID());
                        ps.executeUpdate();
                        ps.close();

                        Statement statement = conn.createStatement();
                        ResultSet rs = statement.executeQuery("select max(id) from tuneindex");
                        tuneid= rs.getInt(1);
                        lastUniqueId = ce.getTunePalID();
                    }

                    PreparedStatement ps = conn.prepareStatement("insert into tunekeys(search_key, tuneid) values (?, ?)");
                    ps.setString(1, ce.getKey());
                    ps.setInt(2, tuneid);
                    ps.executeUpdate();
                    ps.close();
                }
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
        Connection conn = null;
        int lastX = -1;
        String lastTitle = "";
        int tuneid = -1;
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

            String folder = MattProperties.getString("MIDIIndex");

            // Delete all the MIDI files
            Logger.log("Deleting MIDI files...");
             File midiDir = new File(folder);
            String[] children = midiDir.list();
            for (int ii = 0; ii < children.length; ii++)
            {
                new File(midiDir, children[ii]).delete();
            }
            Logger.log("Done");
            ABCFilter filter = new ABCFilter();
            index.clear();

            String curDir = System.getProperty("user.dir");
            File dir = new File(curDir + System.getProperty("file.separator")  + MattProperties.getString("SearchCorpus"));

            //PrintWriter fw = new PrintWriter(indexFile);
            File[] dirs = dir.listFiles();

            MattGuiNB.instance().getProgressBar().setMaximum(dirs.length);

            for (int i = 0 ; i < dirs.length ; i ++)
            {
                MattGuiNB.instance().getProgressBar().setValue(i);

                if (dirs[i].isDirectory())
                {
                    int source = -1;
                    try
                    {
                        String numberPart = ("" + dirs[i]);
                        numberPart = numberPart.substring(numberPart.lastIndexOf(System.getProperty("file.separator")) + 1);
                        source = Integer.parseInt("" + numberPart);
                    }
                    catch (Exception e)
                    {
                        Logger.log(dirs[i] + " is not a number, so I'm skipping that folder");
                        continue;
                    }
                    File[] files = dirs[i].listFiles(filter);
                    for (int j = 0 ; j < files.length ; j ++)
                    {
                        addTunes(source, files[j],  conn);
                    }
                }
                else
                {
                    addTunes(-1, dirs[i],  conn);
                }
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
    
    /*public void reindex()
    {
        System.out.print(this);
        new Thread()
        {
            public void run()
            {

                ready = false;
                Logger.log("Reindexing...");
                
                String folder = MattProperties.getString("MIDIIndex");

                Logger.log("Deleting MIDI files...");
                 File midiDir = new File(folder);
                String[] children = midiDir.list();
                for (int ii = 0; ii < children.length; ii++) 
                {
                    new File(midiDir, children[ii]).delete();
                }
                Logger.log("Done");
                ABCFilter filter = new ABCFilter();
                index.clear();

                try
                {
                    String curDir = System.getProperty("user.dir");
                    File dir = new File(curDir + System.getProperty("file.separator")  + MattProperties.getString("SearchCorpus"));
                    
                    //PrintWriter fw = new PrintWriter(indexFile);
                    File[] dirs = dir.listFiles();

                    for (int i = 0 ; i < dirs.length ; i ++)
                    {
                        if (dirs[i].isDirectory())
                        {
                            int source = -1;
                            try
                            {
                                String numberPart = ("" + dirs[i]);
                                numberPart = numberPart.substring(numberPart.lastIndexOf(System.getProperty("file.separator")) + 1);
                                source = Integer.parseInt("" + numberPart);
                            }
                            catch (Exception e)
                            {
                                Logger.log(dirs[i] + " is not a number, so I'm skipping that folder");
                                continue;
                            }
                            File[] files = dirs[i].listFiles(filter);
                            for (int j = 0 ; j < files.length ; j ++)
                            {
                                addTunes(source, files[j],  fw);
                            }
                        }
                        else
                        {
                            addTunes(-1, dirs[i],  fw);
                        }
                        
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
    */
    
    private void addTunes(int source, File f, Connection conn) throws IOException, SQLException
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
                        createCorpusEntry(source, head, subKey, f.getName(), tune.getTitles()[0], tune.getReferenceNumber(), tune, tuneBook, conn);
                        // Find the end of the comment
                        iVariation = key.indexOf("\"", iVariation + 1);
                        start = iVariation + 1;
                        // Now find the next variation
                        iVariation = key.indexOf("\"", start);
                        if (iVariation == -1)
                        {
                            endOfTune = true;
                            subKey = key.substring(start, key.length());                    
                            createCorpusEntry(source, head, subKey, f.getName(), tune.getTitles()[0], tune.getReferenceNumber(), tune, tuneBook, conn);
                        }
                    }
                }
                else
                {                
                    // Create an entry for the whole tune
                    createCorpusEntry(source, head, key, f.getName(), tune.getTitles()[0], tune.getReferenceNumber(), tune, tuneBook, conn);
                }
            }
            catch (Exception e)
            {
                if (tune != null)
                {
                    if (tune.getTitles() != null)
                    {
                        Logger.log("Problem indexing tune " + tune.getReferenceNumber() + " " + tune.getTitles()[0] + " or the one after it.");
                    }
                    else
                    {
                        Logger.log("Problem indexing tune " + tune.getReferenceNumber());
                    }


                }
                else
                {
                    Logger.log("Problem indexing a tune");
                }
                e.printStackTrace();
            }
        }
    }

    static String createUniqueTunePalID(int x, int corpus, String fileName, String title)
    {
        String tunePalId = "";        
        title = title.replace(System.getProperty("file.separator").charAt(0), '~');
        title = title.replace('\'', '~');
        title = title.replace('?', '~');
        title = title.replace('"', '~');
        title = title.replace(' ', '~');
        tunePalId = "" + x + "-" + fileName + "-" + corpus + "-" + title;
        return tunePalId;
    }

    private void createCorpusEntry(int source, String head, String body, String fileName, String title, int x, Tune tune, TuneBook book, Connection conn) throws Exception
    {
        String parsons = null;
        String uniqueId = createUniqueTunePalID(x, source, fileName, title);
        String midiFile = MIDITools.instance().createMIDI(head, body, fileName, title, x, uniqueId);
        int[] midiSequence = MIDITools.instance().toMIDISequence(midiFile);
        parsons = MIDITools.instance().toParsons(midiSequence);
        int tuneid = -1;        
       try
        {
            body = MattABCTools.stripComments(body);

            body = MattABCTools.stripWhiteSpace(body);
            body = MattABCTools.expandLongNotes(body);
            body = MattABCTools.expandParts(body);
            body = MattABCTools.stripBarDivisions(body);
            body = MattABCTools.removeTripletMarks(body);
            body = MattABCTools.removeExtraNotation(body);
            //key = MattABCTools.removeLongNotes(key);
            body = body.toUpperCase();
            body = body.replace("WWWFROMMUSICAVIVAHTTPWWW.MUSICAVIVA.COMWTHEINTERNETCENTERFORFREESHEETMUSICDOWNLOADS.", "");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            body = "";
        }
        if (body.length() == 0)
        {
            Logger.log("Could not index: " + title);
        }
        else
        {            
            CorpusEntry ce = new CorpusEntry();
            ce.setFile(fileName);
            ce.setSource(source);

            String titles[] = tune.getTitles();
            if (titles.length > 1)
            {
                ce.setAltTitle(tune.getTitles()[1]);
            }
            ce.setTitle(title);
            ce.setX(x);
            ce.setKey(body);
            ce.setParsons(parsons);
            ce.setMidiSequence(midiSequence);
            ce.setMidiFileName(midiFile);
            ce.setKeySignature(tune.getKey().toLitteralNotation());
            // Is it a new tune, so insert a tune into the tuneindex table
            if ((!lastUniqueId.equals(uniqueId)))
            {

                //PreparedStatement ps = conn.prepareStatement("insert into tuneindex(`file_name`, `title`, `alt_title`, `x`, `notation`, `source`, `tune_type`, `tunepalid`, `key_sig`) values(?, ?, ?,?, ?, ?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement ps = conn.prepareStatement("insert into tuneindex(`file_name`, `title`, `alt_title`, `x`, `notation`, `source`, `tune_type`, `tunepalid`, `key_sig`) values(?, ?, ?,?, ?, ?,?,?,?)");
                ps.setString(1, ce.getFile());
                ps.setString(2, ce.getTitle());
                ps.setString(3, ce.getAltTitle());
                ps.setInt(4, x);
                ps.setString(5, book.getTuneNotation(x));
                ps.setInt(6, source);
                ps.setString(7, tune.getRhythm());
                ps.setString(8, uniqueId);
                ps.setString(9, ce.getKeySignature());
                ps.executeUpdate();

                tuneid = getLastId("tuneindex", conn);
                /*ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                {
                    tuneid = rs.getInt(1);
                }
                else {
                    System.out.println("There are no generated keys.");
                    System.exit(0);
                }
                 */
                ps.close();
                lastUniqueId = uniqueId;
                lastTuneId = tuneid;
            }
            // Insert the key into the keys table and link it to the tune
            PreparedStatement ps = conn.prepareStatement("insert into tunekeys(`search_key`, `tuneid`, `midi_file_name`, `midi_sequence`, `parsons`) values(?, ?, ?, ?, ?)");
            ps.setString(1, body);
            ps.setInt(2, lastTuneId);
            ps.setString(3, midiFile);
            ps.setString(4, MIDITools.instance().arrayToString(ce.getMidiSequence()));
            ps.setString(5, ce.getParsons());
            ps.executeUpdate();
            ps.close();
        }
    }

    public int getLastId(String table, Connection conn) throws SQLException
    {
        int lastId = 0;
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select max(id) as max from " + table);
        if (rs.next())
        {
            lastId = rs.getInt("max");
        }
        return lastId;

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