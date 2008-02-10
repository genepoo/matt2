/*
 * Stats.java
 *
 * Created on 24 January 2007, 21:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author bduggan
 */
public class Stats {
    private float data[];
    private int start;
    private int end;
    /** Creates a new instance of Stats */
    public Stats(float[] data) 
    {
        setData(data);
    }

    public float[] getData() {
        return data;
    }

    public void setData(float[] data) {
        this.data = data;
        start = 0;
        end = data.length;
    }
    
    float average()
    {
        assert((data != null) && (end != 0));
        float sum = 0;
        
        for (int i = start ; i < end ; i ++)
        {
            sum += data[i];
        }
        return sum / (float) (end - start);
    }
    
    float standardDeviation()
    {
        assert((data != null) && (end != 0));
        
        float average = average();
        float averageDifferenceSquared = 0;
        
        for (int i = start ; i < end ; i ++)
        {
            float difference = data[i] - average;
            averageDifferenceSquared += Math.pow(difference, 2.0f);
        }
        averageDifferenceSquared /= (float) (end - start);
        float stdDev = (float) Math.sqrt(averageDifferenceSquared);
        return stdDev;
        
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
