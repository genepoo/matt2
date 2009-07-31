/*
 * FrequencyDomainCombFilter.java
 *
 * Created on 15 January 2007, 18:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package matt.dsp;

/**
 *
 * @author bduggan
 */
public class FrequencyDomainCombFilter 
{
    private float[] fftMag;
    private float fundamental;
    private int sampleRate;
    private int frameSize;
    private float binSize;
    
    FrequencyDomainCombFilter()
    {
        fftMag = null;
        fundamental = 0;
        sampleRate = 0;
        frameSize = 0;
        binSize = 0;
    }
    
    FrequencyDomainCombFilter(float fundamental, int sampleRate)
    {
        this.fundamental = fundamental;
        this.sampleRate = sampleRate;
    }
    
    private void calculateBinSize()
    {
        binSize = (float) sampleRate / (float) getFrameSize();
    }

    public float[] getFftMag() {
        return fftMag;
    }

    public void setFftMag(float[] fftMag) {
        this.fftMag = fftMag;
    }

    public float getFundamental() {
        return fundamental;
    }

    public void setFundamental(float fundamental) {
        this.fundamental = fundamental;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public float getBinSize() {
        return binSize;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
        calculateBinSize();
    }
    
    private boolean harmonicInRange(float lower, float upper)
    {
        int numHarmonics = 30;
        for (int i = 1 ; i <= numHarmonics ; i ++)
        {
            float harmonic = (float) fundamental * (float) i;
            if ((lower <= harmonic) && (upper >= harmonic))
            {
                return true;
            }
        }
        return false;
    }
    
    float calculateOutputEnergy()
    {
        float output = 0;
        float harmonicfilter = 0;
        int filterSize = fftMag.length / 2;
        for (int filterIndex = 0 ; filterIndex < filterSize ; filterIndex ++)
        {
            float lowerBinFrequency = (filterIndex * binSize) - (binSize / 2);
            float upperBinFrequency = (filterIndex * binSize) + (binSize / 2);
            if (harmonicInRange(lowerBinFrequency, upperBinFrequency))
            {
                harmonicfilter = 1.0f;
            }
            else     
            {
                harmonicfilter = 0.0f;
            }
            output += fftMag[filterIndex] * harmonicfilter;
        }        
        return output;
    }
}
