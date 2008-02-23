/*
 * Learner.java
 *
 * Created on 26 April 2006, 10:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;
import java.util.*;

/**
 *
 * @author bduggan
 */
public class Learner {
    
    public static final int ORNAMENTED = 0;
    public static final int UNORNAMENTED = 1;    
        
    int currentFeature = 0;        
    private String unOrnamented;
    private String ornamented;    
    private ArrayList learned = new ArrayList();    
    StringBuffer notes[] = new StringBuffer[2];
    StringBuffer feature[] = new StringBuffer[2];
    int charPos[] = new int[2];
    int notePos[] = new int[2];
    int length[] = new int[2];
    char c[] = new char[2];
    int artifact;    
    private String rhythm;
    private String tuneName;
    private String musician;
    private String key;
    
    private ArrayList parts;
    
    /** Creates a new instance of Learner */
    public Learner() {
        feature[UNORNAMENTED] = new StringBuffer();
        feature[ORNAMENTED] = new StringBuffer();
        StringBuffer notes[] = new StringBuffer[2];
        StringBuffer feature[] = new StringBuffer[2];
        parts = new ArrayList();
    }

    public String getUnOrnamented() {
        return unOrnamented;
    }

    public void setUnOrnamented(String unOrnamented) {
        this.unOrnamented = unOrnamented;
    }

    public String getOrnamented() {
        return ornamented;
    }

    public void setOrnamented(String ornamented) {
        this.ornamented = ornamented;
    }
       
    private void clearBuffers()
    {
        // Clear the string buffers
        currentFeature ++;
        feature[UNORNAMENTED].delete(0, feature[UNORNAMENTED].length());
        feature[ORNAMENTED].delete(0, feature[ORNAMENTED].length());        
    }
    
    String findPreviousNote(StringBuffer notes, int pos)
    {
        char retValue = 0;
        
        do 
        {
            retValue = notes.charAt(pos);
            
            if (retValue == '!')
            {
                return "z!Breath!";
            }
            if (toDigit(retValue) == -1)
            {
                break;
            }
            pos --;
        }
        while (pos > 0);
        
        return new Character(retValue).toString();
    }
    
    public void learn()
    {
        new Thread() 
        {
            public void run()
            {
                learna();
            }
        }.start();        
    }
    
    public String learna()
    {        
        int rollLength = 0;
        int noteLength = 0;
        int tripletLength = 0;
        boolean inBang = false;
        
        
        // Logger.log("Ornamented: " + getOrnamented());
        // Logger.log("Unornamented: " + getUnOrnamented());
     
        notes[ORNAMENTED] = new StringBuffer(stripWhiteSpace(stripComments(skipHeaders(ornamented))));
        // First check to see if there is a prefix
        int iFirstBar = notes[ORNAMENTED].indexOf("|");
        String prefix = notes[ORNAMENTED].substring(0, iFirstBar);
        if (noteLength(new StringBuffer(prefix)) != 8)
        {
            artifact = Artifact.PREFIX_ARTIFACT;
            feature[ORNAMENTED] = new StringBuffer(prefix);
            createArtifact();
            clearBuffers();
            notes[ORNAMENTED] = new StringBuffer(stripBarDivisions(stripWhiteSpace(notes[ORNAMENTED].substring(iFirstBar))));
        }
        else
        {
            notes[ORNAMENTED] = new StringBuffer(stripBarDivisions(stripWhiteSpace(notes[ORNAMENTED].toString())));
        }
        getParts(skipHeaders(ornamented));
        notes[UNORNAMENTED] = new StringBuffer(stripBarDivisions(stripWhiteSpace(stripComments(skipHeaders(unOrnamented)))));
        
        
        
        boolean finished = false;
        currentFeature = 0;
        artifact = Artifact.NO_ARTIFACT;
        
        length[UNORNAMENTED] = notes[UNORNAMENTED].length();
        length[ORNAMENTED] = notes[ORNAMENTED].length();
        
        while (! finished)
        {
            // Check to see if we are finished
            if (charPos[UNORNAMENTED]  == length[UNORNAMENTED])
            {
                if ((artifact == Artifact.SAME_ARTIFACT) || (artifact == Artifact.NO_ARTIFACT))
                {
                    createArtifact();
                    clearBuffers();
                    artifact = Artifact.POSTFIX_ARTIFACT;
                }
            }
            else 
            {
                c[UNORNAMENTED] = notes[UNORNAMENTED].charAt(charPos[UNORNAMENTED]);
            }
            if (charPos[ORNAMENTED]  == length[ORNAMENTED])
            {
               
                createArtifact();
                clearBuffers();
                finished = true;
                continue;
            }
            else
            {
                c[ORNAMENTED] = notes[ORNAMENTED].charAt(charPos[ORNAMENTED]);
            }
            
            if (artifact == Artifact.POSTFIX_ARTIFACT)
            {
                feature[ORNAMENTED].append(c[ORNAMENTED]);
                charPos[ORNAMENTED] ++;
                continue;
            }
                                                
            if (c[ORNAMENTED] == '~')
            {
                
                createArtifact();
                clearBuffers();
                rollLength = notes[ORNAMENTED].charAt(charPos[ORNAMENTED] + 2) - '0';
                artifact = Artifact.ROLL_ARTIFACT;
                notePos[ORNAMENTED] += rollLength;
                notePos[UNORNAMENTED] += rollLength;
            }
            
            if (artifact == Artifact.ROLL_ARTIFACT)
            {
                if (feature[ORNAMENTED].length() < 3)
                {
                    feature[ORNAMENTED].append(c[ORNAMENTED]);
                    charPos[ORNAMENTED] ++;
                }
                if (noteLength(feature[UNORNAMENTED]) < rollLength)
                {
                    feature[UNORNAMENTED].append(c[UNORNAMENTED]);
                    charPos[UNORNAMENTED] ++;
                }
                else
                {
                                        
                    createArtifact();
                    clearBuffers();
                    artifact = Artifact.NO_ARTIFACT;
                }
                continue;
            }
                                    
            if (c[ORNAMENTED] == '{')
            {
                createArtifact();
                clearBuffers();
                String prev = findPreviousNote(notes[ORNAMENTED], charPos[ORNAMENTED] - 1);
                feature[ORNAMENTED].append(prev);
                feature[ORNAMENTED].append('{');                
                feature[UNORNAMENTED].append(notes[UNORNAMENTED].charAt(charPos[UNORNAMENTED] - 1) );
                artifact = Artifact.ORNAMENT_ARTIFACT;
                charPos[ORNAMENTED] ++;  
                continue;
            }
            if (c[ORNAMENTED] == '}')
            {
                feature[ORNAMENTED].append('}');
                feature[ORNAMENTED].append(notes[ORNAMENTED].charAt(charPos[ORNAMENTED] + 1) );
                feature[UNORNAMENTED].append(notes[UNORNAMENTED].charAt(charPos[UNORNAMENTED]));
                
                notePos[UNORNAMENTED] ++;
                notePos[ORNAMENTED] ++;
                
                // Check for an accidental
                char nxt = notes[ORNAMENTED].charAt(charPos[ORNAMENTED] + 1);
                if (( nxt == '=') || (nxt == '^'))
                {
                    feature[ORNAMENTED].append(notes[ORNAMENTED].charAt(charPos[ORNAMENTED] + 2));
                    charPos[ORNAMENTED] ++;
                }
                //
                createArtifact();    
                clearBuffers();
                
                notePos[UNORNAMENTED] --;
                notePos[ORNAMENTED] --;
                
                
                artifact = Artifact.NO_ARTIFACT;                
                charPos[ORNAMENTED] ++;
                // If the last character was a digit, the next unornamented note should be the note, not the digit
                if (toDigit(c[UNORNAMENTED]) != -1)
                {
                    // Make sure the next time round, we get the note rather than the digit.
                    notes[UNORNAMENTED].replace(
                                charPos[UNORNAMENTED]
                                ,charPos[UNORNAMENTED]+1
                                ,notes[UNORNAMENTED].substring(charPos[UNORNAMENTED] -1, charPos[UNORNAMENTED])
                                );
                    //
                }
                continue;
            }
            if (artifact == Artifact.ORNAMENT_ARTIFACT)
            {
                feature[ORNAMENTED].append(c[ORNAMENTED]);
                charPos[ORNAMENTED] ++;
                continue;
            }
                        
            if (c[ORNAMENTED] == '!')
            {
                if (artifact != Artifact.BREATH_ARTIFACT)
                {
                    if (! feature[ORNAMENTED].toString().endsWith("z"))
                    {
                        createArtifact();
                    }
                    else
                    {
                        notePos[UNORNAMENTED] --;
                        notePos[ORNAMENTED] --;
                    }
                    clearBuffers();
                    artifact = Artifact.BREATH_ARTIFACT;

                    // get the note before the breath
                    feature[ORNAMENTED].append(notes[ORNAMENTED].charAt(charPos[ORNAMENTED] - 2 ));
                    feature[ORNAMENTED].append(notes[ORNAMENTED].charAt(charPos[ORNAMENTED] - 1 ));                    
                    feature[ORNAMENTED].append(c[ORNAMENTED]);
                    charPos[ORNAMENTED] ++;
                    notePos[ORNAMENTED] ++;

                    feature[UNORNAMENTED].append(notes[UNORNAMENTED].charAt(charPos[UNORNAMENTED] - 2 ));
                    feature[UNORNAMENTED].append(notes[UNORNAMENTED].charAt(charPos[UNORNAMENTED] - 1 ));
                    // charPos[UNORNAMENTED] ++;
                    notePos[UNORNAMENTED] ++;
                    continue;
                }
                else
                {
                    // The end of the breath mark
                    feature[ORNAMENTED].append(c[ORNAMENTED]);                    
                    charPos[ORNAMENTED] ++;
                    createArtifact();
                    artifact = Artifact.NO_ARTIFACT;
                    continue;
                }                
            }
            
            if (c[ORNAMENTED] == '^' || c[ORNAMENTED] == '=')
            {
                createArtifact();
                clearBuffers();
                artifact = Artifact.ACCIDENTAL_ARTIFACT;
                // If its an accidental, just write out the note the accidental occured on...
                feature[ORNAMENTED].append(c[ORNAMENTED]);
                charPos[ORNAMENTED] +=1;
                feature[ORNAMENTED].append(notes[ORNAMENTED].charAt(charPos[ORNAMENTED]));
                charPos[ORNAMENTED] +=1;
                notePos[ORNAMENTED] ++;
                feature[UNORNAMENTED].append(c[UNORNAMENTED]);
                charPos[UNORNAMENTED] ++;
                notePos[UNORNAMENTED] ++;
                
                createArtifact();
                artifact = Artifact.NO_ARTIFACT;
                continue;
            }
            
            if (artifact == Artifact.BREATH_ARTIFACT)
            {
                feature[ORNAMENTED].append(c[ORNAMENTED]);
                charPos[ORNAMENTED] ++;
                continue;
            }
            
            if (c[ORNAMENTED] == '(')
            {
                createArtifact();
                clearBuffers();
                
                artifact = Artifact.TRIPLET_ARTIFACT;
                tripletLength = toDigit(notes[ORNAMENTED].charAt(charPos[ORNAMENTED] + 1));
                
                feature[ORNAMENTED].append("(" + tripletLength);
                charPos[ORNAMENTED] += 2;
                feature[ORNAMENTED].append(notes[ORNAMENTED].substring(charPos[ORNAMENTED], charPos[ORNAMENTED] + tripletLength));
                feature[UNORNAMENTED].append(notes[UNORNAMENTED].substring(charPos[UNORNAMENTED], charPos[UNORNAMENTED]  + tripletLength -1));
                
                charPos[ORNAMENTED] += tripletLength;
                charPos[UNORNAMENTED] += tripletLength -1;
                
                notePos[ORNAMENTED] += tripletLength - 1;
                notePos[UNORNAMENTED] += tripletLength - 1;
                
                createArtifact();
                clearBuffers();
                
                artifact  = Artifact.NO_ARTIFACT;
             
                
                continue;
            }            
            // Is it the same in both?
            if ( (c[UNORNAMENTED] == c[ORNAMENTED]) && (notePos[UNORNAMENTED] == notePos[ORNAMENTED]) )
            {
                if (artifact != Artifact.SAME_ARTIFACT) 
                {
                    createArtifact();
                    clearBuffers();
                }
                artifact = Artifact.SAME_ARTIFACT;
                feature[UNORNAMENTED].append(c[UNORNAMENTED]);
                feature[ORNAMENTED].append(c[ORNAMENTED]);
                charPos[UNORNAMENTED] ++;
                charPos[ORNAMENTED] ++;
                notePos[UNORNAMENTED] ++;
                notePos[ORNAMENTED] ++;
                continue;
            }

            if ((artifact != Artifact.VARIATION_ARTIFACT) && (artifact != Artifact.NO_ARTIFACT))
            {
                
                createArtifact();
                artifact = Artifact.NO_ARTIFACT;
                continue;
            }
                        
            artifact = Artifact.VARIATION_ARTIFACT;
            
            feature[ORNAMENTED].append(c[ORNAMENTED]);
            
            if (noteLength > 0)
            {
                noteLength --;
                
            }
            else
            {
                feature[UNORNAMENTED].append(c[UNORNAMENTED]);
                notePos[UNORNAMENTED] ++;
                charPos[UNORNAMENTED] ++;
            }
            
            if (toDigit(c[UNORNAMENTED]) > 2)
            {
                noteLength = toDigit(c[UNORNAMENTED]) - 2;
                notePos[UNORNAMENTED] += noteLength;
            }
                        
            notePos[ORNAMENTED] ++;            
            charPos[ORNAMENTED] ++;
        }
        
        Logger.log("I learned " + Artifact.nextId + " artifacts");
        return learned.toString();        
    } 
    
    private String skipHeaders(String tune)
    {
        int i = 0;
        int inChars = 0;
        boolean inHeader = true;
        
        while ((i < tune.length()) && (inHeader))
        {
            char c = tune.charAt(i); 
            if (inChars == 1)
            {
                if (c == ':')
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
    
    private String stripBarDivisions(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if (c != '|')
            {
                retValue.append(c);
            }
        }
        return retValue.toString();                
    }
    
    private String stripWhiteSpace(String transcription)
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
    
    private String stripComments(String transcription)
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
    
    private int toDigit(char c)
    {
        if ((c >= '0') && (c <= '9'))
        {
            return c - '0';
        }
        else
        {
            return -1;
        }
    }
    
    private int noteLength(StringBuffer notes)
    {
        int stL = notes.length();
        int notL = 0;
        int i = 0;
        boolean inOrnament = false;
        
        while (i < stL)
        {
            char c = notes.charAt(i ++);
            if ((c == '{') || (c == '}'))
            {
                inOrnament = ! inOrnament; 
                continue;
            }
            
            if (inOrnament)
            {
                continue;
            }
            
            if (c == '~')
            {
                continue;
            }
                        
            if (toDigit(c) != -1)
            {
                notL += toDigit(c) -1;
                continue;
            }
            if (c == '|')
            {
                continue;
            }
            notL ++;
        }
        return notL;
    }
    
    public String getTuneName() {
        return tuneName;
    }

    public void setTuneName(String tuneName) {
        this.tuneName = tuneName;
    }

    public String getMusician() {
        return musician;
    }

    public void setMusician(String musician) {
        this.musician = musician;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }    

    public String getRhythm() {
        return rhythm;
    }

    public void setRhythm(String rhythm) {
        this.rhythm = rhythm;
    }
    
    void createArtifact()
    {
        if ((artifact != Artifact.NO_ARTIFACT))
        {
            int featureLength;
            
            if ((feature[ORNAMENTED].length() == 0) && (feature[UNORNAMENTED].length() == 0))
            {
                return;
            }
            
            if (feature[UNORNAMENTED].length() != 0)
            {
                featureLength = noteLength(feature[UNORNAMENTED]);
            }
            else
            {
                featureLength = noteLength(feature[ORNAMENTED]);
            }                     
            
            // Worls ok only for reels
            int realNotePos;
            if (artifact == Artifact.POSTFIX_ARTIFACT)
            {
                realNotePos = notePos[UNORNAMENTED];
            }
            else
            {
                realNotePos = notePos[UNORNAMENTED] - featureLength;
            }
            
            int bar =  (realNotePos / 8) % 8;
            int part = realNotePos / 64;
            String partName;
            if (part >= parts.size())
            {
                partName = "";
            }
            else
            {
                partName = (String) parts.get(part);
            }
                        
            Artifact newArtifact = new matt.Artifact(
                bar, // Bar 
                partName, // Part
                artifact, // TypeID
                featureLength, // Duration
                realNotePos, //Position
                feature[UNORNAMENTED].toString(), // Unornamented
                feature[ORNAMENTED].toString(), // Ornamented
                key, // Key
                tuneName, // Tune name
                musician, // Musician
                rhythm // Rhythm
                );
            if (artifact != Artifact.SAME_ARTIFACT)
            {
                learned.add(newArtifact);
                Logger.log(newArtifact.toString());
            }
        }        
    }       
    
    public void getParts(String tune)
    {
        char c;
        int length = tune.length();
        int i = 0;
        boolean inPart = false;
        StringBuffer part = new StringBuffer();
        
        while (i < length)
        {
            c = tune.charAt(i);
            if (c == '"')
            {
                if (inPart)
                {
                    parts.add(part.substring(0, 2));
                    inPart = false;
                    part.delete(0, part.length());
                }
                else
                {
                    inPart = true;
                }                
            }
            else
            {
                if (inPart)
                {
                    part.append(c);
                }
                
            }
            i ++;
        }                
    }
}