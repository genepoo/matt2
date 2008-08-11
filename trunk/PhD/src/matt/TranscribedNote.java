/*
 * TranscribedNote.java
 *
 * Created on 31 January 2007, 15:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author bduggan
 */
public class TranscribedNote {    
    
    private int midiNote;
    private float frequency;    
    private float start;
    private float duration;
    private float unmergedDuration;
    private float unmergedStart;
    private float energy;
    private String name = "NA";
    private int quaverQ;
    private int multiple;
    /** Creates a new instance of TranscribedNote */
    public TranscribedNote(float frequency, float start, float duration) {
        setFrequency(frequency);
        this.start = start;
        this.unmergedStart = start;
        this.duration = duration;
        this.unmergedDuration = duration;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
        
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString()
    {
        return "" + getStart() + "\t" + getDuration() + "\t" + getFrequency() + "\t" + EnergyCalculator.formatEnergy(getEnergy()) + "\t" + getName() + "\t" + getMultiple() + "\t" + getQuaverQ();
    }

    public float getEnergy()
    {
        return energy;
    }

    public void setEnergy(float energy)
    {
        this.energy = energy;
    }

    public int getQuaverQ()
    {
        return quaverQ;
    }

    public void setQuaverQ(int quaverQ)
    {
        this.quaverQ = quaverQ;
    }

    public int getMultiple()
    {
        return multiple;
    }

    public void setMultiple(int multiple)
    {
        this.multiple = multiple;
    }

    public float getUnmergedDuration()
    {
        return unmergedDuration;
    }

    public void setUnmergedDuration(float unmergedDuration)
    {
        this.unmergedDuration = unmergedDuration;
    }

    public float getUnmergedStart()
    {
        return unmergedStart;
    }

    public void setUnmergedStart(float unmergedStart)
    {
        this.unmergedStart = unmergedStart;
    }

    public int getMidiNote() {
        return midiNote;
    }

    public void setMidiNote(int midiNote) {
        this.midiNote = midiNote;
    }
}
