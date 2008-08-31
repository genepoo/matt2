/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.*;
import java.sql.*;
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.sound.sampled.*;
/**
 *
 * @author Bryan
 */
public class TuneLister {
    
    static File folder;
    
    public static void main(String[] args)
    {
        try
        {
            JFileChooser fc = new JFileChooser();

            WavFilter filter = new WavFilter();
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.setSelectedFile(new File("" + MattProperties.instance().get("BatchPath")));
            int returnVal = fc.showOpenDialog(MattGuiNB.instance());
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                folder = fc.getSelectedFile();
                File[] files;
                if (folder.isDirectory())
                {
                    files = folder.listFiles(filter);
                    for (int i = 0 ; i < files.length ; i ++)
                    {
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(files[i]);            
                        AudioFormat	format = audioInputStream.getFormat();
                        String name = files[i].getName();
                        StringTokenizer st = new StringTokenizer(name, "-");
                        System.out.print(st.nextToken().trim() + "\t");
                        System.out.print(st.nextToken().trim() + "\t");
                        System.out.print(st.nextToken().trim() + "\t");

                        if (st.hasMoreTokens())
                        {
                            String key = st.nextToken();
                            key = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                            System.out.print(key);                    
                        }
                        else
                        {
                            System.out.print("D");                    
                        }
                        System.out.print("\t");
                        System.out.println((float) audioInputStream.available() / (format.getSampleRate() * 2));
                        audioInputStream.close();

                    }
                    
                }
            }        
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }
}


