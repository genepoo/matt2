/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author Bryan Duggan
 */
public class SpectralCentroid {
    private float[] fftMag;
    private float binWidth;
    private float sampleRate;
    private float frameSize;

    /**
     * @return the fftMag
     */
    public float[] getFftMag()
    {
        return fftMag;
    }

    /**
     * @param fftMag the fftMag to set
     */
    public void setFftMag(float[] fftMag)
    {
        this.fftMag = fftMag;
    }

    float getBinWidth()
    {
        binWidth = (float) sampleRate / (float) frameSize;
        return binWidth;
    }

    float calculate()
    {
        float topLine = 0, bottomLine = 0;
        binWidth = (float) sampleRate / (float) frameSize;
        
        for (int i = 0 ; i < fftMag.length ; i ++)
        {
            topLine += (((float) i) * binWidth) * fftMag[i];
            bottomLine += fftMag[i];
        }

        return topLine / bottomLine;
    }

    /**
     * @return the sampleRate
     */
    public float getSampleRate()
    {
        return sampleRate;
    }

    /**
     * @param sampleRate the sampleRate to set
     */
    public void setSampleRate(float sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    /**
     * @return the frameSize
     */
    public float getFrameSize()
    {
        return frameSize;
    }

    /**
     * @param frameSize the frameSize to set
     */
    public void setFrameSize(float frameSize)
    {
        this.frameSize = frameSize;
    }
}
