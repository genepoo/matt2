package matt;

import java.io.*;

class WavFilter extends javax.swing.filechooser.FileFilter implements FilenameFilter
{
      public boolean accept(File dir, String name) 
      {
            return (name.contains(".wav") || name.contains(".WAV") || name.contains(".mp3") || name.contains(".MP3"));
      }        

    public boolean accept(File f)
    {
        if (f.isDirectory()) 
        {
            return true;
        }
        return (f.getName().toUpperCase().indexOf(".WAV") > -1);
    }
    
    public String getDescription() 
    {
        return "*.WAV";
    }              
}
