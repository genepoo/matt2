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
        if (MattProperties.getString("mode").equals("client"))
        {
            MattGuiNB.log(msg);
        }
        else
        {
            System.out.println(msg);
        }
    }
}
