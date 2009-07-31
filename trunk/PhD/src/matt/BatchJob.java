/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt; 
import java.io.*;
import java.sql.*;
import java.util.PriorityQueue;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
/**
 *
 * @author Bryan Duggan
 */
public class BatchJob extends Thread
{
    private File folder = null;
    private boolean running = false;
    
    ABCFinder finder = null;
    ODCFTranscriber transcriber = null;
    private long jobId;
    public static final int NEW = 0;
    public static final int TRANSCRIBING = 1;
    public static final int SEARCHING = 2;
    public static final int DONE = 3;
    public static final int ERROR  = 4;

    private boolean serverMode;
   
    public static Results results = null;

    public BatchJob()
    {
        serverMode = false;
        jobId = -1;
    }
    
    public boolean chooseFolder()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setSelectedFile(new File("" + MattProperties.instance().get("BatchPath")));
        int returnVal = fc.showOpenDialog(MattGuiNB.instance());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            setFolder(fc.getSelectedFile());
            MattProperties.instance().setProperty("BatchPath", "" + folder);
            MattProperties.instance().save();
            return true;
        }
        else
        {
            return false;
        }        
    }
    
    public void run()
    {
        WavFilter filter = new WavFilter();
        
        running = true;
        Connection conn = null;
        try
        {
            if (serverMode)
            {
                conn = DBHelper.getConnection();
                PreparedStatement s = conn.prepareStatement("update job set status = ? where id = ?");
                s.setLong(1, BatchJob.TRANSCRIBING);
                s.setLong(2, jobId);
                s.execute();
            }
            results = new Results();
            results.log("" + MattProperties.instance());
            File[] files;
            if (folder.isDirectory())
            {
                files = folder.listFiles(filter);
            }
            else if (folder.toString().endsWith(".zip"))
            {
                UnZipper.unzip("" + folder);
                folder = new File(UnZipper.getPath());
                files = folder.listFiles(filter);
            }            
            else
            {
                files = new File[1];
                files[0] = folder;
            }
            for (int i = 0 ; i < files.length ; i ++)
            {
                MattGuiNB.instance().clearGraphs();
                MattGuiNB.instance().clearMatches();
                MattGuiNB.instance().getTxtABC().setText("");
                if (transcriber != null)
                {
                    transcriber.cleanup();
                }
                finder = new ABCFinder();
                transcriber = MattGuiNB.instance().getTranscriber();
                
                transcriber.setInputFile(files[i].toString());
                MattGuiNB.instance().setTranscriber(transcriber);
                transcriber.loadAudio();
                transcriber.transcribe();
                finder.setSearchString(transcriber.getAbcTranscription());
                finder.setStartIn(MattProperties.instance().get("SearchCorpus").toString());
                if (serverMode)
                {
                    conn = DBHelper.getConnection();
                    PreparedStatement s = conn.prepareStatement("update job set status = ?, transcription = ? where id = ?");
                    s.setLong(1, BatchJob.SEARCHING);
                    s.setString(2, transcriber.getAbcTranscription());
                    s.setLong(3, jobId);
                    s.execute();
                }
                finder.setTranscribedNotes(transcriber.getTranscribedNotes());
                finder.findFromIndex();
                if (!running)
                {
                    break;
                }                
                PriorityQueue<ABCMatch> matches = finder.getPq();
                StringBuffer result = new StringBuffer();
                String delim = "\t";
                result.append(files[i].getName());
                result.append(delim);
                for (int j = 0 ; j < 10 ; j ++)
                {                    
                    ABCMatch match = matches.poll();
                    if (match != null)
                    {
                        result.append(match.getTitle());
                        result.append(delim);
                        result.append(match.getX());
                        if (match.getRepititions() != -1)
                        {
                            result.append(delim);    
                            result.append(match.getWhich());
                            result.append(delim);  
                            result.append(match.getRepititions());
                        }
                        result.append(delim);                   
                        result.append(match.getEditDistance());
                        result.append(delim);   
                        float normalisedEd = ((float) match.getEditDistance() / (float) transcriber.getTranscribedNotes().length) * 100.0f;
                        result.append(normalisedEd);
                        result.append(delim);
                        if (serverMode)
                        {
                            conn = DBHelper.getConnection();
                            String sql = "INSERT INTO `tunometer`.`match` (`jobId` ,`tuneId` ,`ed`, `correct`) VALUES (?, ?, ?, ?)";
                            PreparedStatement s = conn.prepareStatement(sql);
                            s.setLong(1, jobId);
                            s.setLong(2, match.getIndex());
                            s.setFloat(3, match.getEditDistance());                        
                            s.setBoolean(4, false);                        
                            s.execute();
                            DBHelper.safeClose(conn, s, null);
                        }
                    }
                }
                results.log(result.toString());
                if (serverMode)
                {
                    conn = DBHelper.getConnection();
                    PreparedStatement s = conn.prepareStatement("update job set status = ? where id = ?");                    
                    s.setLong(1, BatchJob.DONE);
                    s.setLong(2, jobId);
                    s.execute();
                }
                DBHelper.safeClose(conn, null, null);
            }
        }
        catch (Exception e)
        {
            Logger.log("Problem with batch job");
            e.printStackTrace();
        }
        finally
        {
            results.close();
        }
        Logger.log("Garbage collecting...");
        System.gc();
        Logger.log("Done.");
        running = false;
    }

    public boolean isRunning()
    {
        return running || ((finder != null) && finder.isRunning());
    }

    public void setRunning(boolean running)
    {        
        this.running = running;
        if (finder != null)
        {    
            finder.setRunning(running);
        }
    }

    public File getFolder()
    {
        return folder;
    }

    public void setFolder(File folder)
    {
        this.folder = folder;
    }

    public long getJobId()
    {
        return jobId;
    }

    public void setJobId(long jobId)
    {
        this.jobId = jobId;
    }

    public boolean isServerMode()
    {
        return serverMode;
    }

    public void setServerMode(boolean serverMode)
    {
        this.serverMode = serverMode;
    }
}
