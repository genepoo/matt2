/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.*;

import javax.sound.midi.*;
import java.util.*;

/**
 *
 * @author Bryan
 */
public class MIDITools {
    private static MIDITools _instance;
    private  boolean finished;
    
    private Sequencer sequencer;
    public static MIDITools instance()
    {
        if (_instance == null)
        {
            _instance = new MIDITools();
            _instance.setFinished(true);
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
        fw.write("Q:1/4 = 200\n");
        //fw.write("%%%%MIDI program 24\n");
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
    
    public int[] toMIDISequence(TranscribedNote[] notes)
    {
        Vector<Integer> v = new Vector<Integer>();
        
        for (int i = 0 ; i < notes.length ; i ++)
        {
            v.add(notes[i].getMidiNote());
        }
        int[] ret = new int[notes.length];
        for (int i = 0 ; i < notes.length ; i ++)
        {
           ret[i] = v.get(i) .intValue();
        }
        return ret;
    }
    
    public int[] toMIDISequence(String file) throws InvalidMidiDataException, IOException 
    {
        Sequence sequence = MidiSystem.getSequence(new File(file));
        Track[] tracks = sequence.getTracks();
        ArrayList<Integer> midiSequence = new ArrayList();
        for(int i = 0 ; i < tracks[0].size(); i ++)
        {
            MidiMessage mm = tracks[0].get(i).getMessage();
            int len = mm.getLength();
            int status = mm.getStatus();
            if (status == ShortMessage.NOTE_ON)
            {                
                byte[] b = mm.getMessage();
                int currentNote = b[1];
                midiSequence.add(new Integer(currentNote));
            }
            
        }        
        int[] ret = new int[midiSequence.size()];
        for (int i = 0 ; i < midiSequence.size() ; i ++)
        {
            ret[i] = midiSequence.get(i).intValue();

        }
        return ret;
    }
    
    public String arrayToString(int[] midiSequence)
    {
        StringBuffer ret = new StringBuffer();
        for (int i = 0 ; i < midiSequence.length ; i ++)
        {
            ret.append("" + midiSequence[i]);
            if (i < midiSequence.length - 1)
            {
                ret.append(",");
            }                
        }
        return ret.toString();
    }
    
    public String toParsons(int[] midiSequence) throws InvalidMidiDataException, IOException 
    {
        int previousNote = -1;
        StringBuffer parsons  = new StringBuffer();
        for(int i = 0 ; i < midiSequence.length; i ++)
        {

            int currentNote = midiSequence[i];
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
        return parsons.toString();
    }
    
    public void playMidiFile(String file) 
    {
        File midiFile = new File(file);
        if(!midiFile.exists() || midiFile.isDirectory() || !midiFile.canRead()) {
            Logger.log("Could not play midi file: " + file);
            return;
        }
        // Play once
        try {
            
            sequencer = MidiSystem.getSequencer();
            sequencer.setSequence(MidiSystem.getSequence(midiFile));
            sequencer.open();
            sequencer.start();
            finished = false;
            new Thread()
            {
                public void run()
                {
                    while(! isFinished()) 
                    {
                        if(sequencer.isRunning()) 
                        {
                            try 
                            {
                                Thread.sleep(1000); // Check every second
                            } 
                            catch(InterruptedException ignore) 
                            {
                                break;
                            }
                        } 
                        else 
                        {
                            break;
                        }
                    }            // Close the MidiDevice & free resources
                    sequencer.stop();
                    sequencer.close();
                    finished = true;
                }
            }.start();
        } 
        catch(MidiUnavailableException mue) 
        {
            System.out.println("Midi device unavailable!");
        } 
        catch(InvalidMidiDataException imde) 
        {
            System.out.println("Invalid Midi data!");
        } 
        catch(IOException ioe) 
        {
            System.out.println("I/O Error!");
        } 
    }

    public boolean isFinished()
    {
        return finished;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }
}
