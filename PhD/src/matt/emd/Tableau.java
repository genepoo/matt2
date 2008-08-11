package matt.emd;

/*
 * CostMatrix.java
 *
 * Created on 28. April 2004, 14:25
 */

import java.util.*;

/**
 * Implements a abstract class to remember paused iterations.
 */    
abstract class IterationPosition {
    /** The origin cell of this iteration. This is the cell why this iteration is done.*/        
    protected TableauCell mOriginCell;

    /** The origin cell of this iteration. This is the cell why this iteration is done.*/        
    protected Tableau mTableau;

    /**
     * Creates a new instance of IterationPosition
     * @param originCell The origin cell
     */        
    public IterationPosition(Tableau tableau) {
        mTableau = tableau;
    }

    /**
     * Retruns the origin of this iteration
     * @return The cell why this iteration is done
     */        
    public TableauCell getOriginCell() {return mOriginCell;};
    
    /**
     * Starts a new Iteration
     * @param originCell The cell from which the iteration has been started
     */        
    public void reset(TableauCell originCell) {
        mOriginCell = originCell;
    }
    
    /**
     * Returns the next cell of this iteration
     * @return The cell to look at next
     */        
    public abstract TableauCell next();
}

/**
 * Implements a class to remember paused iterations over rows.
 */    
class RowIterationPosition extends IterationPosition {
    /** The RowHeader of the row beeing iterated over */
    private TableauRowHeader mTableauRowHeader;
    
    /** The Index of the col which is next */
    private int nextJ = 1;
    
    /**
     * Creates a new instance of RowIterationPosition
     * @param originCell The origin cell
     */        
    public RowIterationPosition(Tableau tableau, TableauRowHeader tableauRowHeader) {
        super(tableau);
        mTableauRowHeader = tableauRowHeader;
        nextJ = 1;
    }

    /**
     * Starts a new iteration
     * @param originCell The cell from which the iteration has been started
     */        
    public void reset(TableauCell originCell) {
        super.reset(originCell);
        nextJ = 1;
    }
    
    /**
     * Retruns the next cell of this iteration
     * @return The cell to look at next
     */        
    public TableauCell next() {
        if (nextJ <= mTableau.getN()) {
            nextJ++;
            return mTableau.getCell(mTableauRowHeader.getIndex(), nextJ-1);
        }
        else
            return null;
    }
}

/**
 * Implements a class to remember paused iterations over cols.
 */    
class ColIterationPosition extends IterationPosition {
    /** The ColHeader of the col beeing iterated over */
    private TableauColHeader mTableauColHeader;
    
    /** The Index of the row which is next */
    private int nextI = 1;
    
    /**
     * Creates a new instance of ColIterationPosition
     * @param originCell The origin cell
     */        
    public ColIterationPosition(Tableau tableau, TableauColHeader tableauColHeader) {
        super(tableau);
        mTableauColHeader = tableauColHeader;
        nextI = 1;
    }

    /**
     * Starts a new iteration
     * @param originCell The cell from which the iteration has been started
     */        
    public void reset(TableauCell originCell) {
        super.reset(originCell);
        nextI = 1;
    }
    
    /**
     * Retruns the next cell of this iteration
     * @return The cell to look at next
     */        
    public TableauCell next() {
        if (nextI <= mTableau.getM()) {
            nextI++;
            return mTableau.getCell(nextI-1, mTableauColHeader.getIndex());
        }    
        else
            return null;
    }
}

/**
 * Abstract Item in the streamlined simplex tablau
 * @author Till Schulte-Coerne
 */
abstract class TableauItem {};

/**
 * Represents a row-header in the simplex tablau including the supply beeing used by this row (after the initialization) and the beloning U-component
 */
class TableauRowHeader extends TableauItem {
    /** The supply of this row */    
    private int mSupply;
    
    /** The used supply of this row (only revelant for the initializator) */    
    private int mSupplyUsed;
    
    /** The index i of this row */    
    private int mIndex;
    
    /** The u<sub>i</sub> */    
    private float mU;
    
    /** IterationPosition for iterations over this row */
    private RowIterationPosition mRowIterationPosition;
    
    /** The count of basic variables in this row */
    public int BasicVariableCount;
    
    /**
     * Creates a new instance of TableauRowHeader with the specified supply value
     * @param index The index i of the row
     * @param supply The supply value for this row
     */    
    public TableauRowHeader(Tableau tableau, int index, int supply) {
        init(index, supply);
        mRowIterationPosition = new RowIterationPosition(tableau, this);
    }
    
    /**
     * Initializes an instance of TableauRowHeader with the specified supply value
     * @param index The index i of the row
     * @param supply The supply value for this row
     */    
    public void init(int index, int supply) {
        mU = Float.NaN;
        mIndex = index;
        mSupply = supply;
        mSupplyUsed = 0;
        BasicVariableCount = 0;
    }
    
    /**
     * Returns the supply of this row
     * @return The supply of this row
     */    
    public int getSupply() {
        return mSupply;
    }
    
    /**
     * Returns the used supply by this row (only revelant for the initializator)
     * @return The used supply by this row
     */    
    public int getSupplyUsed() {
        return mSupplyUsed;
    }
    
    /**
     * Sets the used supply by this row (only revelant for the initializator)
     * @param supplyUsed The used supply by this row
     */    
    public void setSupplyUsed(int supplyUsed) {
        mSupplyUsed = supplyUsed;
    }
    
    /**
     * Returns the U-component of this row
     * @return The U-component of this row
     */    
    public float getU() {
        return mU;
    }
    
    /**
     * Sets the U-component of this row
     * @param u The U-component of this row
     */    
    public void setU(float u) {
        mU = u;
    }    

    /**
     * Returns the index i of this row
     * @return i
     */    
    public int getIndex() {
        return mIndex;
    }

    /* Returns the iterationPosition-Object for iterations over this row */
    public RowIterationPosition getRowIterationPosition() {
        return mRowIterationPosition;
    }
}

/**
 * Represents a col-header in the simplex tableau including the demand beeing filled by this col (after the initialization) and the belonging V-component
 */
class TableauColHeader extends TableauItem {
    /** The demand of this column */    
    private int mDemand;
    
    /** The filled demand of this column (only revelant for the initializator) */    
    private int mDemandFilled;
    
    /** The index j of this column */    
    private int mIndex;
    
    /** The v<sub>j</sub> */    
    private float mV;

    /** IterationPosition for iterations over this col */
    private ColIterationPosition mColIterationPosition;
    
    /**
     * Creates a new instance of TableauColHeader with the specified demand value
     * @param index The index j of the column
     * @param demand The demand value for this column
     */    
    public TableauColHeader(Tableau tableau, int index, int demand) {
        init(index, demand);
        mColIterationPosition = new ColIterationPosition(tableau, this);
    
    }
    
    /**
     * Initializes an instance of TableauColHeader with the specified demand value
     * @param index The index i of the row
     * @param demand The demand value for this col
     */    
    public void init(int index, int demand) {
        mV = Float.NaN;
        mIndex = index;
        mDemand = demand;
        mDemandFilled = 0;
    }
    
    /**
     * Returns the demand of this col
     * @return The demand of this col
     */    
    public int getDemand() {
        return mDemand;
    }
    
    /**
     * Returns the filled demand by this col (only revelant for the initializator)
     * @return The filled demand by this col
     */    
    public int getDemandFilled() {
        return mDemandFilled;
    }
    
    /**
     * Sets the filled demand by this col (only revelant for the initializator)
     * @param demandFilled The filled demand by this col
     */    
    public void setDemandFilled(int demandFilled) {
        mDemandFilled = demandFilled;
    }
    
    /**
     * Returns the V-component of this col
     * @return The V-component of this col
     */    
    public float getV() {
        return mV;
    }
    
    /**
     * Sets the V-component of this col
     * @param v The V-component of this col
     */    
    public void setV(float v) {
        mV = v;
    }    

    /**
     * Returns the index j of this col
     * @return j
     */    
    public int getIndex() {
        return mIndex;
    }
    
    /* Returns the iterationPosition-Object for iterations over this col */
    public ColIterationPosition getColIterationPosition() {
        return mColIterationPosition;
    }
}

/**
 * Represents a Cell in the streamlined simplex tableau with its Fields:<br>
 * <ul>
 * <li>isBasic - Is this Cell a basic variable
 * <li>cost - The cost c<sup>ij</sup> for transporting the amount of 1 from s<sup>i</sup> to d<sup>j</sup>
 * <li>x - The amount transported from s<sup>i</sup> to d<sup>j</sup>
 * </ul>
 */
class TableauCell extends TableauItem {
    /** The c<sub>ij</sub> */    
    private float mCost;
    
    /** The tableau in which the cell is placed */    
    private Tableau mTableau;
    
    /** The header of the row of this cell */    
    private TableauRowHeader mRowHeader;
    
    /** The header of the column of this cell */    
    private TableauColHeader mColHeader;
    
    /** Is this cell a baisc variable? */    
    private boolean mIsBasic;
    
    /** The x<sub>ij</sub> */    
    private int mX;
    
    /**
     * Creates a new instance of TableauCell
     * @param tableau The tableau in which the cell is placed
     * @param rowHeader The header of the row of this cell
     * @param colHeader The header of the col of this cell
     * @param cost The cost c<sub>ij</sub>
     */    
    public TableauCell(Tableau tableau, TableauRowHeader rowHeader, TableauColHeader colHeader, float cost) {
        init(tableau, rowHeader, colHeader, cost);
    }
    
    /**
     * Initializes an instance of TableauCell
     * @param tableau The tableau in which the cell is placed
     * @param rowHeader The header of the row of this cell
     * @param colHeader The header of the col of this cell
     * @param cost The cost c<sub>ij</sub>
     */    
    public void init(Tableau tableau, TableauRowHeader rowHeader, TableauColHeader colHeader, float cost) {
        mTableau = tableau;
        mRowHeader = rowHeader;
        mColHeader = colHeader;
        mCost = cost;
        mIsBasic = false;
        mX = 0;
    }
    
    /**
     * Returns the cost-value c<sub>ij</sub>
     * @return The cost-value c<sub>ij</sub>
     */    
    public float getCost() {
        return mCost;
    }
    
    /**
     * Returns true iff the cell contains a basic variable
     * @return true iff the cell contains a basic variable
     */    
    public boolean isBasic() {
        return mIsBasic;
    }
    
    /**
     * Sets cell to basic or nonbasic
     * @param basic Specifies if the cell a basic or a non basic variable
     */    
    public void setBasic(boolean basic) {
        if (!mIsBasic && basic) { // If newly set to basic
            mRowHeader.BasicVariableCount++;
            if (mTableau.MaxBasicVariableCountPerRow <= mRowHeader.BasicVariableCount) { //'<=' beacuse the index can be wrong (resp. old)
                mTableau.MaxBasicVariableCountPerRow = mRowHeader.BasicVariableCount;
                mTableau.MaxBasicVariableCountPerRowIndex = mRowHeader.getIndex();
            }
        }
        else if (mIsBasic && !basic) { // If newly set to not basic
            mRowHeader.BasicVariableCount--;
            //The MaxBasicVariableCountPerRowIndex must not be set since it is only for optimation issues and relative impropable that it is considerable wrong
        }
        mIsBasic = basic;
    }
    
    /**
     * Returns x<sub>ij</sub>
     * @return The x<sub>ij</sub>
     */    
    public int getX() {
        return mX;
    }
    
    /**
     * Sets x<sub>ij</sub>
     * @param x The x<sub>ij</sub>
     */    
    public void setX(int x) {
        mX = x;
    }
    
    /**
     * Returns the row index i of the cell
     * @return The row index i
     */    
    public int getI() {
        return mRowHeader.getIndex();
    }

    /**
     * Returns the column index j of the cell
     * @return The column index j
     */    
    public int getJ() {
        return mColHeader.getIndex();
    }
}

/**
 * Represents a streamlined simplex tableau for the transportation problem
 * @author Till Schulte-Coerne
 */
public class Tableau {
    /** The internal array of tableau cells */    
    private TableauItem[] mTableau;
    
    /** The count of rows m (1 <= i <= m) */    
    private int M; 
    
    /** The count of columns n (1 <= j <= n) */    
    private int N; 
    
    /** The Maximum cost */    
    private float mMaxCost = Float.NEGATIVE_INFINITY;
    
    /**
     * The maximal count of basic variables in the row<br>
     * <b>Note:<b>
     * This value is not getting decremented and can be properbly wrong.
     */    
    protected int MaxBasicVariableCountPerRow = 0;
    
    /**
     * The index of the row with the maximal count of basic variables<br>
     * <b>Note:<b>
     * This value is based on the properbly wrong value {@link MaxBasicVariableCountPerRow} and due to that also properbly wrong.
     */    
    protected int MaxBasicVariableCountPerRowIndex = 0;

    /**
     * Creates a new instance of Tableau for the specified signatures
     * @param s The signatures to create the tableau for
     */
    public Tableau(Signatures s) {
        init(s);
    }
    
    /**
     * Initalizes a old Intance of Tableau for the specified signatures
     * @param s The signatures to create the tableau for
     */
    public void init(Signatures s) {
        M = s.getPSize();
        N = s.getQSize();
        MaxBasicVariableCountPerRow = 0; MaxBasicVariableCountPerRowIndex = 0;
        if (mTableau == null || mTableau.length != (M + 1) * (N + 1))
            mTableau = new TableauItem[(M + 1) * (N + 1)];
        for (int i = 1; i <= M; i++) {
            if (mTableau[(N + 1) * i] == null || !(mTableau[(N + 1) * i] instanceof TableauRowHeader))
                mTableau[(N + 1) * i] = new TableauRowHeader(this, i, s.getPWeight(i));
            else
                ((TableauRowHeader)mTableau[(N + 1) * i]).init(i, s.getPWeight(i));
            for (int j = 1; j <= N; j++) {
                if (mTableau[j] == null || !(mTableau[j] instanceof TableauColHeader))
                    mTableau[j] = new TableauColHeader(this, j, s.getQWeight(j));
                else
                    ((TableauColHeader)mTableau[j]).init(j, s.getQWeight(j));
                if (s.getCost(i, j) > mMaxCost)
                    mMaxCost = s.getCost(i, j);
                if (mTableau[i * (N + 1) + j] == null || !(mTableau[i * (N + 1) + j] instanceof TableauCell))
                    mTableau[i * (N + 1) + j] = new TableauCell(this, (TableauRowHeader)mTableau[(N + 1) * i], (TableauColHeader)mTableau[j], s.getCost(i, j));
                else 
                    ((TableauCell)mTableau[i * (N + 1) + j]).init(this, (TableauRowHeader)mTableau[(N + 1) * i], (TableauColHeader)mTableau[j], s.getCost(i, j));
            }
        }    
    }
    
    /**
     * Returns the row-count of the tableau
     * @return The row-count M
     */    
    public int getM() {
        return M;
    }
    
    /**
     * Returns the col-count of the tableau
     * @return The col-count N
     */    
    public int getN() {
        return N;
    }
    
    /**
     * Returns the cell i,j from the tableau
     * @param i The row-index
     * @param j The col-index
     * @return The cell from the tableau
     */    
    public TableauCell getCell(int i, int j) {
        int index = i * (N + 1) + j;
        if (i < 1 || j < 1 || index >= mTableau.length)
            throw new IndexOutOfBoundsException();
        return (TableauCell)mTableau[index];
    }
    
    /**
     * Returns the row-header from row i
     * @param i The row-index
     * @return The row-header from row i
     */    
    public TableauRowHeader getRowHeader(int i) {
        int index = i * (N + 1);
        if (i < 1 || index >= mTableau.length)
            throw new IndexOutOfBoundsException();
        return (TableauRowHeader)mTableau[index];
    }
    
    /**
     * Returns the col-header from row j
     * @param j The col-index
     * @return The col-header from row j
     */    
    public TableauColHeader getColHeader(int j) {
        int index = j;
        if (j < 1 || index >= mTableau.length)
            throw new IndexOutOfBoundsException();
        return (TableauColHeader)mTableau[index];
    }
    
    /**
     * Returns the value x<sub>11</sub>*c<sub>11</sub> + ... + x<sub>mn</sub>*c<sub>mn</sub> of the current tableau
     * @return The value x<sub>11</sub>*c<sub>11</sub> + ... + x<sub>mn</sub>*c<sub>mn</sub>
     */    
    public float getZ() {
        float res = 0;
        int i, j;
        for (i = 1; i <= M; i++) {
            for (j = 1; j <= N; j++) {
                if (getCell(i, j).isBasic()) {
                    res += getCell(i, j).getX() * getCell(i, j).getCost();
                }   
            }
        }
        return res;
    }
    
    Stack calc = new Stack(); //Stack of which rows and cols are outstanding (k > 0 means row k; k < 0 means col -k)
    /**
     * Calculates the U and V values by setting the U<sub>i</sub> with i = {@link MaxBasicVariableCountPerRowIndex} to 0 and calculating the rest of the u<sub>i</sub> and v<sub>j</sub> values recursively.
     * @throws NotEnoughBasicVariablesFoundException If not all u<sub>i</sub> or v<sub>j</sub> values where found
     */    
    public void calculateUV() throws NotEnoughBasicVariablesFoundException {
        int i, j;
        TableauCell cell;
        TableauRowHeader row;
        TableauColHeader col;
        
        calc.clear();
        
        //Initialize U and V as NaN
        for (i = 1; i <= M; i++)
            getRowHeader(i).setU(Float.NaN);
        for (j = 1; j <= N; j++)
            getColHeader(j).setV(Float.NaN);

        int firstRow = MaxBasicVariableCountPerRowIndex > 0 ? MaxBasicVariableCountPerRowIndex : 1; //The row to start with
        getRowHeader(firstRow).setU(0); //Init first u_i as 0
        int found = 1; //How may do we have found
        calc.push(new Integer(firstRow));
        while (!calc.isEmpty()) {
            int k = ((Integer)calc.pop()).intValue();
            if (k > 0) { //Search rowwise
                i = k; row = getRowHeader(i);
                for (j = 1; j <= N; j++) {
                    cell = getCell(i, j);
                    col = getColHeader(j);
                    if (cell.isBasic() && Float.isNaN(col.getV())) { //Found a not already calculated basic variable
                        col.setV(cell.getCost() - row.getU()); //v<sub>j</sub> = c<sub>ij</sub> - u<sub>i</sub>
                        calc.push(new Integer(-j)); //Do this column in the future
                        found++;
                    }
                }
            }
            else { //Search colwise
                j = -k; col = getColHeader(j);
                for (i = 1; i <= M; i++) {
                    cell = getCell(i, j);
                    row = getRowHeader(i);
                    if (cell.isBasic() && Float.isNaN(row.getU())) { //Found a not already calculated basic variable
                        row.setU(cell.getCost() - col.getV()); //u<sub>i</sub> = c<sub>ij</sub> - v<sub>j</sub>
                        calc.push(new Integer(i)); //Do this row in the future
                        found++;
                    }
                }
            }
        }
        if (found != M + N)
            throw new NotEnoughBasicVariablesFoundException();
    }
    
    /* Returns the maximum cost-value of the cost-matrix */
    public float getMaxCost() {
        return mMaxCost;
    }
}

