/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.*;

import javax.sound.midi.*;

/**
 *
 * @author Bryan
 */
public class MIDITools {
    private static MIDITools _instance;
    
    public static MIDITools instance()
    {
        if (_instance == null)
        {
            _instance = new MIDITools();
        }
        return _instance;
    }

    public String createMIDI(String head, String key, String fileName, String title, int x) throws IOException, InterruptedException
    {
        head = head.trim() + "\r";
        key = key.trim();
        String folder = MattProperties.getString("MIDIIndex");
        
        
        String tempFile = folder + System.getProperty("file.separator") + "temp.abc";
        FileWriter fw = new FileWriter(tempFile);
        fw.write(head);
        fw.write(key);
        fw.flush();
        fw.close();
        String midiFileName = "";
        int variation = 0;
        boolean unique = false;
        title = title.replace(System.getProperty("file.separator").charAt(0), '~');
        title = title.replace('\'', '~');
        title = title.replace('?', '~');
        title = title.replace('"', '~');
        do
        {
            
            midiFileName = folder + System.getProperty("file.separator") + x + "-" + fileName + "-" + title;
            if (variation > 0)
            {
                midiFileName += "-Variation " + variation;
            }
            midiFileName += ".mid";
            
            if (new File(midiFileName).exists())
            {
                variation ++;
            }
            else
            {
                unique = true;
            }
        }
        while (! unique);
        String cmd = MattProperties.getString("ABC2MIDI") + " " + tempFile + " -o " + "\"" + midiFileName + "\"";
        Process abc2MIDI  = Runtime.getRuntime().exec(cmd);
        abc2MIDI.waitFor();
        if (! new File(midiFileName).exists())
        {
            Logger.log(midiFileName + " not created");
        }
        
        return midiFileName;
    }
    public String toParsons(String file) throws InvalidMidiDataException, IOException 
    {
        StringBuffer parsons = new StringBuffer("");
        Sequence sequence = MidiSystem.getSequence(new File(file));
        Track[] tracks = sequence.getTracks();
        int previousNote = -1;
        for(int i = 0 ; i < tracks[0].size(); i ++)
        {
            MidiMessage mm = tracks[0].get(i).getMessage();
            int len = mm.getLength();
            int status = mm.getStatus();
            if (status == ShortMessage.NOTE_ON)
            {                
                byte[] b = mm.getMessage();
                int currentNote = b[1];
                
                // No parsons code for the first note
                if (previousNote != -1)
                {
                    if (currentNote > previousNote)
                    {
                        parsons.append("U");
                    }
                    else if (currentNote < previousNote)
                    {
                        parsons.append("D");
                    }
                    else
                    {
                        parsons.append("S");
                    }
                }
                previousNote = currentNote;
                // System.out.println(b);
            }
            
        }
        return parsons.toString();
    }
}
