/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt.web;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.sound.sampled.*;
import java.util.zip.*;

/**
 *
 * @author Bryan Duggan
 */
public class Upload {

    public int submit(AudioInputStream is, AudioFileFormat.Type fileType)
            throws Exception
    {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String exsistingFileName = "test.wav";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String urlString = "http://localhost/tunometer/submit.php";

        try
        {
            Properties systemProperties = System.getProperties();
            systemProperties.setProperty("http.proxyHost","localhost");
            systemProperties.setProperty("http.proxyPort","51966");
            
            //URL url = new URL("http", "localhost", 51966, urlString);            
            URL url = new URL(urlString);            
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"wav\";" + " filename=\"" + exsistingFileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // Send upa  zip file
            ZipOutputStream out = new ZipOutputStream(dos);
            out.setLevel(9);
            out.putNextEntry(new ZipEntry(exsistingFileName));            
            bytesAvailable = is.available();
            AudioSystem.write(is, fileType, out);

            /*
             (if (AudioSystem.write(is, fileType, dos) == -1) {
                throw new IOException("Problems writing to file");                
            }
             */ 
            /*
             // read file and write it into form...
            bytesRead = is.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = is.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = is.read(buffer, 0, bufferSize);
            }
             */
            out.flush();
            out.finish();
                    
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            is.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex)
        {
            System.out.println("From ServletCom CLIENT REQUEST:" + ex);
        } catch (IOException ioe)
        {
            System.out.println("From ServletCom CLIENT REQUEST:" + ioe);
        }
        //------------------ read the SERVER RESPONSE
        StringBuffer response = new StringBuffer();
        try
        {
            inStream = new DataInputStream(conn.getInputStream());
            String str;
            
            while ((str = inStream.readLine()) != null)
            {
                response.append(str);
            }
            inStream.close();
            System.out.println("Response: " + response);
        } catch (IOException ioex)
        {
            System.out.println("From (ServerResponse): " + ioex);

        }
        return Integer.parseInt("" + response.substring(response.indexOf(":") + 1));
    }
}
