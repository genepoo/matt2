/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import matt.dsp.TimeDomainCombFilter;

/**
 *
 * @author Bryan Duggan
 */
public class ODFCalculator extends Thread {
    int numFilters = 12;
    private float[] frame;
    private float[] signal;
    TimeDomainCombFilter[] tdFilters = new TimeDomainCombFilter[numFilters];
    private float oldPowers[] = new float[numFilters];
    private float powers[] = new float[numFilters];
    private int odfIndex = 0;
    private float[] odf;
    private int sampleRate;
    private int hopIndex; 
    
    public void run()
    {
        int currentSample = 0;
       
        int frameSize = Integer.parseInt("" + MattProperties.getString("frameSize"));
        
        int hopSize = (int) ((float) frameSize * 0.25f);    
        for (int frameIndex = 0 ; frameIndex < frameSize; frameIndex ++)
        {
            currentSample = (hopIndex * hopSize) + frameIndex;
            frame[frameIndex] = signal[currentSample];         
        }                 

        // Calculate the energy in each frequency range

        // Calculate the harmonicity value
        // Calculate the ODF
        for (int i = 0 ; i < tdFilters.length; i ++)
        {
            tdFilters[i].setFrame(getFrame());
            powers[i] = tdFilters[i].calculateHarmonicity();                     
            if (hopIndex > 0)
            {
                odf[odfIndex] += Math.pow(powers[i] - oldPowers[i], 2);                        
            }
            oldPowers[i] = powers[i];
        }       
        if (hopIndex > 0)
        {
            odfIndex ++;
        }
    }

    public float[] getFrame()
    {
        return frame;
    }

    public void setFrame(float[] frame)
    {
        this.frame = frame;
    }

    public float[] getSignal()
    {
        return signal;
    }

    public void setSignal(float[] signal)
    {
        this.signal = signal;
    }

    public TimeDomainCombFilter[] getTdFilters()
    {
        return tdFilters;
    }

    public void setTdFilters(TimeDomainCombFilter[] tdFilters)
    {
        this.tdFilters = tdFilters;
    }

    public int getOdfIndex()
    {
        return odfIndex;
    }

    public void setOdfIndex(int odfIndex)
    {
        this.odfIndex = odfIndex;
    }

    public float[] getOdf()
    {
        return odf;
    }

    public void setOdf(float[] odf)
    {
        this.odf = odf;
    }   

    public int getSampleRate()
    {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    public int getHopIndex()
    {
        return hopIndex;
    }

    public void setHopIndex(int hopIndex)
    {
        this.hopIndex = hopIndex;
    }
}
