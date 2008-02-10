/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.*;
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
    Transcriber transcriber = null;

    public BatchJob()
    {
    }
    
    public boolean chooseFolder()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
        Results results = null;
        running = true;
        try
        {
            results = new Results();
            File[] files;
            if (folder.isDirectory())
            {
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
                transcriber = new Transcriber();

                transcriber.setInputFile(files[i].toString());
                MattGuiNB.instance().setTranscriber(transcriber);
                transcriber.loadAudio();
                transcriber.transcribe();
                finder.setSearchString(MattGuiNB.instance().getTxtABC().getText());
                finder.setStartIn(MattProperties.instance().get("SearchCorpus").toString());
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
                    result.append(match.getTitle());
                    result.append(delim);
                    result.append(match.getX());
                    result.append(delim);                   
                    result.append(match.getEditDistance());
                    result.append(delim);                   
                }
                results.log(result.toString());
            }
        }
        catch (Exception e)
        {
            MattGuiNB.log("Problem with batch job");
            e.printStackTrace();
        }
        finally
        {
            results.close();
        }
        MattGuiNB.log("Garbage collecting...");
        System.gc();
        MattGuiNB.log("Done.");
        running = false;
    }

    public boolean isRunning()
    {
        return running || finder.isRunning();
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
}
