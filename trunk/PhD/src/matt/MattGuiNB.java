/*
 * MattGuiNB.java
 *
 * Created on 16 July 2007, 17:18
 */

package matt;

import abc.midi.TunePlayer;
import abc.parser.TuneBook;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import javax.swing.table.*;
import java.util.*;
import abc.notation.Tune;


/**
 *
 * @author  Bryan
 */
public class MattGuiNB extends javax.swing.JFrame {
    
    /** Creates new form MattGuiNB */
    private MattGuiNB() {
        initComponents();
        
        // Add the graphs...
        frameGraph.setBounds(10,10,380,120);
        fftTabs.setBounds(400,10,380,120);
        signalGraph.setBounds(10,140, 770, 120);        
        odfGraph.setBounds(10,270, 770, 120);
        // odfThresholdGraph.setBounds(10,640, 1000, 200);
                
        getContentPane().add(frameGraph);
        getContentPane().add(fftTabs);
        getContentPane().add(signalGraph);
        getContentPane().add(odfGraph);
        // getContentPane().add(odfThresholdGraph);
 
        frameGraph.setBackground(Color.CYAN);
        signalGraph.setBackground(Color.GREEN);
        odfGraph.setBackground(Color.YELLOW);
        center(this);
        tunePlayer.start();
        MattProperties.instance();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spLog = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnPlayOriginal = new javax.swing.JButton();
        btnPlayTranscription = new javax.swing.JButton();
        btnChooseFile = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnClearLog = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtABC = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        btnTranscribe = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        btnPlayFound = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMatches = new javax.swing.JTable();
        btnQuit = new javax.swing.JButton();
        Learn = new javax.swing.JButton();
        btnAnalysed = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtBest = new javax.swing.JLabel();
        btnBest = new javax.swing.JButton();
        btnReindex = new javax.swing.JButton();
        btnBatch = new javax.swing.JButton();
        btnTranscribeAndFind = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 1, 1));

        spLog.setAutoscrolls(true);

        txtLog.setColumns(20);
        txtLog.setRows(5);
        spLog.setViewportView(txtLog);

        btnPlayOriginal.setText("Original");
        btnPlayOriginal.setName("btnPlayOriginal"); // NOI18N
        btnPlayOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayOriginalActionPerformed(evt);
            }
        });

        btnPlayTranscription.setText("Transcribed");
        btnPlayTranscription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayTranscriptionActionPerformed(evt);
            }
        });

        btnChooseFile.setText("Choose File");
        btnChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseFileActionPerformed(evt);
            }
        });

        jLabel2.setText("Log:");

        btnClearLog.setText("Clear Log");
        btnClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLogActionPerformed(evt);
            }
        });

        txtABC.setColumns(20);
        txtABC.setRows(5);
        jScrollPane1.setViewportView(txtABC);

        jLabel3.setText("ABC:");

        btnTranscribe.setText("Transcribe");
        btnTranscribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTranscribeActionPerformed(evt);
            }
        });

        btnFind.setText("Find");
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        btnPlayFound.setText("Found");
        btnPlayFound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayFoundActionPerformed(evt);
            }
        });

        jLabel4.setText("Matches:");

        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
        });

        tblMatches.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Matched", "ED"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMatches.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMatchesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMatches);

        btnQuit.setText("Quit");
        btnQuit.setName("btnQuit"); // NOI18N
        btnQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitActionPerformed(evt);
            }
        });

        Learn.setText("Learn");
        Learn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LearnActionPerformed(evt);
            }
        });

        btnAnalysed.setText("Analysed");
        btnAnalysed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalysedActionPerformed(evt);
            }
        });

        txtBest.setText("<Start searcing!>");

        btnBest.setText("Best");
        btnBest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBestActionPerformed(evt);
            }
        });

        btnReindex.setText("Reindex");
        btnReindex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReindexActionPerformed(evt);
            }
        });

        btnBatch.setText("Batch");
        btnBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatchActionPerformed(evt);
            }
        });

        btnTranscribeAndFind.setText("Trans & Find");
        btnTranscribeAndFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTranscribeAndFindActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(spLog, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBatch, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(btnTranscribeAndFind, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnChooseFile, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(btnTranscribe, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnQuit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFind, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAnalysed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPlayOriginal, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPlayFound)
                            .addComponent(btnPlayTranscription))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnClearLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBest, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(btnReindex)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Learn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(txtBest, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(42, 42, 42)
                        .addComponent(jLabel6)
                        .addGap(501, 501, 501))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnChooseFile, btnFind, btnPlayFound, btnPlayOriginal, btnPlayTranscription, btnTranscribe});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(394, 394, 394)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spLog, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnFind)
                                .addComponent(btnPlayOriginal)
                                .addComponent(btnTranscribeAndFind)
                                .addComponent(btnChooseFile, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnPlayFound)
                                .addComponent(btnClearLog))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnQuit)
                                .addComponent(btnTranscribe)
                                .addComponent(btnAnalysed)
                                .addComponent(btnPlayTranscription, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnBest)
                                .addComponent(Learn)
                                .addComponent(btnReindex))
                            .addComponent(btnBatch)))
                    .addComponent(txtBest))
                .addGap(149, 149, 149))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayFoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayFoundActionPerformed
         int row = tblMatches.getSelectedRow();
         ABCMatch match = (ABCMatch) tuneMatches.elementAt(row);         
         try
         {
             // TuneBook book = new TuneBook(new File(match.getFileName()));
             if (tunePlayer.isPlaying())
             {
                 tunePlayer.stopPlaying();
             }
             else
             {
                    Tune tune = match.getTune();
                     tunePlayer.play(tune);
             }
         }
         catch (Exception e)
         {
             System.out.println("Could not play tune");
             e.printStackTrace();
         }
    }//GEN-LAST:event_btnPlayFoundActionPerformed

    private void tblMatchesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMatchesMouseClicked
        if (evt.getClickCount() == 2) {
         JTable target = (JTable)evt.getSource();
         int row = target.getSelectedRow();
         int column = target.getSelectedColumn();
         ABCMatch match = (ABCMatch) tuneMatches.elementAt(row);
         JOptionPane.showMessageDialog(this, match.getNotation(), match.getFileName(), JOptionPane.PLAIN_MESSAGE);
         }
    }//GEN-LAST:event_tblMatchesMouseClicked

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
// TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        if ((finder != null) && finder.isRunning())
        {
            finder.setRunning(false);            
        }
        else
        {
            finder = new ABCFinder();
            finder.setSearchString(getTxtABC().getText());
            finder.setStartIn(MattProperties.instance().get("SearchCorpus").toString());
            finder.finda();
        }
    }//GEN-LAST:event_btnFindActionPerformed

    private void btnTranscribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTranscribeActionPerformed
        transcriber.transcribea();        
    }//GEN-LAST:event_btnTranscribeActionPerformed

    private void btnClearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearLogActionPerformed
        txtLog.setText("");
        getTxtABC().setText("");
    }//GEN-LAST:event_btnClearLogActionPerformed

    private void btnChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFileActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new WavFilter());
        System.out.println(transcriber.getInputFile());
        
        fc.setCurrentDirectory(new File("" + MattProperties.instance().get("BatchPath")));
        int returnVal = fc.showOpenDialog(MattGuiNB.this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            clearGraphs();
            transcriber.setInputFile(fc.getSelectedFile().toString());
            transcriber.loadAudio();
        }
    }//GEN-LAST:event_btnChooseFileActionPerformed

    private void btnPlayOriginalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayOriginalActionPerformed
        transcriber.playOriginal();
    }//GEN-LAST:event_btnPlayOriginalActionPerformed

    private void btnPlayTranscriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayTranscriptionActionPerformed
        if (tunePlayer.isPlaying())
        {
            tunePlayer.stopPlaying();
            return;
        }
        
        try 
        {
            FileWriter outFile = new FileWriter("temp.abc");
            PrintWriter out = new PrintWriter(outFile);
            
            out.println("X:1");
            out.println("T:Temp");
            out.println("R:Reel");
            out.println("M:C|");
            out.println("L:1/8");
            out.println("K:D");
            out.println(getTxtABC().getText());
            out.close();
            File abcFile = new File ("temp.abc");
            TuneBook book = new TuneBook(abcFile);
            Tune aTune = book.getTune(1);
            tunePlayer.play(aTune);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }       
    }//GEN-LAST:event_btnPlayTranscriptionActionPerformed

    private void btnQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnQuitActionPerformed

    private void btnAnalysedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalysedActionPerformed
        transcriber.playTranscription();
    }//GEN-LAST:event_btnAnalysedActionPerformed

    private void btnBestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBestActionPerformed
    
        if (best == null)
        {
            return;
        }
         try
         {
             // TuneBook book = new TuneBook(new File(match.getFileName()));
             if (tunePlayer.isPlaying())
             {
                 tunePlayer.stopPlaying();
             }
             else
             {
                    Tune tune = best.getTune();
                     tunePlayer.play(tune);
             }
         }
         catch (Exception e)
         {
             System.out.println("Could not play tune");
             e.printStackTrace();
         }
    }//GEN-LAST:event_btnBestActionPerformed

    private void btnReindexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReindexActionPerformed
        CorpusIndex.instance().reindex();
    }//GEN-LAST:event_btnReindexActionPerformed

    private void LearnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LearnActionPerformed
        try {
            File unOrnamentedFile = new File(Matt.instance().getUnOrnamentedTuneFile());
            File ornamentedFile = new File(Matt.instance().getOrnamentedTuneFile());
            TuneBook unOrnamentedBook = new TuneBook(unOrnamentedFile);
            TuneBook ornamentedBook = new TuneBook(ornamentedFile);
            
            // Get the number of tunes to learn from
            int numTunes = unOrnamentedBook.size();
            
            int[] tuneRefs = unOrnamentedBook.getReferenceNumbers();
            String learned = null;
            for (int i = 0 ; i < numTunes ; i ++) {
                Learner learner = new Learner();
                Tune unOrnamentedTune = unOrnamentedBook.getTune(tuneRefs[i]);
                Tune ornamentedTune = ornamentedBook.getTune(tuneRefs[i]);
                
                MattGuiNB.instance().log("Learning tune: " + unOrnamentedTune.getTitles()[0]);
                
                learner.setUnOrnamented(null);
                learner.setUnOrnamented(unOrnamentedBook.getTuneNotation(tuneRefs[i]));
                learner.setOrnamented(ornamentedBook.getTuneNotation(tuneRefs[i]));
                learner.setTuneName(unOrnamentedTune.getTitles()[0]);
                learner.setMusician(ornamentedTune.getOrigin());
                
                learner.setKey(unOrnamentedTune.getKey().toLitteralNotation());
                learner.setRhythm(unOrnamentedTune.getRhythm());
                learner.learn();
            }
        } catch (Exception e) {
            System.out.println("Could not learn");
            e.printStackTrace();
        }
    }//GEN-LAST:event_LearnActionPerformed

    private void btnBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatchActionPerformed
        if (batchJob!= null && batchJob.isRunning())
        {
            batchJob.setRunning(false);
        }
        else
        {
            batchJob = new BatchJob();
            if (batchJob.chooseFolder())
            {
                batchJob.start();
            }
        }
    }//GEN-LAST:event_btnBatchActionPerformed

    private void btnTranscribeAndFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTranscribeAndFindActionPerformed
        if (batchJob!= null && batchJob.isRunning())
        {
            batchJob.setRunning(false);
        }
        else
        {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new WavFilter());            
            fc.setCurrentDirectory(new File("" + MattProperties.instance().get("BatchPath")));
            int returnVal = fc.showOpenDialog(MattGuiNB.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                batchJob = new BatchJob();
                batchJob.setFolder(fc.getSelectedFile());
                batchJob.start();                
            }            
        }
    }//GEN-LAST:event_btnTranscribeAndFindActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MattGuiNB().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Learn;
    private javax.swing.JButton btnAnalysed;
    private javax.swing.JButton btnBatch;
    private javax.swing.JButton btnBest;
    private javax.swing.JButton btnChooseFile;
    private javax.swing.JButton btnClearLog;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnPlayFound;
    private javax.swing.JButton btnPlayOriginal;
    private javax.swing.JButton btnPlayTranscription;
    private javax.swing.JButton btnQuit;
    private javax.swing.JButton btnReindex;
    private javax.swing.JButton btnTranscribe;
    private javax.swing.JButton btnTranscribeAndFind;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane spLog;
    private javax.swing.JTable tblMatches;
    private javax.swing.JTextArea txtABC;
    private javax.swing.JLabel txtBest;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
    
    private Transcriber transcriber = null;
    private BatchJob batchJob = null;
    private Matt matt;
    private static MattGuiNB _instance;
    
    private Graph frameGraph  = new Graph();
    private JTabbedPane fftTabs = new JTabbedPane();
    private Graph signalGraph  = new Graph();
    private Graph odfGraph = new Graph();
    private Vector tuneMatches = new Vector();
    
    private TunePlayer tunePlayer = new TunePlayer();
    ABCFinder finder = null;
    ABCMatch best = null;
    public JTabbedPane getFftTabs() {
        return fftTabs;
    }
    
    public void enableButtons(boolean enabled)
    {
        /*
         btnPlayTranscription.setEnabled(enabled);
        btnChooseFile.setEnabled(enabled);
        btnPlayOriginal.setEnabled(enabled);
        btnTranscribe.setEnabled(enabled);                
        btnPlayFound.setEnabled(enabled);
        btnFind.setEnabled(enabled);
         */ 
    }
    
    public void clearGraphs()
    {
        frameGraph.clear();
        signalGraph.clear();
        odfGraph.clear();
        clearMatches();
        fftTabs.removeAll();
        txtABC.setText("");
        txtLog.setText("");
        txtBest.setText("");
    }
    
    public void center(JFrame frame) 
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int w = 800;
        int h = 700;
        int x = center.x - w/2, y = center.y - h/2;
        frame.setBounds(x, y, w, h);
        if (w == bounds.width && h == bounds.height)
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.validate();
    }
    
    public Transcriber getTranscriber() {
        return transcriber;
    }

    public void setTranscriber(Transcriber transcriber) {
        this.transcriber = transcriber;
    }
    
    // Variables declaration - do not modify                     
    // End of variables declaration                   

    public Matt getMatt() {
        return matt;
    }

    public void setMatt(Matt matt) {
        this.matt = matt;
    }

    public Graph getFrameGraph() {
        return frameGraph;
    }


    public Graph getSignalGraph() {
        return signalGraph;
    }

    public Graph getOdfGraph() {
        return odfGraph;
    }
    
    public static void log(Object s)
    {
        _instance.txtLog.append(s + System.getProperty("line.separator"));
        _instance.txtLog.setCaretPosition(_instance.txtLog.getText().length());
    }
    
    public static MattGuiNB instance()
    {
        if (_instance == null)
        {
            _instance = new MattGuiNB();
            // CorpusIndex.intstance();
        }
        return _instance;
    }
    
    public void addMatch(ABCMatch match)
    {
        DefaultTableModel model = (DefaultTableModel) tblMatches.getModel();
        Vector row = new Vector();
        row.add(match.getTitle());
        row.add(match.getLine());
        row.add(new Double(match.getEditDistance()));
        model.addRow(row);
        tuneMatches.add(match);
    }
    
    public void clearMatches()
    {
        DefaultTableModel model = (DefaultTableModel) tblMatches.getModel();
        
        model.setRowCount(0);
        tuneMatches.clear();
    }
    

    public javax.swing.JTextArea getTxtABC()
    {
        return txtABC;
    }

    public void setTxtABC(javax.swing.JTextArea txtABC)
    {
        this.txtABC = txtABC;
    }
    
    public synchronized void setBestSoFar(ABCMatch match)
    {
        best = match;
        txtBest.setText("Title: " + match.getTitle() + " ED:" + match.getEditDistance());
    }

    public javax.swing.JProgressBar getProgressBar()
    {
        return progressBar;
    }

    public void setProgressBar(javax.swing.JProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }
}
