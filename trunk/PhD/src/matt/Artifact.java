/*
 * Artifact.java
 *
 * Created on 15 August 2006, 11:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author bduggan
 */
public class Artifact {
    
    public static final int NO_ARTIFACT = 0;
    public static final int SAME_ARTIFACT = 1;
    public static final int ROLL_ARTIFACT = 2;
    public static final int CUT_ARTIFACT = 3;
    public static final int CRAN_ARTIFACT = 4;
    public static final int TRILL_ARTIFACT = 5;
    public static final int VARIATION_ARTIFACT = 6;
    public static final int ORNAMENT_ARTIFACT = 7;
    public static final int BREATH_ARTIFACT = 8;
    public static final int ACCIDENTAL_ARTIFACT = 9;
    public static final int TRIPLET_ARTIFACT = 10;
    public static final int POSTFIX_ARTIFACT = 11;
    public static final int PREFIX_ARTIFACT = 12;

    
    private int id;
    private int bar;
    private int position;
    private String part;
    private int typeID;
    private String type;
    private String unornamentedNGram;
    private String ornamentedNGram;
    private int duration;
    private String rhythm;
    private String tuneName;
    private String musician;
    private String key;
    
    public static final String[] ARTIFACTS = {"Nothing", "Same", "Roll", "Cut", "Cran", "Trill", "Variation", "Ornament", "Breath", "Accidental", "Triplet", "Postfix", "Prefix"};
    
    public static int nextId = 0;
        
    /**
     * Creates a new instance of Artifact
     */
    public Artifact() {
    }
    
    public Artifact(int bar, String part, int typeID, int duration, int position, String uNGram, String oNGram, String key, String tuneName, String musician, String rhythm)
    {
        this.id = nextId ++;
        this.bar = bar;
        this.part = part;
        this.typeID = typeID;
        this.duration = duration;
        this.position = position;
        this.unornamentedNGram = uNGram;
        this.ornamentedNGram = oNGram;
        this.key = key;
        this.tuneName = tuneName;        
        this.musician = musician;
        this.rhythm = rhythm;                
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getType() {
        return ARTIFACTS[typeID];
    }

    public String getUnornamentedNGram() {
        return unornamentedNGram;
    }

    public void setUnornamentedNGram(String unornamentedNGram) {
        this.unornamentedNGram = unornamentedNGram;
    }

    public String getOrnamentedNGram() {
        return ornamentedNGram;
    }

    public void setOrnamentedNGram(String ornamentedNGram) {
        this.ornamentedNGram = ornamentedNGram;
    }
    
    public void writeTag(String tagName, String tagValue, StringBuffer to)
    {
        to.append("\t<" + tagName + ">" + tagValue + "</" + tagName + ">\n");
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

    

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
      
        public String toString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("<case name=\"" + id + "\">\n");
            writeTag("tuneName", tuneName, sb);
            writeTag("musician", musician, sb);
            writeTag("key", key, sb);
            writeTag("rhythm", rhythm, sb);
            writeTag("artifact", getType(), sb);
            writeTag("part", part, sb);
            writeTag("bar", new Integer(bar).toString(), sb);
            writeTag("position", new Integer(position).toString(), sb);
            writeTag("duration", new Integer(duration).toString(), sb);
            writeTag("unornamented", unornamentedNGram, sb);
            writeTag("ornamented", ornamentedNGram, sb);
            sb.append("</case>\n\n");
            return sb.toString();        
    }

}
