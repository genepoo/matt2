package matt.emd;


/*
 * NorthWestInitializer.java
 *
 * Created on 14. Juni 2004, 10:39
 */

/**
 *
 * @author  schulte-coerne
 */
public class NorthWestInitializer implements Initializer {
    
    /** Creates a new instance of NorthWestInitializer */
    public NorthWestInitializer() {
    }
    
    public void Initialize(Tableau tableau) throws NotEnoughBasicVariablesFoundException {
        int i = 1;
        int j = 1;
        while (i <= tableau.getM() && j <= tableau.getN()) {
            TableauRowHeader rowHeader = tableau.getRowHeader(i);
            TableauColHeader colHeader = tableau.getColHeader(j);
            TableauCell cell = tableau.getCell(i, j);
            int flow = Math.min(rowHeader.getSupply() - rowHeader.getSupplyUsed(), colHeader.getDemand() - colHeader.getDemandFilled());
            rowHeader.setSupplyUsed(rowHeader.getSupplyUsed() + flow);
            colHeader.setDemandFilled(colHeader.getDemandFilled() + flow);
            cell.setX(flow);
            cell.setBasic(true);
            if ((rowHeader.getSupplyUsed() == rowHeader.getSupply() && (i < tableau.getM())) || (j == tableau.getN()))
                i++;
            else 
                j++;
        }
        tableau.calculateUV();
    }
    
}
