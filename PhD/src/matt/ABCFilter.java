/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.io.*;
import java.util.*;

public class ABCFilter implements FilenameFilter
{
      public boolean accept(File dir, String name) 
      {
            return (name.contains(".abc") || name.contains(".ABC"));
      }        
}
