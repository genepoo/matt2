/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matt;

import java.util.Collection;
import java.util.Arrays;
/**
 *
 * @author Bryan Duggan
 */
public class EditDistance {
  
     private static int Minimum (int a, int b, int c) {
  int mi;

    mi = a;
    if (b < mi) {
      mi = b;
    }
    if (c < mi) {
      mi = c;
    }
    return mi;

  }
    
  public static float LD (String s, String t) {
  int d[][]; // matrix
  int n; // length of s
  int m; // length of t
  int i; // iterates through s
  int j; // iterates through t
  char s_i; // ith character of s
  char t_j; // jth character of t
  int cost; // cost

    // Step 1

    n = s.length ();
    m = t.length ();
    if (n == 0) {
      return m;
    }
    if (m == 0) {
      return n;
    }
    d = new int[n+1][m+1];

    // Step 2

    for (i = 0; i <= n; i++) {
      d[i][0] = i;
    }

    for (j = 0; j <= m; j++) {
      d[0][j] = j;
    }

    // Step 3

    for (i = 1; i <= n; i++) {

      s_i = s.charAt (i - 1);

      // Step 4

      for (j = 1; j <= m; j++) {

        t_j = t.charAt (j - 1);

        // Step 5

        if (s_i == t_j) {
          cost = 0;
        }
        else {
          cost = 1;
        }

        // Step 6

        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

      }

    }
    // Step 7
    /*
     float max;
    float min;
    float ed;
    if (s.length() > t.length())
    {
        max = s.length();
        min = t.length();
    }
    else
    {
        max = t.length();
        min = s.length();
    }
     */
    float ed = d[n][m];
    return ed;

  }
  
  public static int getLevenshteinDistance (String s, String t) {
  if (s == null || t == null) {
    throw new IllegalArgumentException("Strings must not be null");
  }
		
  /*
    The difference between this impl. and the previous is that, rather 
     than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
     we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
     is the 'current working' distance array that maintains the newest distance cost
     counts as we iterate through the characters of String s.  Each time we increment
     the index of String t we are comparing, d is copied to p, the second int[].  Doing so
     allows us to retain the previous cost counts as required by the algorithm (taking 
     the minimum of the cost count to the left, up one, and diagonally up and to the left
     of the current cost count being calculated).  (Note that the arrays aren't really 
     copied anymore, just switched...this is clearly much better than cloning an array 
     or doing a System.arraycopy() each time  through the outer loop.)

     Effectively, the difference between the two implementations is this one does not 
     cause an out of memory condition when calculating the LD over two very large strings.  		
  */		
		
  int n = s.length(); // length of s
  int m = t.length(); // length of t
		
  if (n == 0) {
    return m;
  } else if (m == 0) {
    return n;
  }

  int p[] = new int[n+1]; //'previous' cost array, horizontally
  int d[] = new int[n+1]; // cost array, horizontally
  int _d[]; //placeholder to assist in swapping p and d

  // indexes into strings s and t
  int i; // iterates through s
  int j; // iterates through t

  char t_j; // jth character of t

  int cost; // cost

  for (i = 0; i<=n; i++) {
     p[i] = i;
  }
		
  for (j = 1; j<=m; j++) {
     t_j = t.charAt(j-1);
     d[0] = j;
		
     for (i=1; i<=n; i++) {
        cost = s.charAt(i-1)==t_j ? 0 : 1;
        // minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
        d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
     }

     // copy current distance counts to 'previous row' distance counts
     _d = p;
     p = d;
     d = _d;
  } 
		
  // our last action in the above loop was to switch d and p, so p now 
  // actually has the most recent cost counts
  return p[n];
}
    
public static float editDistance(String feature1, String feature2)
    {
        int length_1 = feature1.toString().length();
        int length_2 = feature2.toString().length();
        int difference = 0;

        char sc;

        if (length_1 == 0)
        {
            return length_1;
        }
        if (length_2 == 0)
        {
            return length_2;
        }

        int[][] d = new int[length_1 + 1][length_2 + 1];

        for (int i = 1; i <= length_1; i++)
        {
            sc = feature1.toString().charAt(i - 1);
            for (int j = 1; j <= length_2; j++)
            {
                int v = d[i - 1][j - 1];
                if (feature2.toString().charAt(j - 1) != sc)
                {
                    difference = 1;
                } else
                {
                    difference = 0;
                }
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), v + difference);
            }
        }

        float ed = d[length_1][length_2];
        return ed;
    }
    
    public static void main(String[] args)
    {
        
       String toFind, searchIn;
       
       toFind = "Brian";
       searchIn = "Bryan Duggan more text";
       
       
       System.out.println("editDistance: " + editDistance(toFind, searchIn));
       System.out.println("LD: " + LD(toFind, searchIn));
       System.out.println("getLevenshteinDistance: " + getLevenshteinDistance(toFind, searchIn));
       
       float ed = getLevenshteinDistance(toFind, searchIn);
       ed = 1.0f - ((searchIn.length() - ed) / toFind.length()); 
       
       System.out.println("Normalised ed " + ed);
    }
    
}
