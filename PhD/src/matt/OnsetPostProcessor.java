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
public class OnsetPostProcessor {
    private Vector<TranscribedNote> transcribedNotes;    
    private int sampleRate;
    private float[] signal;
    
    public OnsetPostProcessor(Vector<TranscribedNote> transcribedNotes, int sampleRate, float[] signal)
    {
        this.transcribedNotes = transcribedNotes;
        this.sampleRate = sampleRate;
        this.signal = signal;
    }

    public Vector<TranscribedNote> getTranscribedNotes()
    {
        return transcribedNotes;
    }

    public void setTranscribedNotes(Vector<TranscribedNote> transcribedNotes)
    {
        this.transcribedNotes = transcribedNotes;
    }
    
    /**
     * Remove false positives
     * @return
     */
    TranscribedNote[] postProcess()
    {
        float quaver = 0.0f;
        float[] histData = new float[transcribedNotes.size()];
        FuzzyHistogram fuzzyHistogram = new FuzzyHistogram();                
        for (int i = 0 ; i < transcribedNotes.size() ; i ++)
        {
            TranscribedNote note = (TranscribedNote) transcribedNotes.elementAt(i);
            histData[i] = note.getDuration();
        }
        // The average note duration, probably the length of a quaver
        quaver = fuzzyHistogram.calculatePeek(histData, 0.3f);
        
        Logger.log("Quaver length: " + quaver);
        for (int i = 0 ; i < transcribedNotes.size(); i ++)
        {
            TranscribedNote note = transcribedNotes.get(i);
            note.setMultiple(calculateNearestMultiple(note.getDuration(), quaver));
            Logger.log(i + "\t" + transcribedNotes.get(i));
        }
        // If the Note duration is short, 
        // its probaby a false positive
        if (MattProperties.getBoolean("ornamentationCompensation"))
        {
            for (int i = 1 ; i < transcribedNotes.size() ; i ++)
            {
                TranscribedNote previous = transcribedNotes.elementAt(i - 1);
                TranscribedNote current = transcribedNotes.elementAt(i);            
                int multiple = calculateNearestMultiple(current.getDuration(), quaver);
                if (multiple == 0)
                {
                    // Merge it with the following note                
                    if (i + 1 < transcribedNotes.size())
                    {                    
                        TranscribedNote next = transcribedNotes.elementAt(i + 1);
                        // Merge the duration, but ignore the start
                        next.setStart(current.getStart());
                        next.setUnmergedStart(next.getStart());
                        next.setUnmergedDuration(next.getDuration());
                        next.setDuration(next.getDuration() + current.getDuration());
                        Logger.log("Merging note: " + i + " " + current + " with next: " + (i + 1) + " " + next);
                    }

                    transcribedNotes.remove(i);
                    i --;                
                }
                else
                {
                    current.setMultiple(multiple);
                }
            }
            // Now check the very first note
            if (calculateNearestMultiple(transcribedNotes.elementAt(0).getDuration(), quaver) == 0)
            {
                transcribedNotes.elementAt(1).setDuration(transcribedNotes.elementAt(1).getDuration() + transcribedNotes.elementAt(0).getDuration());
                transcribedNotes.remove(0);
            } 
        }
        
        // Now go back and split long notes and redo the FFT
        if (MattProperties.getBoolean("missedOnsetCompensation"))
        {
            // Recalculate the quaver length
            histData = new float[transcribedNotes.size()];
            fuzzyHistogram = new FuzzyHistogram();                
            for (int i = 0 ; i < transcribedNotes.size() ; i ++)
            {
                TranscribedNote note = (TranscribedNote) transcribedNotes.elementAt(i);
                histData[i] = note.getDuration();
            }
            // The average note duration, probably the length of a quaver
            quaver = fuzzyHistogram.calculatePeek(histData, 0.3f);
            for (int i = 0 ; i < transcribedNotes.size() ;)
            {
                TranscribedNote current = transcribedNotes.elementAt(i);
                int multiple = calculateNearestMultiple(current.getDuration(), quaver);
                current.setMultiple(multiple);
                if (multiple > 1)
                {
                    // Split the note
                    float newDuration = current.getDuration() / multiple;
                    current.setMultiple(1);
                    current.setDuration(newDuration);
                    for (int m = 1 ; m < multiple; m ++)
                    {
                        TranscribedNote newNote = new TranscribedNote(0, current.getStart() + (m * newDuration), newDuration);
                        newNote.setName("NEW");
                        newNote.setMultiple(1);
                        transcribedNotes.add(i + m, newNote);
                    }                    
                    // Now recalculate the energy and FFT of the notes
                    for (int m = 0 ; m < multiple; m ++)
                    {
                        Logger.log("Recalculating energy and FFT for note: " + (m + i));
                        TranscribedNote note = transcribedNotes.get(m + i);
                        
                        int signalStart =  (int) (note.getUnmergedStart() * (float)sampleRate);
                        int signalEnd = (int) ((note.getUnmergedStart() + note.getUnmergedDuration()) * (float)sampleRate);
                        int signalLength = signalEnd - signalStart;
                        int smallestPowerOf2 = FastFourierTransform.smallestPowerOf2(signalLength);
                        
                        FastFourierTransform fft = new FastFourierTransform();
                        EnergyCalculator ec = new EnergyCalculator();
                        ec.setSignal(signal);                        
                        ec.setStart(signalStart);
                        ec.setEnd(signalEnd);
                        float energy = ec.calculateAverageEnergy();
                        
                        int fftFrameSize = (int) Math.pow(2, smallestPowerOf2);
                        Logger.log("Performing FFT on frame size " + fftFrameSize);
                        float fftFrame[] = new float[fftFrameSize];
                        WindowFunction windowFunction = new WindowFunction();
                        windowFunction.setWindowType(WindowFunction.HANNING);
                        float[] win;
                        win = windowFunction.generate(fftFrameSize);
                        for (int j = 0 ; j < fftFrameSize; j ++)
                        {
                            fftFrame[j] = (signal[signalStart + j] / 0x8000) * win[j];
                        }
                        float[] fftOut = fft.fftMag(fftFrame, 0, fftFrameSize);            
                        float frequency;
                        PitchDetector pitchDetector = new PitchDetector();
                        frequency = pitchDetector.maxPeek(fftOut, sampleRate, fftFrameSize); 
                        Logger.log("Frequency by highest value: " + frequency);
                        frequency = pitchDetector.maxBryanFrequency(fftOut, sampleRate, fftFrameSize);
                        Logger.log("Frequency by Bryan's algorithm value: " + frequency);

                        note.setFrequency(frequency);
                        note.setEnergy(energy);
                    }
                }
                if (multiple == 0)
                {
                    i ++;
                }
                else
                {
                    i += multiple;    
                }
            }
        }

        Logger.log("After Missed Onset Compensation");        
        for (int i = 0 ; i < transcribedNotes.size(); i ++)
        {
            Logger.log(i + "\t" + transcribedNotes.get(i));
        }        
        TranscribedNote[] notes = new TranscribedNote[transcribedNotes.size()];
        transcribedNotes.copyInto(notes);        
        return notes;
    }
    
    public static int calculateNearestMultiple(float duration, float standard)
    {
        return Math.round(duration / standard);
    }

    public int getSampleRate()
    {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate)
    {
        this.sampleRate = sampleRate;
    }
}
