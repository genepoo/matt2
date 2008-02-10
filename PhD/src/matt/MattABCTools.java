/*
 * ABCTools.java
 *
 * Created on 17 July 2007, 16:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

import java.util.StringTokenizer;

/**
 *
 * @author Bryan
 */
public class MattABCTools {
    
    enum partTypes {NORMAL, REPEAT, SPECIAL12};
    
    /** Creates a new instance of ABCTools */
    public MattABCTools() {
    }
    
    public static String skipHeaders(String tune)
    {
        int i = 0;
        int inChars = 0;
        boolean inHeader = true;
        
        while ((i < tune.length()) && (inHeader))
        {
            char c = tune.charAt(i); 
            if (inChars == 1)
            {
                if ((c == ':') && (tune.charAt(i-1) != '|'))
                {
                    inHeader = true;
                }
                else 
                {
                    inHeader = false;
                    i -=2;
                }
            }
            if ((c == '\r') || (c == '\n'))
            {                
                inChars = -1;
            }
            i ++;
            inChars ++;
        }
        return tune.substring(i);
    }
    
    public static String expandParts(String notes)
    {
        StringBuffer retValue = new StringBuffer(notes);
        int start = 0;
        int end = 0;
        String endToken = ":|";
        while (true)
        {
            end = retValue.indexOf(endToken);

            if ((end == -1))
            {
                break;
            }
            else
            {
                start = retValue.lastIndexOf("|:", end);
                if (start == -1)
                {
                    start = 0;
                }
                else
                {
                    start += 2;
                }
                if ((notes.length() > end + 2) && Character.isDigit(notes.charAt(end + 2)))
                {
                    StringBuffer expanded = new StringBuffer();
                    int normalPart = retValue.lastIndexOf("|", end);
                    expanded.append(retValue.substring(start, normalPart));
                    expanded.append("|");
                    expanded.append(retValue.substring(normalPart + 2, end));
                    int secondTime = retValue.indexOf("|", end + 2);
                    expanded.append("|");
                    expanded.append(retValue.substring(start, normalPart));
                    expanded.append("|");
                    expanded.append(retValue.substring(end + 3, secondTime));
                    expanded.append("|");
                    retValue.replace(start, secondTime, expanded.toString());
                }
                else
                {
                    StringBuffer expanded = new StringBuffer();
                    expanded.append(retValue.substring(start, end));
                    expanded.append("|");
                    expanded.append(retValue.substring(start, end));
                    retValue.replace(start, end + 2, expanded.toString());
                }
            }
        }
        return retValue.toString();
    }

    
    public static String stripBarDivisions(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if ((c != '|') && (c != ':'))
            {
                retValue.append(c);
            }
        }
        return retValue.toString();                
    }
    
    public static String removeTripletMarks(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        // First remove ornaments
        boolean inOrnament = false;
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if ((c == '(') && Character.isDigit(notes.charAt(i+1)))
            {
                i +=1;
                continue;
            }
            retValue.append(c);
        }
        return retValue.toString();
    }
    
    public static String expandLongNotes(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        // First remove ornaments
        boolean inOrnament = false;
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if (c == '{')
            {
                inOrnament = true;
                continue;
            }
            if (c == '}')
            {
                inOrnament = false;
                continue;
            }
                         
            if ((c != '~') && ! inOrnament && (c != ',') && (c != '=') && (c != '^') && (c != '\''))
            {
                retValue.append(c);
            }            
        }
        for (int i = 1 ;  i < retValue.length(); i ++)
        {
            char c  = retValue.charAt(i);
            char p = retValue.charAt(i -1);
            // Its a long note
            if (Character.isDigit(c) && Character.isLetter(p))
            {
                String expanded = "";
                int howMany = c - '0';
                for (int j = 0 ; j < howMany; j ++)
                {
                    expanded += p;
                }
                retValue.replace(i - 1, i + 1, expanded);
            }
            // Expand Z's
            /*
             if (c == 'z')
            {
                retValue.replace(i, i, "" + p);
            }
             */
        }
        return retValue.toString();
    }
    
    public static String stripNonNotes(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        notes = stripComments(notes);
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
                
            if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || ((c >= '1') && (c <= '9')) || (c == '('))
            {
                retValue.append(c);
            }
        }
        return retValue.toString();                
    }
    
    public static String stripWhiteSpace(String transcription)
    {
        StringBuffer retValue = new StringBuffer();        
        int i = 0;
        while (i < transcription.length())
        {            
            if ((transcription.charAt(i) != ' ') &&  (transcription.charAt(i) != '\r') && (transcription.charAt(i) != '\n'))
            {
                retValue.append(transcription.charAt(i));
            }
            i ++;
        }
            
        return retValue.toString();        
    }
    
    public static String stripComments(String transcription)
    {
        StringBuffer retValue = new StringBuffer();
        
        int i = 0;
        boolean inComment = false;
        while (i < transcription.length())
        {
            
            if (transcription.charAt(i) == '"')
            {
                if (inComment)
                {
                    inComment = false;
                    i ++;
                    continue;
                }
                else
                {
                    inComment = true;
                }
            }
            if (!inComment)
            {
                retValue.append(transcription.charAt(i));
            }
            i ++;
        }
        return retValue.toString();
    }
    
    public static void main(String[] args)
    {
        // String test = "eAAG AGEF|G2AF GED2|cBcd eged|cdef ged2|eAAG AGEF|G2AF GED2|cBcd eged|cABG A2Bd:||:eaab agef|g2af gedB|cBcd (3efg ed|cdef ged2|eaab agef|g2af gedB|cBcd eged|cABG A2Bd:|";
        String test = "D(3B,DE ~G3A|B2dB ABGA|BAGE DB,DE|G2BG ABGE|DB,DE ~G3A|B2dB ABGA|BAGE G2BG|1 ABGE ~G3E:|2 ABGE ~G3A|||:BABd edeg|a2ge g2eg|a2ge ~g3e|dedB A2GA|BABd edeg|bage g2eg|a2ge dBGB|ABGE ~G3A:||:BG~G2 DGBd|eg~g2 egdc|BG~G2 DGBG|AGAB A2GA|BG~G2 DGBd|eg~g2 dega|bgag edBG|1 ABGE ~G3A:|2 ABGE ~G3E||";        
        test = stripComments(test);
        test = stripWhiteSpace(test);
        test = removeTripletMarks(test);
        
        System.out.println(test);
        System.out.println("Results: " + expandParts(test));        
    }
}
