/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt.web;

import javax.swing.JApplet;

/**
 *
 * @author Bryan Duggan
 */
public class SoundCapture extends JApplet {
    CapturePlayback cpPanel;
    
    
    public SoundCapture()
    {
        cpPanel = new CapturePlayback();
        cpPanel.setApplet(this);
        add(cpPanel);
    }
    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    public void init() {
        // TODO start asynchronous download of heavy resources
    }

    // TODO overwrite start(), stop() and destroy() methods

}
