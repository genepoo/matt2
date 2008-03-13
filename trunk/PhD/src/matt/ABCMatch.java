/*
 * ABCMatch.java
 *
 * Created on 17 July 2007, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;
import abc.notation.Tune;
import abc.parser.TuneBook;
import java.util.*;
import java.io.*;
/**
 *
 * @author Bryan
 */
public class ABCMatch implements Comparator {
    
    private String fileName;
    private String notation = null;
    private String line;
    private String title;
    private float editDistance;
    private int x;
    private int index;
    private int which = -1;
    private int repititions = -1;
    
    private Tune tune;
    
    /** Creates a new instance of ABCMatch */
    public ABCMatch() {
        
    }    

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Tune getTune() {
        if (tune == null)
        {
            Logger.log("Lazy loading tune: " + getX());
            try
            {           
                String fName = "" + MattProperties.instance().get("SearchCorpus") + System.getProperty("file.separator") + getFileName();
                File f = new File(fName);
                TuneBook book = new TuneBook(f);
                setTune(book.getTune(x));
                setNotation(book.getTuneNotation(x));
            }
            catch (Exception e)
            {
                Logger.log("Lazy loading failed");            
                e.printStackTrace();                
            }
            
        }
        return tune;
    }

    public void setTune(Tune tune) {
        this.tune = tune;
    }

    public String getNotation() {
        getTune();
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getLine()
    {
        return line;
    }

    public void setLine(String line)
    {
        this.line = line;
    }

    public float getEditDistance()
    {
        return editDistance;
    }

    public void setEditDistance(float editDistance)
    {
        this.editDistance = editDistance;
    }
    
    // To implement the Comparitor interface

    public int compare(Object o0, Object  o1)
    {
        ABCMatch match0 = (ABCMatch) o0;
        ABCMatch match1 = (ABCMatch) o1;
        
        if (match0.getRepititions() == -1)
        {
            if (match0.getEditDistance() < match1.getEditDistance())
            {
                return -1;
            }
            if (match0.getEditDistance() == match1.getEditDistance())
            {
                return 0;
            }
            return 1;
        }
        else
        {
            if (match0.getWhich() < match1.getWhich())
            {
                return -1;
            }
            if (match0.getWhich() == match1.getWhich())
            {
                return 0;
            }
            return 1;
        }
    }
    
    public String toString()
    {
        String ret = "Title: " + getTitle() + " File: " + getFileName() + " ED: " + getEditDistance();
        if (which != -1)
        {
            ret += " Set order: " + which + " Repetitions: " + getRepititions();
        }
        ret += " Line: " + getLine();
        return ret;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getWhich()
    {
        return which;
    }

    public void setWhich(int which)
    {
        this.which = which;
    }

    public int getRepititions()
    {
        return repititions;
    }

    public void setRepititions(int repititions)
    {
        this.repititions = repititions;
    }
}
