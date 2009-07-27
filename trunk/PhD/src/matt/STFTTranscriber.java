/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.util.Vector;

/**
 *
 * @author Bryan Duggan
 */
public class STFTTranscriber extends Transcriber{

    public STFTTranscriber()
    {
        super();
    }

    public void transcribe()
    {
        String spellings = "";
        String unfilteredSpellings = "";
        Vector<TranscribedNote> notes = new Vector();
        ABCTranscriber abcTranscriber =new ABCTranscriber(this);
        abcTranscriber.makeScale("Major");

        WindowFunction windowFunction = new WindowFunction();
        windowFunction.setWindowType(WindowFunction.HANNING);
        float[] win;
        win = windowFunction.generate(frameSize);
        String lastSpelling = null;
        EnergyCalculator ec  = new EnergyCalculator();
        ec.setSignal(signal);

        float signalEnergy = ec.calculateAverageEnergy();
        float noteEnergy = 0;
        float frequency = 0, lastFrequency = 0;
        SpectralCentroid sc = new SpectralCentroid();
        sc.setSampleRate(sampleRate);
        sc.setFrameSize(frameSize);
        float fftFrame[] = new float[frameSize];
        Logger.log("Spectral range:" + (sc.getBinWidth()) * frameSize);
        for (int i = 0 ; i < (signal.length - frameSize) ; i += hopSize)
        {
            String spelling;

            for (int j = 0 ; j < frameSize ; j ++)
            {
                if ((i + j) > signal.length)
                {
                    Logger.log("Got here!");
                }
               fftFrame[j] = (signal[i + j] / 0x8000) * win[j];
            }

            FastFourierTransform fft = new FastFourierTransform();

            float[] fftOut = fft.fftMag(fftFrame, 0, frameSize);
            sc.setFftMag(fftOut);
            Logger.log("Centroid: " + i + "\t" + sc.calculate());
            PitchDetector pitchDetector = new PitchDetector();
            float mFrequency = pitchDetector.mikelsFrequency(fftOut, sampleRate, frameSize);
            frequency = mFrequency;
            spelling = MattABCTools.stripAll(abcTranscriber.spell(frequency));
            unfilteredSpellings += spelling;
            if (lastSpelling == null)
            {
                lastSpelling = spelling;
                lastFrequency = frequency;
            }
            else if (  !(spelling.equals(lastSpelling)))
            {
                TranscribedNote note = new TranscribedNote();
                note.setSpelling(lastSpelling);
                note.setEnergy(noteEnergy);
                note.setFrequency(lastFrequency);
                if (notes.size() == 0)
                {
                    note.setStart(0);
                    note.setDuration(sampleToSeconds(i));
                }
                else
                {
                    TranscribedNote lastNote = notes.get(notes.size() -1);
                    note.setStart(lastNote.getStart() + lastNote.getDuration());
                    note.setDuration(sampleToSeconds(i) - note.getStart());
                }
                lastSpelling = spelling;
                lastFrequency = frequency;
                noteEnergy = 0;
                notes.add(note);
            }
        }
        // Add the last note
        if (lastSpelling != null)
        {
            TranscribedNote note = new TranscribedNote();
            note.setSpelling(lastSpelling);
            note.setFrequency(lastFrequency);
            note.setEnergy(noteEnergy);
            if (notes.size() == 0)
            {
                note.setStart(0);
                note.setDuration(sampleToSeconds(signal.length));
            }
            else
            {
                TranscribedNote lastNote = notes.get(notes.size() -1);
                note.setStart(lastNote.getStart() + lastNote.getDuration());
                note.setDuration(sampleToSeconds(signal.length) - note.getStart());
            }
            notes.add(note);
        }

        OnsetPostProcessor opp = new OnsetPostProcessor(notes, sampleRate, signal);
        transcribedNotes = opp.postProcess();
        /*
         transcribedNotes = new TranscribedNote[notes.size()];
        for (int i = 0 ; i < notes.size() ; i ++)
        {
            spellings += notes.get(i).getSpelling();
            transcribedNotes[i] =notes.get(i);
        }
       */

        abcTranscriber.setTranscribedNotes(transcribedNotes);
        abcTranscriber.printScale();
        String notesString = null;
        notesString  = abcTranscriber.convertToABC();
        printNotes();
        Logger.log(unfilteredSpellings);
        gui.getTxtABC().setText(notesString);
    }

}
