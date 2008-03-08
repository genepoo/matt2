/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matt;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 *
 * @author Bryan Duggan
 */
public class AudioCapture implements Runnable
{
    TargetDataLine line;
    Thread thread;
    double duration;
    AudioFormat format;
    private String fileName;
    private AudioInputStream audioInputStream;
    SimpleDateFormat dateFormat = new SimpleDateFormat();
   
    public AudioCapture()
    {
        format = new AudioFormat(44100, 16, 1, true, false);
        dateFormat.applyPattern("dd-MM-yyyy HHmmss");                    

    }

    public void start()
    {

        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop()
    {
        thread = null;
    }

    public void run()
    {

        duration = 0;

        // define the required attributes for our line, 
        // and make sure a compatible line is supported.

        DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                format);

        if (!AudioSystem.isLineSupported(info))
        {
            Logger.log("Not supported");
            return;
        }

        // get and open the target data line for capture.
        try
        {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        }
        catch (LineUnavailableException ex)
        {
            Logger.log("Unable to open the line: " + ex);
            return;
        }
        catch (SecurityException ex)
        {
            Logger.log(ex.toString());
            return;
        }
        catch (Exception ex)
        {
            Logger.log(ex.toString());
            return;
        }

        // play back the captured audio data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;

        line.start();

        while (thread != null)
        {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1)
            {
                break;
            }
            out.write(data, 0, numBytesRead);
        }

        // we reached the end of the stream.  stop and close the line.
        line.stop();
        line.close();
        line = null;

        // stop and close the output stream
        try
        {
            out.flush();
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        byte audioBytes[] = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

        long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;

        try
        {
            audioInputStream.reset();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
        saveToFile();
    }

    public void saveToFile()
    {
        // reset to the beginnning of the captured data
        
        Date now = new Date();
        long start = System.currentTimeMillis();
        now.setTime(start);
        
        fileName = MattProperties.instance().getProperty("tunometerPath") + System.getProperty("file.separator") + "query "  + dateFormat.format(now) + ".wav";
   
        File file = new File(fileName);
        try
        {
            if (AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file) == -1)
            {
                throw new IOException("Problems writing to file");
            }
        }
        catch (Exception ex)
        {
            Logger.log(ex.toString());
        }
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
