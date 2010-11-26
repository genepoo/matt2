/*
 * MattApplet.java
 *
 * Created on 08 January 2009, 22:18
 */

package matt.web;

import matt.MattProperties;
import matt.Logger;
import matt.ODCFTranscriber;
import matt.MattABCTools;
import matt.STFTTranscriber;
import matt.Series;
import abc.midi.*;
import javax.sound.midi.*;
import abc.parser.TuneBook;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import javax.swing.table.*;
//import java.util.*;
import abc.notation.Tune;
import javax.swing.plaf.ColorUIResource;


import abc.midi.*;
import java.applet.AppletContext;
import java.awt.Color;
import java.net.MalformedURLException;
import java.util.logging.Level;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import matt.Graph;
import java.io.*;
import java.net.URLEncoder;
import java.util.TimerTask;
import java.util.Timer;
import javax.sound.sampled.*;
import javax.swing.UIManager.LookAndFeelInfo;
import matt.*;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;



/**
 *
 * @author  Bryan Duggan
 */
public class MattApplet extends javax.swing.JApplet implements matt.GUI {

    CheckList newframe = new CheckList();
    static public MattApplet _instance;
    Capture capture = new Capture();
    Playback playback = new Playback();
    AudioInputStream audioInputStream;
    String errStr;
    AudioFormat format = null;  
    final int bufSize = 16384;
    double duration, seconds;
    ODCFTranscriber transcriber = new ODCFTranscriber();
	
    float[] signal;
    
    private TunePlayer tunePlayer = new TunePlayer();
    
    private int sampleRate;
    private int numSamples;    
    byte[] audioData;

    public MattApplet()
    {

        _instance = this;
    }

//    @Override
//    public void paint(Graphics g) {
//
//        super.paint(g);
//
//        Graphics2D g2d = (Graphics2D) g;
//        GradientPaint gradient = new GradientPaint(0, 0, Color.BLUE, this.getWidth(),   this.getHeight(),Color.CYAN);
//        g2d.setPaint(gradient);
//        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
//    }
    
    private void myInit()
    {
        // Add the graphs...
        signalGraph.setBounds(10,10,getBounds().width - 160 ,80);
        /*
         Dimension d = new Dimension();
        d.width = getBounds().width - 20;
        d.height = txtStatus.getSize().height;

        txtStatus.setSize(d);
        txtStatus.setPreferredSize(d);
        */
       
        getContentPane().add(signalGraph);
        //setBounds(0, 0, 630, 300);
        signalGraph.setBackground(new Color(176,210,13));
        format = new AudioFormat(44100, 16, 1, true, false);
        transcriber.setGui(this);


        MattProperties.instance(false).setProperty("drawFFTGraphs", "false");
        MattProperties.instance(false).setProperty("drawODFGraphs", "false");
        MattProperties.instance(false).setProperty("tansey", "false");
        MattProperties.instance(false).setProperty("applet", "true");
        _instance = this;
        
        tunePlayer.addListener(new TunePlayerAdapter()
        {
            public void playEnd(PlayerStateChangeEvent e)
            {
                btnPlayTranscribed.setText("Play ABC");
            }

        });
        cmbFundamental.setSelectedItem(MattProperties.instance().getString("fundamentalNote"));
    }


    /** Initializes the applet MattApplet */
    public void init() {
        MattProperties.instance(true);
        signalGraph = new Graph();
        tunePlayer.start();
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
//                    try
//                    {
//                        String os = System.getProperty("os.name");
//                        if ((os.indexOf("Windows") > -1))
//                        {
//                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                        }
//                        else
//                        {
//                            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//                        }
//
//                        //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//                    }
//                    catch(Exception e)
//                    {
//                        System.out.println("Error setting native LAF: " + e);
//                    }
                    try {
                        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            if ("Nimbus".equals(info.getName())) {
                                UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (UnsupportedLookAndFeelException e) {
                        // handle exception
                    } catch (ClassNotFoundException e) {
                        // handle exception
                    } catch (InstantiationException e) {
                        // handle exception
                    } catch (IllegalAccessException e) {
                        // handle exception
                    }
                    initComponents();
                    myInit();

                }

            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnPlayTranscribed = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        btnRecord = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        slSilence = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        cmbFundamental = new javax.swing.JComboBox();
        btnTranscribe = new javax.swing.JButton();
        cmbCorpus = new javax.swing.JComboBox();
        cmbType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtABC = new javax.swing.JTextArea();
        btnFind = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();
        cmbTranscriber = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(221, 221, 221));

        jPanel1.setFocusable(false);
        jPanel1.setOpaque(false);

        btnPlayTranscribed.setText("Play ABC");
        btnPlayTranscribed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayTranscribedActionPerformed(evt);
            }
        });

        btnPlay.setText("Playback");
        btnPlay.setEnabled(false);
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        btnRecord.setText("Record");
        btnRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordActionPerformed(evt);
            }
        });

        jLabel6.setText("Silence threshold:");

        slSilence.setMaximum(6000);
        slSilence.setValue(1500);

        jLabel1.setText("Fundamental:");

        cmbFundamental.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bb", "C", "D", "Eb", "F", "G" }));
        cmbFundamental.setName("cmbFundamental"); // NOI18N
        cmbFundamental.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbFundamentalItemStateChanged(evt);
            }
        });
        cmbFundamental.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFundamentalActionPerformed(evt);
            }
        });

        btnTranscribe.setText("Transcribe");
        btnTranscribe.setEnabled(false);
        btnTranscribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTranscribeActionPerformed(evt);
            }
        });

        cmbCorpus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "thesession.org", "Norbeck", "O'Neill's 1001", "Ceol Rince na hÉireann 1", "Ceol Rince na hÉireann 2", "Ceol Rince na hÉireann 3", "Ceol Rince na hÉireann 4", "Johnny O'Leary", "Nigel Gatherer", "The Microphone Rambles", "John Tose", "Jack Campin", "Fife and Drum", "Nottingham Database" }));
        cmbCorpus.setName("cmbCorpus"); // NOI18N
        cmbCorpus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCorpusActionPerformed(evt);
            }
        });

        cmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Reel", "Jig", "Slip Jig", "Slide", "March", "Mazurka", "Polka", "Hop", "Barndance", "Double Jig", "Single Jig", "Fling", "Halling", "Highland", "Hornpipe", "Set dance", "Polska J", "Polska K1", "Polska L1", "Polska O", "Strathspey", "Three-two", "Waltz" }));
        cmbType.setName("cmbLimit"); // NOI18N
        cmbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTypeActionPerformed(evt);
            }
        });

        jLabel3.setText("Tune Book:");

        jLabel4.setText("Tune Type:");

        txtStatus.setText("<Press record to begin!>");
        txtStatus.setMaximumSize(new java.awt.Dimension(68, 14));
        txtStatus.setMinimumSize(new java.awt.Dimension(68, 14));
        txtStatus.setPreferredSize(new java.awt.Dimension(68, 20));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(slSilence, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btnTranscribe, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .add(btnRecord, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btnPlayTranscribed, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .add(btnPlay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .add(cmbFundamental, 0, 86, Short.MAX_VALUE)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel4)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cmbType, 0, 118, Short.MAX_VALUE)
                            .add(cmbCorpus, 0, 118, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {btnPlayTranscribed, btnRecord, btnTranscribe, jLabel1, jLabel6}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel6)
                    .add(slSilence, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(cmbFundamental, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnTranscribe)
                            .add(btnPlayTranscribed)))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btnRecord)
                        .add(btnPlay)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(cmbCorpus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmbType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                    .add(jLabel4))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {btnPlay, cmbFundamental}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel2.setFocusable(false);
        jPanel2.setOpaque(false);

        jLabel2.setText("ABC to search for:");

        txtABC.setColumns(20);
        txtABC.setLineWrap(true);
        txtABC.setRows(5);
        jScrollPane2.setViewportView(txtABC);

        btnFind.setText("Search!");
        btnFind.setMaximumSize(new java.awt.Dimension(32767, 32767));
        btnFind.setMinimumSize(new java.awt.Dimension(73, 18));
        btnFind.setPreferredSize(new java.awt.Dimension(78, 20));
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnFind, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                        .add(jLabel2)
                        .add(progressBar, 0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnFind, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addContainerGap())
        );

        cmbTranscriber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Method 1", "Method 2" }));
        cmbTranscriber.setOpaque(false);
        cmbTranscriber.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTranscriberItemStateChanged(evt);
            }
        });
        cmbTranscriber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTranscriberActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(86, 86, 86)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbTranscriber, 0, 87, Short.MAX_VALUE)
                .add(1408, 1408, 1408))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1227, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(56, 56, 56)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmbTranscriber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(152, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
	
private void btnFindActionPerformed(ActionEvent evt)//GEN-FIRST:event_btnFindActionPerformed
	{//GEN-HEADEREND:event_btnFindActionPerformed
    /*
     String url = "" + MattApplet._instance.getDocumentBase();
    url = url.substring(0, url.lastIndexOf("/")) + "/";
    url += "search.php?q=" + URLEncoder.encode(txtABC.getText());
     */
    String toFind = txtABC.getText();
    toFind = MattABCTools.expandLongNotes(toFind);
    toFind = MattABCTools.stripWhiteSpace(toFind);
    toFind = MattABCTools.stripBarDivisions(toFind);
    toFind = toFind.toUpperCase();


    String docBase = "" + getDocumentBase();
    int li = docBase.lastIndexOf("/");
    String url = docBase.substring(0, li) + "/search8.jsp?version=1.4&q=" + URLEncoder.encode(toFind);
    System.out.println(url);
    //String url = "http://localhost:8080/MattWeb/search8.jsp?q=" + URLEncoder.encode(toFind);
    //String url = "http://skooter500.s156.eatj.com/MattWeb/search.jsp?q=" + URLEncoder.encode(toFind);
    //String url = "http://www.comp.dit.ie/matt2/search8.jsp?q=" + URLEncoder.encode(toFind);
    url += "&corpus=" + (cmbCorpus.getSelectedIndex());
    url += "&type=" + cmbType.getSelectedItem();
    url += "&silence=" + (int) slSilence.getValue();
    url += "&method=" + cmbTranscriber.getSelectedItem();
    System.out.println("URL: " + url);
    try
    {
        getAppletContext().showDocument(new java.net.URL(url), "results");
    }
    catch (MalformedURLException ex)
    {
        Logger.log(ex.toString());
    }
	}//GEN-LAST:event_btnFindActionPerformed


class RemindTask extends TimerTask {
    int secleft = 5;

    public void run() {
      if (countdown) {
          if (secleft > 0) {
            txtStatus.setText("Recording in " + secleft + "...");
            secleft--;
          } else {
            txtStatus.setText("Recording.");
            btnRecord.setText("Record");
            countdown = false;
            record();
            this.cancel();
          }
        }
       else {
          txtStatus.setText("<Press record to begin!>");
          this.cancel();
       }
    } 
  }

public boolean countdown = false;

private void btnRecordActionPerformed(ActionEvent evt) {
    //.setVisible(true);
    Timer timer = new Timer();
    if (btnRecord.getText().equals("Record"))
    {
        btnRecord.setText("Stop");
        timer.schedule(new RemindTask(), 0, 1 * 1000);
        countdown = true;
    }
    else if (countdown)
    {
        timer.cancel();
        countdown = false;
        btnRecord.setText("Record");
    }
    else
    {
        timer.cancel();
        record();
    }
}

private void record() {
    //timer.cancel();
    if (btnRecord.getText().equals("Record"))
    {
        signalGraph.getDefaultSeries().clearLines();
        signalGraph.getDefaultSeries().clear();
        txtABC.setText("");
        capture.start();
        btnRecord.setText("Stop");
    }
    else
    {
        // lines.removeAllElements();
        capture.stop();
        btnRecord.setText("Record");
		
        try
        {
            synchronized(capture)
            {
                capture.wait();
            }
            AudioFormat format = audioInputStream.getFormat();
        
            numSamples = (int) audioInputStream.getFrameLength();
            audioData = new byte[(int) numSamples * 2];
            signal = new float[numSamples];
            audioInputStream.read(audioData, 0, (int) numSamples * 2);

            sampleRate = (int) format.getSampleRate();
            transcriber.setSampleRate(sampleRate);
            boolean bigEndian = format.isBigEndian();
            // Copy the signal from the file to the array
            getProgressBar().setValue(0);
            getProgressBar().setMaximum(numSamples);
            for (int signalIndex = 0; signalIndex < numSamples; signalIndex++)
            {
                signal[signalIndex] = ((audioData[(signalIndex * 2) + 1] << 8) + audioData[signalIndex * 2]);                                             
                getProgressBar().setValue(signalIndex);
                //System.out.println(signal[signalIndex]);
            }
            
            Logger.log("Removing silence at the start...");
            transcriber.setSignal(signal);
            transcriber.setSilenceThreshold(slSilence.getValue());
            transcriber.removeSilence();           
            signal = transcriber.getSignal();
            Logger.log("Graphing...");
            if (Boolean.parseBoolean("" + MattProperties.getString("drawSignalGraphs")) == true)
            {
                signalGraph.getDefaultSeries().setData(signal);
                signalGraph.getDefaultSeries().setGraphType(Series.LINE_GRAPH);

                signalGraph.repaint();
            }
            Logger.log("Done.");
            btnPlay.setEnabled(true);
            btnTranscribe.setEnabled(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Logger.log("Could not plot audio: " + e.getMessage());
            Logger.log("Could not hear the melody. Try adjusting the silence threshold.");
        }
    }
}                                         

private void btnPlayActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
    if (btnPlay.getText().equals("Playback"))
    {
        playback.start();
        btnPlay.setText("Stop");
    }
    else
    {
        playback.stop();
        btnPlay.setText("Playback");
    }
}//GEN-LAST:event_btnPlayActionPerformed

private void btnTranscribeActionPerformed(ActionEvent evt) {                                              
        try
        {

            txtABC.setText("");
            transcriber.setInputFile("");
            MattProperties.setString("fundamentalNote", "" + cmbFundamental.getSelectedItem());
            transcriber.transcribea();
            btnPlayTranscribed.setEnabled(true);
        }
        catch (Exception ex)
        {
            Logger.log(ex.toString());
            ex.printStackTrace();
        }
}

private void btnPlayTranscribedActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnPlayTranscribedActionPerformed
        if (tunePlayer.isPlaying())
        {
            tunePlayer.stopPlaying();
            btnPlayTranscribed.setText("Play ABC");
            return;
        }        
        try 
        {
            btnPlayTranscribed.setText("Stop");
            StringBuffer tuneText = new StringBuffer();
            tuneText.append("X:1\r\n");
            tuneText.append("T:Temp\r\n");
            tuneText.append("R:Reel\r\n");
            tuneText.append("M:C|\r\n");
            tuneText.append("L:1/8\r\n");
            tuneText.append("K:D\r\n");
            tuneText.append(getTxtABC().getText());
            TuneBook book = new TuneBook();
            book.putTune(tuneText.toString());            
            Tune aTune = book.getTune(1);
            tunePlayer.play(aTune);            
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }       

}//GEN-LAST:event_btnPlayTranscribedActionPerformed

private void cmbFundamentalActionPerformed(ActionEvent evt)//GEN-FIRST:event_cmbFundamentalActionPerformed
{//GEN-HEADEREND:event_cmbFundamentalActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_cmbFundamentalActionPerformed

private void cmbFundamentalItemStateChanged(ItemEvent evt)//GEN-FIRST:event_cmbFundamentalItemStateChanged
{//GEN-HEADEREND:event_cmbFundamentalItemStateChanged
    MattProperties.setString("fundamentalNote", "" + cmbFundamental.getSelectedItem());
}//GEN-LAST:event_cmbFundamentalItemStateChanged

private void cmbTypeActionPerformed(ActionEvent evt)//GEN-FIRST:event_cmbTypeActionPerformed
{//GEN-HEADEREND:event_cmbTypeActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_cmbTypeActionPerformed

private void cmbTranscriberActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbTranscriberActionPerformed
{//GEN-HEADEREND:event_cmbTranscriberActionPerformed

}//GEN-LAST:event_cmbTranscriberActionPerformed

private void cmbTranscriberItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_cmbTranscriberItemStateChanged
{//GEN-HEADEREND:event_cmbTranscriberItemStateChanged
    if (cmbTranscriber.getSelectedItem().equals("Method 1"))
    {
        transcriber = new ODCFTranscriber();
    }
    else
    {
        transcriber = new STFTTranscriber();
        
    }
    transcriber.setGui(this);
    transcriber.setSampleRate(sampleRate);
    if (signal != null)
    {
        transcriber.setSignal(signal);
    }
}//GEN-LAST:event_cmbTranscriberItemStateChanged

private void cmbCorpusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCorpusActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_cmbCorpusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnPlayTranscribed;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnTranscribe;
    private javax.swing.JComboBox cmbCorpus;
    private javax.swing.JComboBox cmbFundamental;
    private javax.swing.JComboBox cmbTranscriber;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSlider slSilence;
    private javax.swing.JTextArea txtABC;
    private javax.swing.JLabel txtStatus;
    // End of variables declaration//GEN-END:variables
    private Graph signalGraph;
    
    public class Playback implements Runnable {

        SourceDataLine line;
        Thread thread;

        public void start() {
            thread = new Thread(this);
            thread.setName("Playback");
            thread.start();
        }

        public void stop() {
            thread = null;
        }
        
        private void shutDown(String message) {
            if ((errStr = message) != null) {
                System.err.println(errStr);
                signalGraph.repaint();
            }
            if (thread != null) {
                thread = null;
                /*
                captB.setEnabled(true);
                pausB.setEnabled(false);
                playB.setText("Play");
                 */
                btnPlay.setText("Playback");
            } 
        }

        public void run() {

            // make sure we have something to play
            if (audioInputStream == null) {
                shutDown("No loaded audio to play back");
                return;
            }
            // reset to the beginnning of the stream

             try {
                audioInputStream.reset();
            } catch (Exception e) {
                e.printStackTrace();
                shutDown("Unable to reset the stream\n" + e);
                
                return;
            }

            AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                        
            if (playbackInputStream == null) {
                shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
                return;
            }

            // define the required attributes for our line, 
            // and make sure a compatible line is supported.

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
                format);
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the source data line for playback.

            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufSize);
            } catch (LineUnavailableException ex) { 
                shutDown("Unable to open the line: " + ex);
                return;
            }

            // play back the captured audio data

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;

            // start the source data line
            line.start();

            while (thread != null) {
                try {
                    if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                        break;
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0 ) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }
                } catch (Exception e) {
                    shutDown("Error during playback: " + e);
                    break;
                }
            }
            // we reached the end of the stream.  let the data play out, then
            // stop and close the line.
            if (thread != null) {
                line.drain();
            }
            line.stop();
            line.close();
            line = null;
            shutDown(null);
        }
    } // End class Playback
        

    /** 
     * Reads data from the input channel and writes to the output stream
     */
    class Capture implements Runnable {

        TargetDataLine line;
        Thread thread;

        public void start() {
            errStr = null;
            thread = new Thread(this);
            thread.setName("Capture");
            thread.start();
        }

        public void stop() {
            thread = null;
        }
        
        private void shutDown(String message) {
            System.out.println(message);
            if ((errStr = message) != null && thread != null) {
                thread = null;
            }
        }

        
        public void run() {

            duration = 0;
            audioInputStream = null;
            System.out.println("0");

            // define the required attributes for our line, 
            // and make sure a compatible line is supported.
         
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, 
                format);
            System.out.println("1");
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }
            System.out.println("2");
            // get and open the target data line for capture.
            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) { 
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) { 
                shutDown(ex.toString());
                // showInfoDialog();
                return;
            } catch (Exception ex) { 
                shutDown(ex.toString());
                return;
            }
            
            System.out.println("3");
            // play back the captured audio data
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;
            
            line.start();
            System.out.println("4");
            while (thread != null) {
                if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
            }

            // we reached the end of the stream.  stop and close the line.
            line.stop();
            line.close();
            line = null;

            // stop and close the output stream
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // load bytes into the audio input stream for playback

            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

            long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
            duration = milliseconds / 1000.0;

            try {
                audioInputStream.reset();
            } catch (Exception ex) { 
                ex.printStackTrace(); 
                return;
            }
            synchronized(this)
            {
                notify();
            }
            // signalGraph.getDefaultSeries().setData(audioBytes);
        }
    } // End class Capture

    public void clearGraphs()
    {
        signalGraph.clear();
    }

    public Graph getSignalGraph()
    {
        return signalGraph;
    }

    public Graph getOdfGraph()
    {
        return null;
    }

    public JProgressBar getProgressBar()
    {
        return progressBar;
    }

    public void setTitle(String t)
    {
        
    }

    public void enableButtons(boolean b)
    {
        btnRecord.setEnabled(b);
        btnPlayTranscribed.setEnabled(b);
        btnFind.setEnabled(b);
        btnPlay.setEnabled(b);
        btnTranscribe.setEnabled(b);
        cmbCorpus.setEnabled(b);
        cmbFundamental.setEnabled(b);
        cmbType.setEnabled(b);
        txtABC.setEnabled(b);
        slSilence.setEnabled(b);
        cmbTranscriber.setEnabled(b);
    }

    public void clearFFTGraphs()
    {
        
    }

    public Graph getFrameGraph()
    {
        return null;
    }

    public JTextArea getTxtABC()
    {
        return txtABC;
    }
    
    public static void setStatus(String msg)
    {
        _instance.txtStatus.setText(msg);
    }
}
