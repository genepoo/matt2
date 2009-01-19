/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author Bryan Duggan
 */
public interface GUI {
    
    public void clearGraphs();
    public Graph getSignalGraph();
    public Graph getFrameGraph();
    public Graph getOdfGraph();
    public javax.swing.JProgressBar getProgressBar();
   public void  setTitle(String t);
   public void enableButtons(boolean b);
   public void clearFFTGraphs();
   
   public javax.swing.JTextArea getTxtABC();
}
