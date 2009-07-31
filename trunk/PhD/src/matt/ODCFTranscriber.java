/*
 * Transcriber.java
 *
 * Created on 08 January 2007, 17:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import matt.dsp.FrequencyDomainCombFilter;
import matt.dsp.TimeDomainCombFilter;
import matt.dsp.PeakCalculator;
import matt.dsp.FastFourierTransform;
import javax.sound.sampled.*;
import java.io.*;
import java.awt.*;
import java.util.*;
/**
 *
 * @author bduggan
 */
public class ODCFTranscriber {
    
    int numFilters = 12;
    
    Clip clip;
    byte[] audioData;
    protected int frameSize;
    protected int hopSize;
    protected int sampleRate;
    private int numSamples;    
    protected String inputFile;
    AudioInputStream audioInputStream;
    private float oldPowers[] = new float[numFilters];
    private float powers[] = new float[numFilters];
    private String abcTranscription;

    private float silenceThreshold = 1500.0f;
    
    protected GUI gui;
    
    protected TranscribedNote[] transcribedNotes;
    
    String defaultFundamental = "D";
    
    private int dynamicThresholdTime = 100; // in milliseconds
    
    TimeDomainCombFilter[] tdFilters = new TimeDomainCombFilter[numFilters];
    FrequencyDomainCombFilter[] fdFilters = new FrequencyDomainCombFilter[24];
        
    private Graph frameGraph;
    private Graph signalGraph;
    private Graph odfGraph;
    private SourceDataLine line = null;
    float staticThreshold = 0;
    
    protected float[] signal;
    
    private boolean isPlaying = false;
  
    /** Creates a new instance of Transcriber */
    public ODCFTranscriber() {
        frameSize = 2048;
        hopSize = (int) ((float) frameSize * 0.25f);                   
    }
    
    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void transcribea()
    {
        new Thread()
        {
            public void run()
            {
                transcribe();
            }
        }.start();
    }
    
    public void loadAudio()
    {
        MattProperties.setString("fundamentalNote", "D");
                
        try
        {
            File soundFile = new File(inputFile);
            Logger.log("Processing: " + soundFile.getName());
            
            int iK = soundFile.getName().indexOf("[");
            if (iK > -1)                
            {
                int iKK = soundFile.getName().indexOf("]");
                // Set the fundamental note
                
                String fundamentalNote = soundFile.getName().substring(iK + 1, iKK);
                MattProperties.setString("fundamentalNote", fundamentalNote);
                
            }

            audioInputStream = null;
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);            
            AudioFormat	format = audioInputStream.getFormat();
            numSamples = (int) audioInputStream.getFrameLength();
            Logger.log("Length of the stream in samples: " + numSamples);            
            Logger.log("Loading the signal...");
            
            audioData = new byte[(int) numSamples * 2];    
            signal = new float[numSamples];
            audioInputStream.read(audioData, 0, (int) numSamples * 2);
            sampleRate = (int) format.getSampleRate();

            boolean bigEndian = format.isBigEndian();                                 
            // Copy the signal from the file to the array            
            gui.getProgressBar().setValue(0);
            gui.getProgressBar().setMaximum(numSamples);
            for (int signalIndex = 0 ; signalIndex < numSamples; signalIndex ++)
            {
                signal[signalIndex] = ((audioData[(signalIndex * 2) + 1] << 8) + audioData[signalIndex * 2]);
                gui.getProgressBar().setValue(signalIndex);
            }            
            Logger.log("Removing silence at the start...");
            removeSilence();            
            Logger.log("Graphing...");
            if (Boolean.parseBoolean("" + MattProperties.getString("drawSignalGraphs")) == true)
            {
                signalGraph.getDefaultSeries().setData(signal);
                signalGraph.getDefaultSeries().setGraphType(Series.LINE_GRAPH);
                
                signalGraph.repaint();
            }
            Logger.log("Done.");
            
            /*
             PrintWriter pw = new PrintWriter(new File("graph.txt"));
            for (int i = 0 ; i < signal.length; i ++)
            {
                pw.println(signal[i]);
            }
            pw.close();
             */
        }
        catch (Exception e)
        {
            Logger.log("Could not load audio file " + inputFile);
            e.printStackTrace();
        }	
    }
    
    public void removeSilence()
    {
        float average = 0;
        
        int frame = 512;
        
        int i;
        for (i = 0 ; i < signal.length ; i += frame)
        {
            float frameAverage = 0;
            int j = 0;
            for (j = 0 ; (j < frame) && (j + i < signal.length) ; j ++)
            {
                frameAverage += Math.abs(signal[i + j]);
            }
            frameAverage /= (float) j;
            if (frameAverage > getSilenceThreshold())
            {
                break;
            }
        }
        int newSize = signal.length - i;
        if (newSize > 0)
        {
            float[] newSignal = new float[newSize];
            for (int j = 0; j < newSignal.length ; j ++)
            {
                newSignal[j] = signal[j + i];
            }
            Logger.log("" + ((float) i / sampleRate) + " seconds of silence removed from the start");
            signal = newSignal;
            numSamples  = signal.length;
        }
        else
        {
            Logger.log("No audio found!");
            signal = null;
            numSamples = 0;
        }
    }
    
    public void transcribe()
    {        
        File soundFile = new File(inputFile);
        Logger.log("Processing: " + soundFile.getName());
            
        int iK = soundFile.getName().indexOf("[");
        if (iK > -1)                
        {
            int iKK = soundFile.getName().indexOf("]");

            String fundamentalNote = soundFile.getName().substring(iK + 1, iKK);
            MattProperties.setString("fundamentalNote", fundamentalNote);

        }

        gui.setTitle(getInputFile());
        gui.enableButtons(false);
                        
        try
        {
            dynamicThresholdTime = Integer.parseInt(MattProperties.instance().get("DynamicThresholdTime").toString());
            
            Logger.log("Configuring filters...");
            configureFilters();               
            int numHops = (numSamples / hopSize);
            int odfSize =  numHops - 1;
            
            float[] frame = new float[frameSize];            
            float[] odf = new float[odfSize];            
            
            signalGraph.getDefaultSeries().clearLines();
            
            // Iterate through the signal a hop at a time
            Logger.log("Calculating harmonicity...");
            gui.getProgressBar().setValue(0);
            gui.getProgressBar().setMaximum(numHops);
            
            int currentSample = 0;
            int hopIndex = 0;
            int odfIndex = 0;
            for (hopIndex = 0 ; hopIndex < numHops ; hopIndex ++)
            {
                gui.getProgressBar().setValue(hopIndex);
                // Logger.log("Calculating harmonicity at sample " + (hopIndex * hopSize));
                
                if (((hopIndex * hopSize) + frameSize) > numSamples)
                {
                    break;
                }
                // Make a frame
                for (int frameIndex = 0 ; frameIndex < frameSize; frameIndex ++)
                {
                    currentSample = (hopIndex * hopSize) + frameIndex;
                    frame[frameIndex] = signal[currentSample];         
                }                 
                
                // Calculate the energy in each frequency range
                                
                // Calculate the harmonicity value
                // Calculate the ODF
                for (int i = 0 ; i < tdFilters.length; i ++)
                {
                    tdFilters[i].setFrame(frame);
                    powers[i] = tdFilters[i].calculateHarmonicity();                     
                    if (hopIndex > 0)
                    {
                        odf[odfIndex] += Math.pow(powers[i] - oldPowers[i], 2);                        
                    }
                    oldPowers[i] = powers[i];
                }       
                if (hopIndex > 0)
                {
                    odfIndex ++;
                }
                if (Boolean.parseBoolean("" + MattProperties.getString("drawFrameGraphs")) == true)
                {
                    frameGraph.getDefaultSeries().setScale(false);
                    frameGraph.getDefaultSeries().setData(frame);                    
                    frameGraph.repaint();
                }                
            }
            
            // Now calculate the proposed onsets
            Logger.log("Calculating onsets...");
            Vector<Integer> onsetsVector = new Vector();            
            onsetsVector = PeakCalculator.calculatePeaks(odf, 1, odf.length, 0);
                                    
            // Now calculate the dynamic threshold
            Logger.log("Calculating dynamic threshold...");
            float[] odfThreshold = calculateDynamicThreshold(odf, dynamicThresholdTime);
            
            if (Boolean.parseBoolean("" + MattProperties.getString("drawODFGraphs")) == true)
            {
                odfGraph.clear();            
                odfGraph.getDefaultSeries().setScale(true);                
                odfGraph.setScalingFactor(MattProperties.getFloat("scaleODFFactor"));                
                odfGraph.getDefaultSeries().setData(odf);                
                odfGraph.getDefaultSeries().addHorizontalLine(this.staticThreshold);
                odfGraph.getDefaultSeries().setPlotPoints(true);

                // Plot the ODF threshold on the ODF graph
                Series odfThresholdSeries = new Series(odfGraph);
                odfThresholdSeries.setScale(false);                
                odfThresholdSeries.setData(odfThreshold);
                odfThresholdSeries.setMin(odfGraph.getDefaultSeries().getMin());
                odfThresholdSeries.setMax(odfGraph.getDefaultSeries().getMax());                                   
                odfThresholdSeries.setGraphType(Series.BAR_GRAPH);
                odfThresholdSeries.setSeriesColour(Color.BLUE);
                odfGraph.addSeries(odfThresholdSeries);
            }
            // Remove any onsets lower than the threshold
            removeSpuriousOnsets(onsetsVector, odfThreshold, odf);
                        
            // Convert the onsets to signal points
            float[] odfSignal = new float[onsetsVector.size() + 2];            
            odfSignal[0] = 0;
            int odfSignalIndex = 1;
            gui.getProgressBar().setValue(0);
            gui.getProgressBar().setMaximum(onsetsVector.size());
            
            for (int i = 0 ; i < onsetsVector.size(); i ++)            
            {
                gui.getProgressBar().setValue(i);
                int index = ((Integer)onsetsVector.elementAt(i)).intValue();
                int signalIndex = odfIndexToSignal(index);
                odfSignal[odfSignalIndex ++] = signalIndex;
            }
            odfSignal[odfSignal.length - 1] = (signal.length - 1);
            
            // Plot the onsets
            
            if (MattProperties.getBoolean("drawODFGraphs"))
            {
                Enumeration en = onsetsVector.elements();

                while (en.hasMoreElements())
                {
                    int index = ((Integer)en.nextElement()).intValue();
                    odfGraph.getDefaultSeries().addVerticalLine(index);                
                }
                odfGraph.repaint();
            }
            for (int i = 0 ; i < odfSignal.length ; i ++)
            {
                signalGraph.getDefaultSeries().addVerticalLine2(odfSignal[i]);
            }

            signalGraph.repaint();
            
            
            transcribedNotes = calculateNotesUsingFFT(odfSignal, signal, sampleRate);  
            
            ABCTranscriber abcTranscriber = new ABCTranscriber(this);
            // abcTranscriber.makeScale("D", "Major");
            abcTranscriber.printScale();
            String notes = null;
            if (MattProperties.getString("searchMethod").equalsIgnoreCase("bryan"))
            {
                 notes = abcTranscriber.convertToABC();
            }
            if (MattProperties.getString("searchMethod").equalsIgnoreCase("parsons"))
            {
                notes = abcTranscriber.convertToParsons();
            }
            
            if (MattProperties.getString("searchMethod").equalsIgnoreCase("semex"))
            {
                notes = abcTranscriber.convertToMidi();
            }
            
            if (MattProperties.getString("mode").equals("client"))
            {
                gui.getTxtABC().setText("");
                gui.getTxtABC().append(notes);
            }
            abcTranscription = notes;
            Logger.log("Notes after onset post processing:");
            printNotes();
            gui.enableButtons(true);
            
            // playTranscription(transcribedNotes);        
        }
        
        catch (Exception e)
        {
            e.printStackTrace();        
            gui.enableButtons(true);
            // System.exit(0);
        }
         MattProperties.setString("fundamentalNote", defaultFundamental);
         Logger.log("Done.");
    }    
    
    private int odfIndexToSignal(int odfIndex)
    {
        return (odfIndex * hopSize) + (hopSize * 3);
    }
    
    private void removeSpuriousOnsets(Vector onsetsVector, float[] odfThreshold, float[] odf)
    {
        int dynamicThresholdSamples = (int) ((float) sampleRate * ((float) dynamicThresholdTime / 1000.0f));
        int dynamicThresholdWidth = dynamicThresholdSamples / hopSize;
        
        for (int i =0 ; i < onsetsVector.size() ; i ++)
        {
            int onsetIndex = ((Integer) onsetsVector.elementAt(i)).intValue();
            // Find the heigth of the threshold at this index
            int thresholdIndex = (int) Math.floor(((float) onsetIndex / (float) dynamicThresholdWidth));
            // If there are too few thresholds, remove the onset
            if (thresholdIndex > (odfThreshold.length - 1))
            {
                onsetsVector.remove(i --);
            }
            // Or if the onset is lower than the threshold
            else if (odf[onsetIndex] < odfThreshold[thresholdIndex])
            {
                onsetsVector.remove(i --);
            }
        }        
    }

    private TranscribedNote[] calculateNotesUsingFFT(float[] odfSignal, float[] signal, int sampleRate)
    {
        Logger.log("Calculating frequencies of " + (odfSignal.length - 1) + " notes");
        Vector<TranscribedNote> notes = new Vector();
        gui.clearFFTGraphs();
        // Now figure out the frequencies in each note
        FastFourierTransform fft = new FastFourierTransform();
        EnergyCalculator ec = new EnergyCalculator();
        ec.setSignal(signal);
        
        gui.getProgressBar().setValue(0);
        gui.getProgressBar().setMaximum(odfSignal.length);
            
        for (int i = 1; i < odfSignal.length ; i ++)
        {            
            gui.getProgressBar().setValue(i);
            int signalStart =  (int) odfSignal[i -1];
            int signalEnd = (int) odfSignal[i];
            int signalLength = signalEnd - signalStart;
            int smallestPowerOf2 = FastFourierTransform.smallestPowerOf2(signalLength);
            Logger.log("Note: " + (i -1));
            ec.setStart(signalStart);
            ec.setEnd(signalEnd);
            float energy = ec.calculateAverageEnergy();
            Logger.log("Energy: " + ec.formatEnergy(energy));
            
            int fftFrameSize = (int) Math.pow(2, smallestPowerOf2);
            Logger.log("Performing FFT " + (i - 1) + " on frame size " + fftFrameSize);
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

            //float cFrequency = pitchDetector.cepstrumFrequency(fftOut, sampleRate, fftFrameSize);
            // Logger.log("Frequency by Cepstrum algorithm value: " + cFrequency);

            if (MattProperties.getString("pitchDetector").equals("bryan"))
            {
                float bFrequency = pitchDetector.maxBryanFrequency(fftOut, sampleRate, fftFrameSize);
                Logger.log("Frequency by Bryan's algorithm value: " + bFrequency);
                frequency = bFrequency;
            }
            else if (MattProperties.getString("pitchDetector").equals("mikel"))
            {
                float mFrequency = pitchDetector.mikelsFrequency(fftOut, sampleRate, fftFrameSize);
                Logger.log("Frequency by Mikels's algorithm value: " + mFrequency);
                frequency = mFrequency;
            }
            else
            {
                float hFrequency = pitchDetector.maxPeek(fftOut, sampleRate, fftFrameSize); 
                Logger.log("Frequency by highest value: " + hFrequency);

                frequency = hFrequency;                
            }
            float onset, duration;
            onset = (float) signalStart / (float) sampleRate;
            duration = (float) signalLength / (float) sampleRate;
            TranscribedNote newNote = new TranscribedNote(frequency, onset, duration);
            newNote.setEnergy(energy);
            notes.addElement(newNote);            
            
            if (Boolean.parseBoolean("" + MattProperties.getString("drawFFTGraphs")) == true)
            {
                Graph fftGraph = new Graph();

                fftGraph.setBounds(0, 0, 1000, 1000);
                fftGraph.getDefaultSeries().setScale(false);
                fftGraph.getDefaultSeries().setData(fftOut);                
                MattGuiNB.instance().addFFTGraph(fftGraph, "" + newNote.getStart());
            }
            Logger.log("");             
        }

        OrnamentationFilter opp = new OrnamentationFilter(notes, sampleRate, signal);
        TranscribedNote[] postProcessed = opp.filter();
        for (int i = 0 ; i < postProcessed.length ; i ++)
        {
            float start = postProcessed[i].getStart();
            float sigStart = start * sampleRate;
            signalGraph.getDefaultSeries().addVerticalLine(sigStart);                    
        }
        return postProcessed;
    }
    
    public void printNotes()
    {
        for (int i = 0 ; i < transcribedNotes.length ; i ++)
        {
            Logger.log(transcribedNotes[i]);
        }
    }
    
    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public int getHopSize() {
        return hopSize;
    }

    public void setHopSize(int hopSize) {
        this.hopSize = hopSize;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }
    
    private void configureFilters()
    {
        float ratio = 1.05946309436f;
        float frequency = MattProperties.getFloat(MattProperties.getString("fundamentalNote")) / (float) Math.pow(ABCTranscriber.RATIO, 12);
        // Create a filter for each of the semitones in the key of D
        System.out.println("FILTERS:");
        for (int i = 0 ; i < tdFilters.length ; i ++)
        {            
            tdFilters[i] = new TimeDomainCombFilter();
            tdFilters[i].setSampleRate(sampleRate);
            tdFilters[i].setFrequency((int) frequency);     
            System.out.println(tdFilters[i].getFrequency() + "\t" + tdFilters[i].getDelay());
            frequency = frequency * ratio;            
        }
        
        /*
         frequency = MattProperties.getFloat(MattProperties.getString("fundamentalNote"));
        for (int i = 0 ; i < 24 ; i ++)
        {            
            fdFilters[i] = new FrequencyDomainCombFilter();
            fdFilters[i].setSampleRate(sampleRate);
            fdFilters[i].setFundamental((int) frequency);            
            frequency = frequency * ratio;            
        }
         */
    }
        
    protected float sampleToSeconds(int sample)
    {
        return (float) sample / (float) sampleRate;
    }
    
    private float[] calculateDynamicThreshold(float[] odf, int dynamicThresholdTime)
    {
        int dynamicThresholdSamples = (int) ((float) sampleRate * ((float) dynamicThresholdTime / 1000.0f));
        int dynamicThresholdWidth = dynamicThresholdSamples / hopSize;
        int numThresholds = odf.length / dynamicThresholdWidth;
        float odfThreshold[] = new float[numThresholds];
        Stats odfStats = new Stats(odf);
        staticThreshold = odfStats.average();
        int odfThresholdIndex = 0;
        //for (int i = 0 ; i < (odf.length - dynamicThresholdWidth); i += dynamicThresholdWidth)
        for (int i = 0 ; i < (odf.length - dynamicThresholdWidth); i += dynamicThresholdWidth)
        {
            odfStats.setStart(i);
            odfStats.setEnd(i + dynamicThresholdWidth);                
            odfThreshold[odfThresholdIndex] = staticThreshold  + odfStats.standardDeviation();
            odfThresholdIndex ++;
        }
        return odfThreshold;
    }
    
        
    void playTranscription(TranscribedNote[] transcribedNotes)
    {
        isPlaying = true;
        for (int i = 0 ; i < transcribedNotes.length; i ++)
        {
            if (!isPlaying)
            {
                break;
            }
            TonePlayer.playTone(transcribedNotes[i].getFrequency(), transcribedNotes[i].getDuration(), 0.25f);
        }
        isPlaying = false;
    }
    
    void playOriginal()
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    if ((line != null) && line.isActive())
                    {
                        line.stop();
                    }
                    else
                    {
                        AudioFormat audioFormat = audioInputStream.getFormat();
                        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);                    
                        line = (SourceDataLine) AudioSystem.getLine(info);
                        line.open(audioFormat);
                        line.start();
                        line.write(audioData, 0, audioData.length);
                        line.drain();
                        line.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    void playTranscription()
    {
        playTranscription(transcribedNotes);
    }

    public GUI getGui() {
        return gui;
    }
    
    public void cleanup()
    {
        try
        {
            
            audioInputStream.close();
            audioInputStream = null;
            audioData = null;
            oldPowers  = null;
            powers  = null;
            transcribedNotes = null;
            tdFilters = null;
            fdFilters = null;
            signal = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setGui(GUI mattGui) {
        this.gui = mattGui;
        frameGraph  = mattGui.getFrameGraph();
        signalGraph  = mattGui.getSignalGraph();
        odfGraph = mattGui.getOdfGraph();
    }

    public int getDynamicThresholdTime() {
        return dynamicThresholdTime;
    }

    public void setDynamicThresholdTime(int dynamicThresholdTime) {
        this.dynamicThresholdTime = dynamicThresholdTime;
    }      
    public TranscribedNote[] getTranscribedNotes() {
        return transcribedNotes;
    }

    public float[] getSignal()
    {
        return signal;
    }

    public void setSignal(float[] signal)
    {
        this.signal = signal;
        numSamples = signal.length;
    }

    public String getAbcTranscription()
    {
        return abcTranscription;
    }

    public void setAbcTranscription(String abcTranscription)
    {
        this.abcTranscription = abcTranscription;
    }

    public

    boolean isIsPlaying()
    {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying)
    {
        this.isPlaying = isPlaying;
    }

    /**
     * @return the silenceThreshold
     */
    public float getSilenceThreshold()
    {
        return silenceThreshold;
    }

    /**
     * @param silenceThreshold the silenceThreshold to set
     */
    public void setSilenceThreshold(float silenceThreshold)
    {
        this.silenceThreshold = silenceThreshold;
    }
}
