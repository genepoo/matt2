/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

/**
 *
 * @author Bryan Duggan
 */
public class Logger {
    public static void log(Object msg)
    {
        if (MattProperties.getBoolean("applet"))
        {
            String smsg = "" + msg;
            if (smsg.length() > 25)
            {
                smsg = smsg.substring(0, 25) + "...";
            }
            matt.web.MattApplet.setStatus(smsg);
            System.out.println(msg);
            return;
        }
        
        if (MattProperties.getString("mode").equals("client"))
        {
            System.out.println(msg);
            MattGuiNB.log(msg);
        }
        else
        {
            System.out.println(msg);
        }
    }
}
