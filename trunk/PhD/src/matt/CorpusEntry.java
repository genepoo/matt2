/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.util.StringTokenizer;

/**
 *
 * @author Bryan Duggan
 */
public class CorpusEntry {
    private String key;
    private String file;
    private int x;
    private String title;
    private int index;

    public CorpusEntry()
    {
        
    }
    
    public void reset()
    {
    }
            
    public CorpusEntry(String entry)
    {
        StringTokenizer stTok = new StringTokenizer(entry, "\t");
        
        setKey(stTok.nextToken());
        setTitle(stTok.nextToken());
        setFile(stTok.nextToken());
        setX(Integer.parseInt(stTok.nextToken()));
    }
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
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
    
    public String toIndexFile()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(key);
        sb.append("\t");
        sb.append(title);
        sb.append("\t");
        sb.append(file);
        sb.append("\t");
        sb.append(x);
        sb.append(System.getProperty("line.separator"));
        
        return sb.toString();                
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
}
