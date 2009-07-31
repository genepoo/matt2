/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import matt.dsp.FastFourierTransform;
import matt.dsp.SpectralCentroid;
import java.util.Vector;

/**
 *
 * @author Bryan Duggan
 */
public class STFTTranscriber extends ODCFTranscriber{

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
        float stdDevs[] = new float[signal.length / hopSize];
        int iStdDev = 0;
        gui.getProgressBar().setMaximum(signal.length);
        gui.getProgressBar().setValue(0);
        for (int i = 0 ; i < (signal.length - frameSize) ; i += hopSize)
        {
            gui.getProgressBar().setValue(i);
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
            // Logger.log("Centroid: " + i + "\t" + sc.calculate());
            float stdDev = SD.sdFast(fftOut);
            stdDevs[iStdDev ++] = stdDev;
            if (Boolean.parseBoolean("" + MattProperties.getString("drawFFTGraphs")) == true)
            {
                Graph fftGraph = new Graph();

                fftGraph.setBounds(0, 0, 1000, 1000);
                fftGraph.getDefaultSeries().setScale(false);
                fftGraph.getDefaultSeries().setData(fftOut);
                MattGuiNB.instance().addFFTGraph(fftGraph, "" + i);
            }
            ec.setStart(i);
            ec.setEnd(i + frameSize);
            noteEnergy = ec.calculateAverageEnergy();
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
                Logger.log("Found note: " + lastSpelling + ", energy: " + noteEnergy + ", frequency: " + lastFrequency);
                if (notes.size() == 0)
                {
                    note.setStart(0);
                    note.setUnmergedStart(0);
                    note.setDuration(sampleToSeconds(i));
                    note.setUnmergedDuration(sampleToSeconds(i));
                }
                else
                {
                    TranscribedNote lastNote = notes.get(notes.size() -1);
                    float start, duration;
                    start = lastNote.getStart() + lastNote.getDuration();
                    note.setStart(start);
                    note.setUnmergedStart(start);
                    duration = sampleToSeconds(i) - note.getStart();
                    note.setDuration(duration);
                    note.setUnmergedDuration(duration);
                }
                lastSpelling = spelling;
                lastFrequency = frequency;
                noteEnergy = 0;
                notes.add(note);
            }
        }

        /*
         Graph stdGraph = new Graph();

        stdGraph.setBounds(0, 0, 1000, 1000);
        stdGraph.getDefaultSeries().setScale(false);
        stdGraph.getDefaultSeries().setData(stdDevs);
        MattGuiNB.instance().addFFTGraph(stdGraph, "STD DEVS");
         */
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
                note.setUnmergedStart(0);

                note.setDuration(sampleToSeconds(signal.length));
                note.setUnmergedDuration(sampleToSeconds(signal.length));
            }
            else
            {
                TranscribedNote lastNote = notes.get(notes.size() -1);
                float start, duration;
                start = lastNote.getStart() + lastNote.getDuration();
                note.setStart(start);
                note.setUnmergedStart(start);

                duration = sampleToSeconds(signal.length) - note.getStart();
                note.setDuration(duration);
                note.setUnmergedDuration(duration);
            }
            notes.add(note);
        }

        OrnamentationFilter opp = new OrnamentationFilter(notes, sampleRate, signal);
        transcribedNotes = opp.filter();
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
        gui.getProgressBar().setValue(signal.length);
        gui.getTxtABC().setText(notesString);
        Logger.log("Done");
    }

}
