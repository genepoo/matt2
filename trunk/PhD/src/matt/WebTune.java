/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matt;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.commons.lang.StringEscapeUtils;


public class WebTune implements Comparable, Serializable
{

    public int x;
    public String title;
    public String alt_title;
    public String source;
    public int sourceid;
    public String tunepalid;
    public String key_sig;
    public String type;
    public String notation;
    public String line;
    public String midiFileName;
    public int id;
    public float normalEd;
    public String searchKey;
    public Vector<String> keys;
    public float ed;

    public WebTune(ResultSet r) throws SQLException
    {
        searchKey = r.getString("search_key");
        title = r.getString("title");
        alt_title = r.getString("alt_title");
        id = r.getInt("id");
        type = r.getString("tune_type");
        notation = r.getString("notation");
        tunepalid = r.getString("tunepalid");
        x = r.getInt("x");
        midiFileName = r.getString("midi_file_name");
        key_sig = r.getString("key_sig");
        type = r.getString("tune_type");
        source = r.getString("sourcename");
        sourceid = r.getInt("sourceid");
    }

    public int compareTo(Object o1)
    {
        WebTune match0 = (WebTune) this;
        WebTune match1 = (WebTune) o1;

        if (match0.ed < match1.ed)
        {
            return -1;
        }
        if (match0.ed == match1.ed)
        {
            return 0;
        }
        return 1;
    }

    public String writeTag(int tabs, String tag, String value)
    {
        String val, ret = "";
        if (value == null)
        {
            val = "";
        }
        else
        {
            val = value;
        }
        for (int i = 0 ; i < tabs ; i ++)
        {
            ret += "\t";
        }
        return ret + "<" + tag +">" + StringEscapeUtils.escapeXml(val) + "</" + tag +">\n";
    }
    public String toXML()
    {
        StringBuffer xml = new StringBuffer();

        xml.append("<?xml version=\"1.0\"?>\n");
        xml.append("<tune>\n");
        xml.append(writeTag(1, "title", title));        
        xml.append(writeTag(1, "alt_title", alt_title));
        xml.append(writeTag(1, "x", "" + x));
        xml.append(writeTag(1, "id", "" + id));
        xml.append(writeTag(1, "tunepalid", tunepalid));
        xml.append(writeTag(1, "source", source));
        xml.append(writeTag(1, "sourceid", "" + sourceid));
        xml.append(writeTag(1, "type", type));
        xml.append(writeTag(1, "normalEd", "" + normalEd));
        xml.append("\t<search_keys>\n");
        for (int i = 0 ; i < keys.size() ; i ++)
        {
            xml.append(writeTag(2, "key", keys.get(i)));
        }
        xml.append("\t</search_keys>\n");
        xml.append(writeTag(1, "midi_file_name", midiFileName));
        xml.append(writeTag(1, "notation", notation.trim()));        
        xml.append("</tune>");
        return xml.toString();
    }
}
