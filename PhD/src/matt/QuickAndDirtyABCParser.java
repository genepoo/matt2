package matt;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuickAndDirtyABCParser {
	static ArrayList<CorpusEntry> parse(String filename)
	{
		ArrayList<CorpusEntry> tunes = new ArrayList<CorpusEntry>();
		File file = new File(filename);
		 try
		 {
			 BufferedReader in = new BufferedReader(new FileReader(filename));
			 String strLine;
			 StringBuffer tuneString = new StringBuffer();
			 boolean inTune = false;
			 while ((strLine = in.readLine()) != null)
			 {
				 if (strLine.startsWith("X:") || strLine.startsWith("x:"))
				 {
					 inTune = true;
				 }
				 if (inTune)
				 {
					 tuneString.append(strLine + "\n");
				 }
				 if ("".equals(strLine))
				 {
					 if (inTune)
					 {
						CorpusEntry tune = new CorpusEntry();
						tune.setNotation("" + tuneString);
						
						tune.updateFromNotation();												
						tunes.add(tune);
						
						tuneString.setLength(0);
					 }
					 inTune = false;
				 }
			 }
			 // Catch the last one
			 if (inTune)
			 {
				CorpusEntry tune = new CorpusEntry();
				tune.setNotation("" + tuneString);
				tune.updateFromNotation();
				String tunepalid = filename + tune.getX() + tune.getTitle();    			
				tunes.add(tune);
			 }						
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
		 return tunes;
	}
}
