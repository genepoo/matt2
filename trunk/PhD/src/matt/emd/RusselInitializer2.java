package matt.emd;


/*
 * RusselInitialisator.java
 *
 * Created on 6. Mai 2004, 14:57
 */


/**
 * This is the implementation of russel's approximation method for streamlined simplex tableaus to solve the transportation problem.<br>
 * It initializes the basic variables (isBasic(i, j) + x<sub>ij</sub>) <b>and</b> the u<sub>i</sub> and v<sub>j</sub> values.<br>
 * <b>Note:</b><br>
 * We use the u<sub>i</sub> and v<sub>j</sub> variables of the tableau to store the \overline(u<sub>i</sub>) and \overline(v<sub>j</sub>) values temporary.<br>
 * Float.isNaN(u<sub>i</sub>) (or v<sub>j</sub>) ['<i>== Float.NaN</i>' is not working!!] means, that the row (or column) is completly computed and should not be considered in the further computations
 * @author Till Schulte-Coerne
 */
public final class RusselInitializer2 implements Initializer {

    /** No debug messages */    
    public static final int DL_NONE = 0;
    
    /** Only some debug messages */    
    public static final int DL_MEDIUM = 5;
    
    /** All debug messages availiable */    
    public static final int DL_ALL = 10;

    /** The debug level to use */    
    private int mDebugLevel = 0;

    /**
     * Implements a compareable class, that is able to handle c - u - v expressions regarding to infinite values
     */    
    private final class Delta implements Comparable {
        /** The count of Float.POSITIVE_INFINITY in c - u - v */        
        private int infCount = 0;
        
        /** The not Float.POSITIVE_INFINITY part of c - u - v */        
        private double realPart = 0;
        
        /**
         * Set the value of Delta = c - u - v
         * @param c c from the expression c - u - v
         * @param u u from the expression c - u - v
         * @param v v from the expression c - u - v
         */        
        public void Set(float c, float u, float v) {
            infCount = 0; realPart = 0;
            if (Float.isInfinite(c))
                infCount++;
            else
                realPart += c;
            if (Float.isInfinite(u))
                infCount--;
            else
                realPart -= u;
            if (Float.isInfinite(v))
                infCount--;
            else
                realPart -= v;
        }
        
        /**
         * Compares this instance of Delta to another instace of Delta
         * @param o The other instance of Delta
         * @return <ul>
         * <li>-1 if this &lt; o
         * <li>0 if this == o
         * <li>+1 if this &gt; o
         */        
        public int compareTo(Object o) {
            if (!(o instanceof Delta))
                throw new ClassCastException();
            Delta delta2 = (Delta) o;
            if (infCount < delta2.infCount)
                return -1;
            else if (infCount > delta2.infCount)
                return 1;
            else if (realPart < delta2.realPart)
                return -1;
            else if (realPart > delta2.realPart)
                return 1;
            else 
                return 0;
        }
    }
    
    /** The internal delta matrix (delta<sub>ij</sub> = c<sub>ij</sub> - \overline(u)<sub>i</sub> - \overline(v)<sub>j</sub>) */    
    private Delta[][] mDeltaMatrix;
    
    /** The tableau to initialize */    
    private Tableau mTableau; 
    
    private int mRowsConsidered, mColsConsidered;
 
    /**
     * Sets the Cell i,j as basic, calculates it's x<sub>ij</sub> and removes the emptied row or column from the further considerations
     * @param i The row index i
     * @param j The column index j
     */    
    private void addBasicVariable(int i, int j) {
        TableauCell cell = mTableau.getCell(i, j);
        TableauRowHeader rowHeader = mTableau.getRowHeader(i);
        TableauColHeader colHeader = mTableau.getColHeader(j);
        cell.setBasic(true);
        int flow = Math.min(rowHeader.getSupply() - rowHeader.getSupplyUsed(), colHeader.getDemand() - colHeader.getDemandFilled());
        rowHeader.setSupplyUsed(rowHeader.getSupplyUsed() + flow);
        colHeader.setDemandFilled(colHeader.getDemandFilled() + flow);
        cell.setX(flow);
        if (mDebugLevel >= DL_MEDIUM)
            System.out.print("Basic Variable added: " + String.valueOf(i) +  ", " + String.valueOf(j) + ": " + String.valueOf(flow) + "; ");
        if (rowHeader.getSupplyUsed() == rowHeader.getSupply() && (mRowsConsidered > 1 || colHeader.getDemandFilled() != colHeader.getDemand())) {
            removeRowFromConsideration(i);
            if (mDebugLevel >= DL_MEDIUM)
                System.out.print("Row " + String.valueOf(i) + " removed; ");
        }    
        else {
            removeColFromConsideration(j);
            if (mDebugLevel >= DL_MEDIUM)
                System.out.print("Col " + String.valueOf(j) + " removed; ");
        }    
        if (mDebugLevel >= DL_MEDIUM)
            System.out.println(String.valueOf(mRowsConsidered) + " Rows, " + String.valueOf(mColsConsidered) + " Cols remaining");
    }
    
    /**
     * Excludes the specified row from the further computation
     * @param i The row-index
     */    
    private void removeRowFromConsideration(int i) {
        mTableau.getRowHeader(i).setU(Float.NaN); mRowsConsidered--;
        int j, i2;
        float oldV;
        for (j = 1; j <= mTableau.getN(); j++) { //Check columns for changes which have to be done
            TableauColHeader colHeader = mTableau.getColHeader(j);
            if (!Float.isNaN(colHeader.getV()) && mTableau.getCell(i, j).getCost() == colHeader.getV()) { //c<sub>ij</sub> meight have been the only maximum-value in this column
                oldV = colHeader.getV();
                colHeader.setV(Float.NEGATIVE_INFINITY);
                for (i2 = 1; i2 <= mTableau.getM(); i2++) //Find the new \overline(v<sub>j</sub>)
                    if (!Float.isNaN(mTableau.getRowHeader(i2).getU()))
                        colHeader.setV(Math.max(colHeader.getV(), mTableau.getCell(i2, j).getCost()));
                if (oldV != colHeader.getV()) //Recalculate the column in the deltaMatrix if v<sub>j</sub> really changed
                    for (i2 = 1; i2 <= mTableau.getM(); i2++)
                        if (!Float.isNaN(mTableau.getRowHeader(i2).getU()))
                            mDeltaMatrix[i2 - 1][j - 1].Set(mTableau.getCell(i2, j).getCost(), mTableau.getRowHeader(i2).getU(), colHeader.getV());
            }
        }
    }
    
    /**
     * Excludes the specified column from the further computation
     * @param j The col-index
     */    
    private void removeColFromConsideration(int j) {
        mTableau.getColHeader(j).setV(Float.NaN); mColsConsidered--;
        int i, j2;
        float oldU;
        for (i = 1; i <= mTableau.getM(); i++) { //Check rows for changes which have to be done
            TableauRowHeader rowHeader = mTableau.getRowHeader(i);
            if (!Float.isNaN(rowHeader.getU()) && mTableau.getCell(i, j).getCost() == rowHeader.getU()) { //c<sub>ij</sub> meight have been the only maximum-value in this row
                oldU = rowHeader.getU();
                rowHeader.setU(Float.NEGATIVE_INFINITY);
                for (j2 = 1; j2 <= mTableau.getN(); j2++) //Find the new \overline(u<sub>i</sub>)
                    if (!Float.isNaN(mTableau.getColHeader(j2).getV()))
                        rowHeader.setU(Math.max(rowHeader.getU(), mTableau.getCell(i, j2).getCost()));
                if (oldU != rowHeader.getU()) //Recalculate the row in the deltaMatrix if u<sub>i</sub> really changed
                    for (j2 = 1; j2 <= mTableau.getN(); j2++)
                        if (!Float.isNaN(mTableau.getColHeader(j2).getV()))
                            mDeltaMatrix[i - 1][j2 - 1].Set(mTableau.getCell(i, j2).getCost(), rowHeader.getU(), mTableau.getColHeader(j2).getV());
            }
        }
    }

    /** Find the \overline(u<sub>i</sub>) and \overline(v<sub>j</sub>) and initialize the Delta Matrix */
    private void initializeDeltaMatrix() {
        int i, j;
        if (mDeltaMatrix == null || mDeltaMatrix.length != mTableau.getM() || mDeltaMatrix[0] == null || mDeltaMatrix[0].length != mTableau.getN()) {
            mDeltaMatrix = new Delta[mTableau.getM()][mTableau.getN()];
            for (i = 1; i <= mTableau.getM(); i++) 
                for (j = 1; j <= mTableau.getN(); j++) 
                    mDeltaMatrix[i - 1][j - 1] = new Delta();
        }
        
        //U and V should have been initialized with NaN!!
        TableauRowHeader rowHeader;
        TableauColHeader colHeader;
        TableauCell cell;
        mRowsConsidered = mTableau.getM();
        mColsConsidered = mTableau.getN();
        for (i = 1; i <= mTableau.getM(); i++) {
            rowHeader = mTableau.getRowHeader(i);
            rowHeader.setU(Float.NEGATIVE_INFINITY);
            for (j = 1; j <= mTableau.getN(); j++) {
                colHeader = mTableau.getColHeader(j);
                if (Float.isNaN(colHeader.getV()))
                    colHeader.setV(Float.NEGATIVE_INFINITY);
                cell = mTableau.getCell(i, j);
                rowHeader.setU(Math.max(rowHeader.getU(), cell.getCost()));
                colHeader.setV(Math.max(colHeader.getV(), cell.getCost()));
            }
        }
        for (i = 1; i <= mTableau.getM(); i++) {
            for (j = 1; j <= mTableau.getN(); j++)
                mDeltaMatrix[i - 1][j - 1].Set(mTableau.getCell(i, j).getCost(), mTableau.getRowHeader(i).getU(), mTableau.getColHeader(j).getV());
        }
    }

    /**
     * Searches for the non basic cell with the smallest delta<sub>ij</sub> (= c<sub>ij</sub> - \overline(u)<sub>i</sub> - \overline(v)<sub>j</sub>) and sets it to basic
     * @return true iff a new basic variable was found
     */    
    private boolean findBasicVariable() {
        int iMin = 0, jMin = 0, i, j;
        Delta deltaMin = null;
        if (mRowsConsidered < mColsConsidered) { //Run over Rows, to have to look at fever cols
            for (i = 1; i <= mTableau.getM(); i++) {
                if (!Float.isNaN(mTableau.getRowHeader(i).getU())) {
                    for (j = 1; j <= mTableau.getN(); j++) {
                        if (!Float.isNaN(mTableau.getColHeader(j).getV()) && (deltaMin == null || deltaMin.compareTo(mDeltaMatrix[i - 1][j - 1]) > 0)) {
                            deltaMin = mDeltaMatrix[i - 1][j - 1];
                            iMin = i; jMin = j;
                        }
                    }
                }
            }
        }
        else {
            for (j = 1; j <= mTableau.getN(); j++) {
                if (!Float.isNaN(mTableau.getColHeader(j).getV())) {
                    for (i = 1; i <= mTableau.getM(); i++) {
                        if (!Float.isNaN(mTableau.getRowHeader(i).getU()) && (deltaMin == null || deltaMin.compareTo(mDeltaMatrix[i - 1][j - 1]) > 0)) {
                            deltaMin = mDeltaMatrix[i - 1][j - 1];
                            iMin = i; jMin = j;
                        }
                    }
                }
            }
        }
        if (deltaMin == null) //Nothing found!
            return false;
        addBasicVariable(iMin, jMin);
        return true;
    }
    
    /** Creates a new instance of RusselInitialisator2 */
    public RusselInitializer2() {
        this(DL_NONE);
    }
    
    /** Creates a new instance of RusselInitialisator2 */
    public RusselInitializer2(int debugLevel) {
        mDebugLevel = debugLevel;
    }
    
    /**
     * Initializes the specified streamlined simplex tableau
     * @param tableau The tableau to initialize
     * @throws NotEnoughBasicVariablesFoundException If the algorithm terminates before enough basic variables where found
     */ 
    public void Initialize(Tableau tableau) throws NotEnoughBasicVariablesFoundException {
        mTableau = tableau;
        initializeDeltaMatrix();
        int basicVariablesFound = 0;
        while (basicVariablesFound < mTableau.getM() + mTableau.getN() - 1) {
            if (!findBasicVariable()) {
                throw new NotEnoughBasicVariablesFoundException();
            }
            basicVariablesFound++;
        };
        mTableau.calculateUV();
    }
}
