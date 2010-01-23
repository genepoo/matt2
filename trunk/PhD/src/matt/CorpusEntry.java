/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

/**
 *
 * @author Bryan Duggan
 */
public class CorpusEntry {
    private String key;
    private String keySignature;
    private String file;
    private int x;
    private String title;
    private String parsons;
    private int index;
    private int[] midiSequence;
    private String midiFileName;
    private int source;

    public CorpusEntry()
    {
        
    }
    
    public void reset()
    {
    }
            
    public CorpusEntry(String entry)
    {

        try
        {
            StringTokenizer stTok = new StringTokenizer(entry, "\t");
            setKey(stTok.nextToken());
            setKeySignature(stTok.nextToken());
            setTitle(stTok.nextToken());
            setFile(stTok.nextToken());
            setX(Integer.parseInt(stTok.nextToken()));
            int source = Integer.parseInt(stTok.nextToken());
            setSource(source);
            setParsons(stTok.nextToken());
            setMidiFileName(stTok.nextToken());
            String midi = stTok.nextToken();
            stTok = new StringTokenizer(midi, ",");

            Vector<Integer> v = new Vector<Integer>();
            while (stTok.hasMoreTokens())
            {
                v.add(new Integer(stTok.nextToken()));
            }
            midiSequence = new int[v.size()];
            for (int i = 0 ; i < v.size(); i ++)
            {
                midiSequence[i] = v.get(i);
            }
        }
        catch (Exception e)
        {
            Logger.log("Could not fully parse line: " + entry);
            e.printStackTrace();
        }        
    }
    public String getKey()
    {
        if (MattProperties.getString("searchMethod").equals("parsons"))
        {
            return parsons;
        }
        if (MattProperties.getString("searchMethod").equals("bryan"))
        {
            return key;
        }

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
        sb.append(keySignature);
        sb.append("\t");
        sb.append(title);
        sb.append("\t");
        sb.append(file);
        sb.append("\t");
        sb.append(x);
        sb.append("\t");
        sb.append(source);
        sb.append("\t");
        sb.append(parsons);      
        sb.append("\t");
        sb.append(midiFileName);
        sb.append("\t");        
        sb.append(MIDITools.instance().arrayToString(midiSequence));
        // sb.append(System.getProperty("line.separator"));
        
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

    public String getParsons() {
        return parsons;
    }

    public void setParsons(String parsons) {
        this.parsons = parsons;
    }

    public int[] getMidiSequence() {
        return midiSequence;
    }

    public void setMidiSequence(int[] midiSequecne) {
        this.midiSequence = midiSequecne;
    }

    public String getMidiFileName()
    {
        return midiFileName;
    }

    public void setMidiFileName(String midiFileName)
    {
        this.midiFileName = midiFileName;
    }

    /**
     * @return the source
     */
    public int getSource()
    {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(int source)
    {
        this.source = source;
    }

    /**
     * @return the keySignature
     */
    public String getKeySignature() {
        return keySignature;
    }

    /**
     * @param keySignature the keySignature to set
     */
    public void setKeySignature(String keySignature) {
        this.keySignature = keySignature;
    }
    
      
}
