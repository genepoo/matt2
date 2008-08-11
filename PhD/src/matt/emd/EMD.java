package matt.emd;

/*
 * EMD.java
 *
 * Created on 5. Mai 2004, 17:23
 */

import java.util.*;

/**
 * This is an interface for an initilization method for streamlined simplex tableaus to solve the transportation problem.<br>
 * An implemenation of this interface should initialize the basic variables (isBasic(i, j) + x<sub>ij</sub>) <b>and</a> the u<sub>i</sub> and v<sub>j</sub> values.
 */
interface Initializer {
    /**
     * Initializes the specified streamlined simplex tableau
     * @param tableau The tableau to initialize
     * @throws NotEnoughBasicVariablesFoundException If the algorithm terminates before enugh basic variables where found
     */    
    public void Initialize(Tableau tableau) throws NotEnoughBasicVariablesFoundException;
}

/**
 * This class is the main part of the EMD. It links the input (the signatures) to an initializator and the simplex tableau.
 * @author Till Schulte-Coerne
 */
public class EMD {
    public int OverallIterCount = 0;
    
    /** The tableau to calculate on */    
    private Tableau mTableau;
    
    /** The initializer to use */    
    private Initializer mInitializer;
    
    /** The debug level to use */    
    private int mDebugLevel = 0;

    /** No debug messages */    
    public static final int DL_NONE = 0;
    
    /** Only some debug messages */    
    public static final int DL_MEDIUM = 5;
    
    /** All debug messages availiable */    
    public static final int DL_ALL = 10;
      
    
    /**
     * Searches the current tableau for the nonbasic variable with the minimal value c<sub>ij</sub> - u<sub>i</sub> - v<sub>j</sub> which is smaller than 0.
     * @return The non basic cell which would be better to use as basic variable iff it exists and null else
     */    
    private TableauCell findBetterNonBasicCell() {
        TableauCell res = null;
        int i, j;
        float diff;
        float minNonBasicDiff = Float.POSITIVE_INFINITY;
        for (i = 1; i <= mTableau.getM(); i++)
            for (j = 1; j <= mTableau.getN(); j++)
                if (!mTableau.getCell(i, j).isBasic()) {
                    diff = mTableau.getCell(i, j).getCost() - mTableau.getRowHeader(i).getU() - mTableau.getColHeader(j).getV();
                    if (minNonBasicDiff > diff) {
                        minNonBasicDiff = diff;
                        res = mTableau.getCell(i, j);
                    }
                }
        return minNonBasicDiff < -1e-6 * mTableau.getMaxCost() ? res : null;
    }
    
    /**
     * Analyzes the given iteration stack to find the donor/recipient cells and the levaing cell. Than it sets the x<sub>ij</sub> values of these cells and the isBasic value of the entering and leaving cell.
     * @param enteringCell The entering cell (not basic)
     * @param calc The iteration stack (first origin is the entering variable)
     * @throws NotEnoughBasicVariablesFoundException If the loop has the wrong length (calc.length must be even)
     */    
    private void analyzeIterationStack(TableauCell enteringCell, Stack calc) throws NotEnoughBasicVariablesFoundException {
        TableauCell cell, leavingCell = null;

        if (mDebugLevel >= DL_MEDIUM) System.out.print("Found loop (");
        for (int k = 0; k < calc.size(); k++) {
            if (mDebugLevel >= DL_MEDIUM) {
                cell = ((IterationPosition)calc.get(k)).getOriginCell();
                System.out.print("(" + String.valueOf(cell.getI()) + ", " + String.valueOf(cell.getJ()) + ")" + (k < calc.size() - 1 ? ", " : ""));
            }
            if (k % 2 != 0) { //Donor because first one was a recipient (the entering cell)
                if (leavingCell == null || leavingCell.getX() > ((IterationPosition)calc.get(k)).getOriginCell().getX())
                    leavingCell = ((IterationPosition)calc.get(k)).getOriginCell();
            }
        }
        if (mDebugLevel >= DL_MEDIUM) System.out.println(") Leaving Cell: (" + String.valueOf(leavingCell.getI()) + ",  " + String.valueOf(leavingCell.getJ()) + ") Value: " + String.valueOf(leavingCell.getX()));
        int diff = leavingCell.getX(); //We have to remember that because the x-value of the leaving cell will also be changed (to 0)
        for (int k = 0; k < calc.size(); k++) {
            cell = ((IterationPosition)calc.get(k)).getOriginCell();
            if (k % 2 != 0) //Donor because first one was a recipient (the entering cell)
                cell.setX(cell.getX() - diff);
            else //Recipiant
                cell.setX(cell.getX() + diff);
        }
        leavingCell.setBasic(false); //Do that first, therewith the properbility that Tableau.MaxBasicVariableCountPerRow is right is higher
        enteringCell.setBasic(true);
        mTableau.calculateUV(); //Last but not least recalculate the U and V values
    }
    
    private Stack calc = new Stack(); //The IterationPositions we are currently looking at
    private HashSet checked = new HashSet(); //The "Origin" cells we have already looked at
    /**
     * Finds a loop starting with the entering cell and calculates the new resulting tableau (with U and V values)
     * @param enteringCell The entering cell (not basic)
     * @throws NotEnoughBasicVariablesFoundException If no loop was found
     */    
    private void doLoop(TableauCell enteringCell) throws NotEnoughBasicVariablesFoundException {
        calc.clear();
        checked.clear();
        TableauCell cell = null;
        int i, j; 
        
        IterationPosition iter = mTableau.getColHeader(enteringCell.getJ()).getColIterationPosition();
        iter.reset(enteringCell);
        calc.push(iter); //Iterate first through the column of the entering cell

        if (mDebugLevel >= DL_ALL) System.out.println("Entering cell: " + String.valueOf(enteringCell.getI()) + "," + String.valueOf(enteringCell.getJ()));
        while (!calc.isEmpty()) { //Terminates because checked grows with every basic variable we have looked at
            cell = null;
            iter = (IterationPosition)calc.peek(); //What do we have to do today? (will be popped later if we have found no basic variables relevant for the loop)
//            if (mDebugLevel >= DL_ALL) System.out.println("Checking " + (iter.getIterateI() ? "Column " + String.valueOf(iter.getJ()) + " from Row " + String.valueOf(iter.getI()) : "Row " + String.valueOf(iter.getI()) + " from Column " + String.valueOf(iter.getJ())));
            
            while ((cell = iter.next()) != null) {
                i = cell.getI();
                j = cell.getJ();
                if (cell == enteringCell && iter.getOriginCell() != enteringCell) { //Found the entering variable [and a loop]! (and we are not in the first iteration which will trivially find the entering variabe sometimes)
                    analyzeIterationStack(enteringCell, calc);//Analyze the remaining stack to find the recipients and the donors
                    return;
                }
                else if (cell.isBasic() && !checked.contains(cell)) { //Stop iteration and iterate with the found variable as origin (Second push will bee popped immediately)
                    checked.add(cell);
                     //The next Iteration:
                    if (iter instanceof RowIterationPosition) //Iterate Colwise
                        iter = mTableau.getColHeader(cell.getJ()).getColIterationPosition();
                    else //Iterrate Rowwise
                        iter = mTableau.getRowHeader(cell.getI()).getRowIterationPosition();
		    iter.reset(cell);
                    calc.push(iter);
                    break;
                }
                else //Nothing relevant was found
                    cell = null;
            }
            if (cell == null) { //Backtrack by popping the current iteration away if nothing relevant was found
                calc.pop();
                if (mDebugLevel >= DL_ALL) System.out.println("Backtrack");
            }
        }
        //Ups, we should not be here
        throw new NotEnoughBasicVariablesFoundException();
    }
    
    /**
     * Creates a new instance of EMD
     * @param initializer The initializator for the further lifetime of this EMD instance
     * @param debugLevel The debug level to use<br>
     * <ul>
     * <li>DL_NONE
     * <li>DL_MEDIUM
     * <li>DL_ALL
     * </ul>
     */
    public EMD(Initializer initializer, int debugLevel) {
        mInitializer = initializer;
        mDebugLevel = debugLevel;
    } 
    
    /**
     * Calculates the value of the EMD for the specified signatures
     * @param signatures The signatures the EMD is to be calculated of
     * @throws NotEnoughBasicVariablesFoundException This is a exception which normally should <b>not</b> be thrown. See {@link NotEnoughBasicVariablesFoundException}
     * @return The EMD value
     */    
    public double calculate(Signatures signatures, byte normalizerExponent) throws NotEnoughBasicVariablesFoundException {
        int iterCount = 0;
        if (mTableau == null)
            mTableau = new Tableau(signatures);
        else
            mTableau.init(signatures);
        mInitializer.Initialize(mTableau);
        TableauCell betterNonBasicCell = findBetterNonBasicCell();
        while (betterNonBasicCell != null && iterCount < 200) {
            if (mDebugLevel >= DL_MEDIUM) System.out.println("Current solution not optimal");
            doLoop(betterNonBasicCell);
            betterNonBasicCell = findBetterNonBasicCell();
            iterCount++;
        }
        if (mDebugLevel >= DL_MEDIUM) System.out.println("Current solution optimal");
        OverallIterCount+= iterCount;
        return mTableau.getZ() / Math.pow(Math.max(signatures.getPWeightSum(), signatures.getQWeightSum()), normalizerExponent);
    }
}
