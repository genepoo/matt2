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
        
        int start, end;
        float quaver = 0.0f;
        boolean finished = false;
        float ocahWindow = MattProperties.getFloat("ocahWindow");
        start = 0; end = 0;
        
        int filtered = 0;
        int inserted = 0;
        int original = transcribedNotes.size();
        int after = 0;
        
        if (MattProperties.getString("searchMethod").equalsIgnoreCase("bryan"))
        {
        
            while (!finished)
            {
                // Calculate the position at the end of the window
                float currentWindow = 0;
                while (currentWindow < ocahWindow)
                {
                    currentWindow += transcribedNotes.get(end).getDuration();
                    end ++;
                    if (end == transcribedNotes.size())
                    {
                        finished = true;
                        break;
                    }
                    // Do we need to merge the last frame?
                    TranscribedNote lastNote = transcribedNotes.get(transcribedNotes.size() - 1);
                    if (((lastNote.getStart() + lastNote.getDuration()) - transcribedNotes.get(end).getStart()) <= ocahWindow)
                    {
                        end = transcribedNotes.size();
                        finished = true;
                        break;
                    }
                }
                Logger.log("Looking at window " + start + " to " + end);
                float[] histData = new float[end - start];
                FuzzyHistogram fuzzyHistogram = new FuzzyHistogram();                
                for (int i = start ; i < end ; i ++)
                {
                    TranscribedNote note = (TranscribedNote) transcribedNotes.elementAt(i);
                    histData[i - start] = note.getDuration();
                }
                // The average note duration, probably the length of a quaver
                quaver = fuzzyHistogram.calculatePeek(histData, 0.3f);

                Logger.log("Quaver length: " + quaver);
                for (int i = start ; i < end; i ++)
                {
                    TranscribedNote note = transcribedNotes.get(i);
                    note.setMultiple(calculateNearestMultiple(note.getDuration(), quaver));
                    Logger.log(i + "\t" + transcribedNotes.get(i));
                }
                // If the Note duration is short, 
                // its probaby an ornamentation
                if (MattProperties.getBoolean("ornamentationCompensation"))
                {
                    for (int i = start + 1 ; i < end ; i ++)
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
                            filtered ++;
                            i --;            
                            end --;
                        }
                        else
                        {
                            current.setMultiple(multiple);
                        }
                    }
                    // Now check the very first note
                    if (calculateNearestMultiple(transcribedNotes.elementAt(start).getDuration(), quaver) == 0)
                    {
                        transcribedNotes.elementAt(start + 1).setDuration(transcribedNotes.elementAt(start + 1).getDuration() + transcribedNotes.elementAt(start).getDuration());
                        transcribedNotes.remove(start);
                        end --;
                        filtered ++;
                    } 
                }

                // Now go back and split long notes and redo the FFT
                if (MattProperties.getBoolean("missedOnsetCompensation"))
                {
                    // Recalculate the quaver length
                    histData = new float[end - start];
                    fuzzyHistogram = new FuzzyHistogram();                
                    for (int i = start ; i < end ; i ++)
                    {
                        TranscribedNote note = (TranscribedNote) transcribedNotes.elementAt(i);
                        histData[i - start] = note.getDuration();
                    }
                    // The average note duration, probably the length of a quaver
                    quaver = fuzzyHistogram.calculatePeek(histData, 0.3f);
                    for (int i = start ; i < end ;)
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
                                newNote.setSpelling("NEW");
                                newNote.setMultiple(1);
                                transcribedNotes.add(i + m, newNote);
                                inserted ++;
                                end ++;
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
                start = end;
            }
            Logger.log("After Missed Onset Compensation");        
        }
        else
        {
            Logger.log("Skipping onset post processing");        
        }
        
        
        for (int i = 0 ; i < transcribedNotes.size(); i ++)
        {
            Logger.log(i + "\t" + transcribedNotes.get(i));
        }        
        TranscribedNote[] notes = new TranscribedNote[transcribedNotes.size()];
        transcribedNotes.copyInto(notes);      
        after = transcribedNotes.size();
        TFLog.TFLog("filtered.txt", "" + original + "\t" + filtered + "\t" + inserted + "\t" + after);
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
