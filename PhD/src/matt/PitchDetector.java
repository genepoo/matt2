/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.util.*;
/**
 *
 * @author Bryan Duggan
 */
public class PitchDetector 
{
    int maxFrameIndex(float[] frame) 
    {
        float maximumFrameValue = frame[0];
        int maxIndex  = 0;
        for (int i = 1; i < frame.length; i++) 
        {
            if (frame[i] > maximumFrameValue) 
            {
                maximumFrameValue = frame[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    //-----------calculate the maximum frequency in a frameed data-------------	
    float maxFreq(int maxIndex, int sampleRate, int frameSize)
    {
        float binWidth = (float) sampleRate / (float) frameSize;
        float maximumFrequency = (float) maxIndex * binWidth;
        return maximumFrequency;
    }
    
    float maxPeek(float[] fftMag, int sampleRate, int frameSize)
    {
        return maxFreq(maxFrameIndex(fftMag), sampleRate, frameSize);
    }
    
    float maxFilter(float[] fftMag, int sampleRate, int frameSize, float startAt)
    {
        float binWidth = (float) sampleRate / (float) frameSize;
        
        float[] filterPower = new float[24];
        
        float ratio = 1.05946309436f;
        float fundamental = startAt;
        
        float[] filter = new float[fftMag.length / 2];
        for (int i = 0 ; i < 24 ; i ++)
        {
            for (int filterIndex = 0 ; filterIndex < filter.length ; filterIndex ++)
            {
                if (((filterIndex * binWidth) % fundamental ) == 0)
                {
                    filter[filterIndex] = 1;
                }
            }            
        }        
        return 0;
    }
    
    public float maxHistogram(float[] fftMag, int sampleRate, int frameSize)
    {
        Vector peeks = new Vector();
        
        float binWidth = (float) sampleRate / (float) frameSize;
        // int maxPossible = (int) (1000.0f / binWidth);
        
        int pitchPeek = Integer.parseInt("" + MattProperties.instance().get("pitchPeek"));
        
        peeks = PeakCalculator.calculatePeaks(fftMag, pitchPeek, fftMag.length / 2, 0.5f);
        
        float[] intervals = new float[peeks.size() -1];
        for (int i = 1 ; i < peeks.size() ; i ++)
        {            
            intervals[i - 1] = ((Integer)peeks.elementAt(i)).intValue() - ((Integer)peeks.elementAt(i - 1)).intValue() * binWidth;
        }
        SimpleHistogram histogram = new SimpleHistogram();
        histogram.setData(intervals);
        return histogram.calculatePeek();
    }
    
    public float maxBryanFrequency(float[] fftMag, int sampleRate, int frameSize)
    {
        PeakCalculator pk = new PeakCalculator();        
        int pitchPeek = Integer.parseInt("" + MattProperties.instance().get("pitchPeek"));
        Vector peeks = PeakCalculator.calculatePeaks(fftMag, pitchPeek , fftMag.length, 0.5f);
                
        float binWidth = (float) sampleRate / (float) frameSize;
        
        float averageDerivitive = 0;
        float prev = 0;
        int numPeeksToCheck = (peeks.size() <= 2) ? peeks.size() : 2;
        for (int i = 0 ; i < numPeeksToCheck; i ++)
        {
            int inx = ((Integer) peeks.elementAt(i)).intValue();
            float f = ((float) inx) * binWidth;            
            if (i > 0)
            {
                float der = f - prev;
                averageDerivitive += der;
            }
            prev = f;
            Logger.log(f);
        }
        if (peeks.size() > 1)
        {
            averageDerivitive = averageDerivitive / (numPeeksToCheck -1);
            return averageDerivitive;
        }
        else
        {
            if (peeks.size() == 0)
            {
                return maxPeek(fftMag, sampleRate, frameSize);
            }
            int inx = ((Integer) peeks.elementAt(0)).intValue();
            float f = ((float) inx) * binWidth;   
            return f;
        }
    }
    
    public float cepstrumFrequency(float[] fftMag, int sampleRate, int frameSize)
    {
        float frequency = 0;
        FastFourierTransform fft = new FastFourierTransform();
        
        float[] logMag = new float[fftMag.length];
        for (int i = 0 ; i < fftMag.length; i ++)
        {
            logMag[i] = (float) Math.log((double) fftMag[i]);
        }
        
        Graph logGraph = new Graph();
        logGraph.setBounds(0, 0, 1000, 1000);
        logGraph.getDefaultSeries().setScale(false);
        logGraph.getDefaultSeries().setData(logMag);                
        MattGuiNB.instance().addFFTGraph(logGraph, "LOG GRAPH");

        
        
        float[] cepstrum = fft.fftLogMag(fftMag);
        Graph fftGraph = new Graph();
        fftGraph.setBounds(0, 0, 1000, 1000);
        fftGraph.getDefaultSeries().setScale(false);
        fftGraph.getDefaultSeries().setData(cepstrum);                
        MattGuiNB.instance().addFFTGraph(fftGraph, "CEPSTRUM");
        int max = maxFrameIndex(cepstrum);
        
        return frequency;
        
    }
    
    public float mikelsFrequency(float[] fftMag, int sampleRate, int frameSize)
    {
        float frequency = 0;
        
        int pitchPeek = Integer.parseInt("" + MattProperties.instance().get("pitchPeek"));
        Vector<Integer> peeks = PeakCalculator.calculatePeaks2(fftMag, pitchPeek , fftMag.length, 0);
        // Sort them in order of ascending energy
        java.util.Collections.sort(peeks, new EnergyComparitor(fftMag));
        
        final int numCandidates = 5;
        final int numHarmonics = 10;
        float maxEnergy = 0;
        float maxCandidate = 0;
        
        /*
         System.out.println("TOP " + numCandidates + " HARMONICS");
        for (int i = 0 ; i < numCandidates ; i ++)
        {
            int candidate = peeks.elementAt(i).intValue();
            System.out.println(candidate + "\t" + fftMag[candidate]);
        }
         */
        
        for (int i=0 ; i < numCandidates ; i ++)
        {
            int candidate = peeks.elementAt(i).intValue();
            float energy = 0;
            for (int j = 0 ; j < numHarmonics ; j ++)
            {
                int harmonic = candidate + (j * candidate);
                if (harmonic < fftMag.length)
                {
                    energy += fftMag[harmonic];
                }    
            }
            if (energy > maxEnergy)
            {
                maxEnergy = energy;
                maxCandidate = candidate;
                System.out.println("c: " + candidate + " m: " + maxCandidate);
            }
        }
        float binWidth = (float) sampleRate / (float) frameSize;

        frequency = maxCandidate * binWidth;   
      
        return frequency;
    }
    
       class EnergyComparitor implements Comparator
        {
            public EnergyComparitor(float[] fftMag)
            {
                this.fftMag = fftMag;
                        
            }
            public int compare(Object o1, Object o2)
            {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                if (fftMag[i1.intValue()] == fftMag[i2.intValue()])
                {
                    return 0;
                }
                if (fftMag[i1.intValue()] < fftMag[i2.intValue()])
                {
                    return 1;
                }
                return -1;
                        
            }
            public float[] fftMag;
        }
     
    
}
