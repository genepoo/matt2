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
    private double editDistance;
    private int x;
    
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
            MattGuiNB.log("Lazy loading tune: " + getX());
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
                MattGuiNB.log("Lazy loading failed");            
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

    public double getEditDistance()
    {
        return editDistance;
    }

    public void setEditDistance(double editDistance)
    {
        this.editDistance = editDistance;
    }
    
    // To implement the Comparitor interface

    public int compare(Object o0, Object  o1)
    {
        ABCMatch match0 = (ABCMatch) o0;
        ABCMatch match1 = (ABCMatch) o1;
        
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
    
    public String toString()
    {
        return "Title: " + getTitle() + " File: " + getFileName() + " ED: " + getEditDistance() + " Line: " + getLine();
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
}
