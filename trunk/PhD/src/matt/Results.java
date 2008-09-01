/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;
import java.io.*;
import java.text.*;
import java.util.Date;
/**
 *
 * @author Bryan Duggan
 */
public class Results {
    PrintWriter out = null;
    long start;
    

    
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    
    public Results() throws IOException
    {
        
        dateFormat.applyPattern("dd-MM-yyyy HHmmss");            
        start = System.currentTimeMillis();
        Date now = new Date();
        now.setTime(start);
        
        String fileName = MattProperties.instance().getProperty("resultsFolder") + System.getProperty("file.separator") + "results "  + dateFormat.format(now) + ".txt";
        FileWriter outFile = new FileWriter(fileName);
        out = new PrintWriter(outFile);
        out.println("Started at: " + dateFormat.format(now));
    }
    
    public void log(String msg)
    {
        out.println(msg);       
        out.flush();
    }
    
    public void close()
    {
        long end = System.currentTimeMillis();
        Date now = new Date();
        now.setTime(end);
        DecimalFormat nf = new DecimalFormat("0.###");
        out.println("Finished at: " + dateFormat.format(now) + " took: " + nf.format((end - start) / 1000.0f));
        out.close();        
    }
}
