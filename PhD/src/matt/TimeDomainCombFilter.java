/*
 * TimeDomainCombFilter.java
 *
 * Created on 15 January 2007, 18:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author bduggan
 */
public class TimeDomainCombFilter {
    
    private float[] frame;
    private int frequency;
    private int sampleRate;
    private int delay;
    
    /** Creates a new instance of CombFilter */
    public TimeDomainCombFilter() {
    }

    public float[] getFrame() {
        return frame;
    }

    public void setFrame(float[] frame) {
        this.frame = frame;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {        
        this.frequency = frequency;
        delay = (int) ((1.0f / (float) frequency) * (float) sampleRate);  
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getDelay() {
        return delay;
    }
    
    public float calculateOutputPower()
    {
        float power = 0;
        for (int i = 0 ; i < frame.length + delay ; i ++)
        {
            // Add 0's at the start
            if (i < delay)
            {
                power += Math.pow(frame[i], 2);
            }
            // Add 0's at the end
            else if (i >= frame.length)
            {
                power += Math.pow(frame[i - delay], 2);
            }
            else {
                power += Math.pow(frame[i] + frame[i - delay], 2);
            }
        }
        return power;
    }
    
    private float calculateInputPower()
    {
        float power = 0;
        for (int i = 0 ; i < frame.length ; i ++)
        {
            power += Math.pow(frame[i], 2);
        }
        return power;
    }
    
    public float calculateHarmonicity()
    {
        float inputPower, outputPower, power;
        
        inputPower = calculateInputPower();
        outputPower = calculateOutputPower();

        power = (float) outputPower  / (4.0f * inputPower);
        
        return power;
    }
}
