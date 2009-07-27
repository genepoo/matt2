/*
 * Matt.java
 *
 * Created on 24 May 2005, 17:53
 */

package matt;

import java.io.File;
import javax.swing.UIManager;
import abc.notation.Tune;
import abc.parser.TuneBook;
import java.io.IOException;

/**
 *
 * @author bduggan
 */
public class Matt {
    private String unOrnamentedTuneFile;
    private String ornamentedTuneFile;
    private String learnedFile;    
    private Transcriber transcriber;
    private MattGuiNB mattGui;
    
    private static Matt _instance = null;
    
    public static Matt instance()
    {
        
        if (_instance == null)
        {
            _instance = new Matt();
        }
        return _instance;
    }
    
    public static final String PATH = "c:\\Users\\Bryan Duggan\\Documents\\Projects\\PhD\\"; 
    
    /**
     * Creates a new instance of Matt
     */
    public Matt() {
        
        transcriber = new STFTTranscriber();
        mattGui = MattGuiNB.instance();
        mattGui.setTranscriber(transcriber);
    }
    
    public static void main(String[] args) throws Exception 
    {
        
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch(Exception e) 
        {
            System.out.println("Error setting native LAF: " + e);
        }
        
        Matt matt = Matt.instance();
        /*matt.setUnOrnamentedTuneFile(PATH + "abc\\EamonnCotterUn.abc");
        matt.setOrnamentedTuneFile(PATH + "abc\\EamonnCotter.abc");
        matt.setLearnedFile(PATH + "output/casebase.xml");
         */
         String test = "ab[AA]cd[]ef[BBB]g";
         System.out.println(test);
        if (MattProperties.getString("mode").equals("server"))
        {
            MattServer ms = new MattServer();
            ms.start();
        }
        else
        {
            matt.showGui();
        }
    }
    
    public String getUnOrnamentedTuneFile() {
        return unOrnamentedTuneFile;
    }
    
    public void setUnOrnamentedTuneFile(String unOrnamentedTuneFile) {
        this.unOrnamentedTuneFile = unOrnamentedTuneFile;
    }
    
    public String getOrnamentedTuneFile() {
        return ornamentedTuneFile;
    }
    
    public void setOrnamentedTuneFile(String ornamentedTuneFile) {
        this.ornamentedTuneFile = ornamentedTuneFile;
    }
    
    public String getLearnedFile() {
        return learnedFile;
    }
    
    public void setLearnedFile(String learnedFile) {
        this.learnedFile = learnedFile;
    }
    
    public void showGui() throws IOException {
        mattGui.setMatt(this);
        mattGui.setVisible(true);        
        mattGui.setTranscriber(transcriber);
        transcriber.setGui(mattGui);
        
        getTranscriber().setInputFile( PATH + "audio\\test\\D.wav");
        
    }
   
    

    public Transcriber getTranscriber() {
        return transcriber;
    }

    public void setTranscriber(Transcriber transcriber) {
        this.transcriber = transcriber;
    }
}
