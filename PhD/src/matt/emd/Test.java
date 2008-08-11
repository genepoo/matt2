package matt.emd;

/*
 * Test.java
 *
 * Created on 6. Mai 2004, 18:24
 */


import javax.vecmath.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author  schulte-coerne
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    
    private static class Feature extends Vector3f {
        Feature() {
            super();
        }
        
        Feature(float x, float y, float z) {
            super(x, y, z);
        }
    }
    
    private static class Euclid implements GroundDistance {
        public float calculate(Object pFeature, Object qFeature) {
            Feature diff = new Feature();
            diff.sub((Feature) pFeature, (Feature) qFeature);
            return diff.length();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        Class.forName("org.gjt.mm.mysql.Driver");

        Connection con = DriverManager.getConnection("jdbc:mysql://fidschi/BilderHistogrammeRGB", "bilder", "blubb");
        
        Statement stmt = con.createStatement();
        
        ResultSet res = stmt.executeQuery("SELECT h1.ID AS ID1, h1.N AS N1, h1.Histogramm AS Histogramm1, h2.ID AS ID2, h2.N AS N2, h2.Histogramm AS Histogramm2, NOT(ISNULL(r.HistogrammID1)) AS ResultatExistiert FROM histogramme h1, histogramme h2 LEFT JOIN resultate r ON r.HistogrammID1 = h1.ID AND r.HistogrammID2 = h2.ID WHERE h1.ID < h2.ID ORDER BY h1.ID, h2.ID");
        
        System.out.print("Press Return to start");
        System.in.read();
        System.out.println("Starting...");
        
        java.util.Date start = new java.util.Date();
        
        EMD emd = new EMD(new RusselInitializer(), EMD.DL_NONE);
//        EMD emd = new EMD(new NorthWestInitializer(), EMD.DL_NONE);
        
        
        int maxIter = 0;
        int iterCountAlt = 0;
        
        Feature[] f1 = new Feature[(1 << 6)];
        Feature[] f2 = new Feature[(1 << 6)];
        for (int i = 0; i < (1 << 6); i++) {
            Feature f = new Feature(((i & 0x30) << 2) | (1 << 5), ((i & 0xC) << 4) | (1 << 5), ((i & 0x3) << 6) | (1 << 5));
            f1[i] = f;
            f2[i] = f;
        }
        Signatures sig = new Signatures(f1, f2, new Euclid());
        
        int count = 0; double e = 0;

        while (res.next()) {
            count++;
            iterCountAlt = emd.OverallIterCount;
            String hist1 = res.getString("Histogramm1");
            String hist2 = res.getString("Histogramm2");
            int n1 = res.getInt("N1");
            int n2 = res.getInt("N2");
            
            for (int i = 0; i < (1 << 6); i++) {
                sig.setQWeight(i + 1, hist1.charAt(i) * n2);
                sig.setPWeight(i + 1, hist2.charAt(i) * n1);
            }
            
//            System.out.print(String.valueOf(count) + ": " + res.getString("ID1") + ", " + res.getString("ID2") + ": ");
            e = emd.calculate(sig, (byte)1);
//            System.out.println(String.valueOf(e) + "[" + String.valueOf(emd.OverallIterCount - iterCountAlt) + " Iterations]");
            
            String sqlval = "e1 = " + String.valueOf(e) + ", Iter1 = " + String.valueOf(emd.OverallIterCount - iterCountAlt);
            
            if (res.getBoolean("ResultatExistiert"))
                stmt.executeUpdate("UPDATE resultate SET " + sqlval + " WHERE HistogrammID1 = " + res.getString("ID1") + " AND HistogrammID2 = " + res.getString("ID2"));
            else
                stmt.executeUpdate("INSERT INTO resultate SET HistogrammID1 = " + res.getString("ID1") + ", HistogrammID2 = " + res.getString("ID2") + ", " + sqlval);

            maxIter = Math.max(maxIter, emd.OverallIterCount - iterCountAlt);
        }
    
        java.util.Date end = new java.util.Date();
        
        System.out.println(String.valueOf(end.getTime() - start.getTime()) + "ms (" + String.valueOf(emd.OverallIterCount) + " not optimal Solutions [Max: " + String.valueOf(maxIter) + "])");

    }
    
}
