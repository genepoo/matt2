package matt;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class UnZipper
{
    private static String path;
    
    public static final void copyInputStream(InputStream in, OutputStream out)
            throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
        {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    public static final void unzip(String file)
    {
        Enumeration entries;
        ZipFile zipFile;
        String filePrefix;
        int s = file.lastIndexOf(System.getProperty("file.separator"));
        if (s == -1)
        {
            s = 0;
        }
        filePrefix = file.substring(s + 1 , file.length() - 4);
        path = file.substring(0, s) + System.getProperty("file.separator") + filePrefix;
        new File(path).mkdir();
        
        try
        {
            zipFile = new ZipFile(file);

            entries = zipFile.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory())
                {
                    // Assume directories are stored parents first then children.
                    System.err.println("Extracting directory: " + entry.getName());
                    // This is not robust, just for demonstration purposes.
                    (new File(entry.getName())).mkdir();
                    continue;
                }

                System.err.println("Extracting file: " + entry.getName());
                String newFileName = path + System.getProperty("file.separator") + entry.getName();
                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(newFileName)));
            }

            zipFile.close();
        } catch (IOException ioe)
        {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }

    public static String getPath()
    {
        return path;
    }
}
