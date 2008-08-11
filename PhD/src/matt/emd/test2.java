package matt.emd;

/*
 * Test.java
 *
 * Created on 6. Mai 2004, 18:24
 */


import java.util.*;
import java.sql.*;

/**
 *
 * @author  schulte-coerne
 */
public class test2 {
    
    /** Creates a new instance of Test */
    public test2() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        EMD emd = new EMD(new RusselInitializer(), EMD.DL_NONE);
	
	float[][] cost = {{0, 1, 1, 1}, {1, 0, 1, 1}, {1, 1, 0, 0.5f}, {1, 1, 0.5f, 0}};
	
        Signatures sig = new Signatures(cost);
        
	int[] hq = {9, 10, 10, 35};
	int[] hp1 = {24, 0, 40, 0};
	int[] hp2 = {0, 8, 0, 56};
	
        sig.setQWeights(hp2);
        sig.setPWeights(hq);
            
        double e = emd.calculate(sig, (byte)1);
        
        System.out.println(e);

    }
    
}
