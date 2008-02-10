/*
 * TonePlayer.java
 *
 * Created on 20 February 2007, 16:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import javax.sound.sampled.*;
/**
 *
 * @author bduggan
 */
public class TonePlayer {
    
    private static final float SAMPLE_RATE = 8000.0f;
    private static final float PI = 3.14159f;
    
    /** Creates a new instance of TonePlayer */
    public TonePlayer() {
    }
    
    public static void playTone(float frequency, float duration, float amplitude)
    {
        try		
        {
            byte[] audioData = new byte[(int) (SAMPLE_RATE * duration)];
            float xInc = (2 * PI) / (SAMPLE_RATE / frequency);
            float x = 0;
            for (int i = 0 ; i < audioData.length ; i ++)
            {                
                audioData[i] = (byte) (Math.sin((double) x) * 127.0f * amplitude);
                x += xInc;
            }
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (int) SAMPLE_RATE, 8, 1, 1, SAMPLE_RATE, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
            SourceDataLine line = null;
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();
            line.write(audioData, 0, audioData.length);
            line.drain();
            line.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
}
