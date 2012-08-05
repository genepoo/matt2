/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.File;
import java.io.IOException;
import java.sql.*;
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
    private String tunePalID;
    private String type;
    private String keySignature;
    private String file;
    private int x;
    private String title;
    private String altTitle;
    private String parsons;
    private String notation;
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
    
    public void updateFromNotation() {
        String lines[] = notation.split("\\r?\\n");
        boolean titleSet = false;
        boolean keySet = false;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toUpperCase().startsWith("K:") && (keySet == false)) {
                this.setKeySignature(lines[i].substring(2).trim());
                keySet = true;
                break;
            }
            if (lines[i].toUpperCase().startsWith("R:")) {
                this.setType(lines[i].substring(2).trim());
                continue;
            }
            if (lines[i].toUpperCase().startsWith("X:"))
            {
            	try
            	{
            		this.setX(Integer.parseInt(lines[i].substring(2).trim()));
            	}
            	catch (NumberFormatException e)
            	{
            		e.printStackTrace();
            	}
            }
            if (lines[i].toUpperCase().startsWith("T:")) {
                if (!titleSet) {
                    this.setTitle(lines[i].substring(2).trim());
                    titleSet = true;
                } else {
                    this.setAltTitle(lines[i].substring(2).trim());
                }
                continue;
            }
        }
    }
            
    public CorpusEntry(ResultSet rs)
    {
        int id = -1;
        try
        {
            id = rs.getInt("id");
            setTitle(rs.getString("title"));
            setAltTitle(rs.getString("alt_title"));
            setKey(rs.getString("search_key"));
            setFile(rs.getString("file_name"));
            setX(rs.getInt("x"));
            setNotation(rs.getString("notation"));
            setKeySignature(rs.getString("key_sig"));            
            setSource(rs.getInt("source"));
            setMidiFileName(rs.getString("midi_file_name"));
            setParsons(rs.getString("parsons"));
            setTunePalID(rs.getString("tunepalid"));            
            setType(rs.getString("tune_type"));
            String midiSequence = rs.getString("midi_sequence");

            StringTokenizer stTok = new StringTokenizer(midiSequence, ",");
            Vector<Integer> v = new Vector<Integer>();
            while (stTok.hasMoreTokens())
            {
                v.add(new Integer(stTok.nextToken()));
            }
            int midiNotes[] = new int[v.size()];
            for (int i = 0 ; i < v.size(); i ++)
            {
                midiNotes[i] = v.get(i);
            }
            setMidiSequence(midiNotes);

        }
        catch (Exception e)
        {
            Logger.log("Could not load record: " + id);
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

    /**
     * @return the tunePalID
     */
    public String getTunePalID() {
        return tunePalID;
    }

    /**
     * @param tunePalID the tunePalID to set
     */
    public void setTunePalID(String tunePalID) {
        this.tunePalID = tunePalID;
    }

    /**
     * @return the altTitle
     */
    public String getAltTitle() {
        return altTitle;
    }

    /**
     * @param altTitle the altTitle to set
     */
    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    /**
     * @return the notation
     */
    public String getNotation() {
        return notation;
    }

    /**
     * @param notation the notation to set
     */
    public void setNotation(String notation) {
        this.notation = notation;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
      
}
