/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.text.DecimalFormat;

/**
 *
 * @author Bryan Duggan
 */
public class EnergyCalculator 
{
    private float signal[];
    private int start;
    private int end;
    static DecimalFormat formatter = new DecimalFormat("#,##0.0#");
    
    public static final float SILENCE_THRESHOLD = 600000000.0f;
    
    public EnergyCalculator()
    {
    }
    
    public EnergyCalculator(float signal[], int start, int end)
    {
        this.signal = signal;
        this.start = start;
        this.end = end;
    }
    
    public float calculateEnergy()
    {
        float energy = 0;
        for (int i = start  ; i <= end ; i ++)
        {
            if (i == signal.length)
            {
                break;
            }
            energy += Math.abs(signal[i]);
        }
        return energy;
    }
    
    public float calculateAverageEnergy()
    {
        float energy = calculateEnergy();
        float size = (end - start) - 1;
        energy = energy / size;
        return energy;
    }
    
    public float calculateMaxEnergy()
    {
        float energy = 0;
        for (int i = start  ; i <= end ; i ++)
        {
            float current = Math.abs(signal[i]);
            if (current > energy)
            {
                energy = current;
            }
        }
        return energy;
    }
    
    public static String formatEnergy(float energy)
    {
        return formatter.format(new Float(energy));
    }

    public int getStart()
    {
        return start;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public int getEnd()
    {
        return end;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }

    public float[] getSignal()
    {
        return signal;
    }

    public void setSignal(float[] signal)
    {
        this.signal = signal;
        start = 0;
        end = signal.length - 1;
    }
}
