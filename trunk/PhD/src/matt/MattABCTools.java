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
                int newStart = retValue.lastIndexOf("|:", end);
                if (newStart != -1)
                {
                    start = newStart + 2;
                }
                if ((retValue.length() > end + 2) && Character.isDigit(retValue.charAt(end + 2)))
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
                    start = start + expanded.toString().length();
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
        String test = "";
        
        /*
         test += "Beed e2BA|GBAF GFEF|Dddc d2AG|FAGE FEDF|";        
        test += "Eeed efge|fedf e2BA|GABG FGAF|BGAF GEE2:| ";
        test += "|:gfef gebe|gebe gfec|d2fd Adfd|cdAG FDEF|";
        test += "GABG FGAF|E2ef gfed|BcdB A2FA|BGAF GEE2:|";
        test += "|:Beed e2BA|GBAF GFEF|Dddc d2AG|FAGE FEDF|";
        test += "Eeed efge|fedf e2BA|GABG FGAF|BGAF GEE2:|";
        test += "|:gfef gebe|gebe gfec|d2fd Adfd|cdAG FDEF|";
        test += "GABG FGAF|E2ef gfed|BcdB A2FA|BGAF GEE2:|";
        */
        /*test += "~G3A Bcdg|eB~B2 egdB|GFGA BcdB|AcBA GEDE|";
        test += "GFGA Bcdg|eB~B2 d2ef|g2af gedB|1 AcBA GEDE:|2 AcBA GED2||";        
        test += "|:gfga gedg|eB~B2 d2ef|g2af gedB|AcBA GED2|";
        test += "~g3a gedg|eB~B2 d2ef|gbaf gedB|1 AcBA GED2:|2 AcBA GEDE||";
        
        test += "~G3A Bd~d2|eB~B2 eBdB|~G3A B2dB|AcBA GEDE|";
        test += "~G3A ~B3d|eB~B2 d2ef|gbaf gedB|1 AcBA GEDE:|2 AcBA GED2||";
        test += "|:~g3a gfed|eB~B2 d2ef|gfga gedB|AcBA GED2|";
        test += "gfga gfed|eB~B2 d2ef|g2af gedB|1 AcBA GED2:|2 AcBA GEDE||";
        */
         test += "A2FA df~f2|dfef dB~B2|A2FA dffe|dBAG FDDB|";
        test += "A2FA df~f2|afef dB~B2|A2FA dffe|dBAG FDD2||";
        test += "|:a2~a2 afdf|afef dB~B2|fbba bafa|bfaf feef|";
        test += "bf~f2 af~f2|afef dB~B2|A2FA dffe|1 dBAF ADD2:|2 dBAF ADDB||";
        test += "A2FA dfef|df (3efe dB~B2|A2FA defe|dBAG FD~D2|";
        test += "A2FA df~f2|afgf efdB|(3ABA FA defe|dBAG FD~D2||";
        test += "|:~a3z afdf|afef dB~B2|fbba babc'|d'c'ba feef|";
        test += "bf~f2 af~f2|afef efdB|(3ABA FA defe|1 dBAF ADD2:|2 dBAF ADD2||";
        
        test = MattABCTools.stripComments(test);
        test = MattABCTools.stripWhiteSpace(test);
        test = MattABCTools.expandLongNotes(test);
        test = MattABCTools.expandParts(test);
        test = MattABCTools.stripBarDivisions(test);
        test = MattABCTools.removeTripletMarks(test);        
        test = test.toUpperCase();

        System.out.println(test);
    }
}
